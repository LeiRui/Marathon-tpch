package queries;

import HModel.Column_ian;

import java.util.ArrayList;
import java.util.List;

/*
 查询集的描述参数类
 */
public class QueryPicture {
    // 排序键个数，也即单列范围查询不同列数量
    public int ckn;
    // 各列单范围查询起点分布 10%间隔直方图统计概率分布
    public double[][] starts; // [ckn][10]
    // 各列单范围查询长度分布 10%间隔直方图统计概率分布
    public double[][] lengths; // [ckn][10]
    // 单查询列之间的比重分布 以一批次内执行各几次为描述方式
    public int[] qpernum; // [ckn]
    // 单查询对点查列视为均匀随机取值吧

    public int totalQueryBatchNum;
    public int totalQueryNum;

    public QueryPicture(double[][] starts, double[][] lengths, int[] qpernum, int totalQueryBatchNum) {
        this.starts = starts;
        this.lengths = lengths;
        this.qpernum = qpernum;
        this.totalQueryBatchNum = totalQueryBatchNum;
        ckn = qpernum.length;
        int sum = 0;
        for(int i=0;i<qpernum.length;i++) {
            sum+=qpernum[i];
        }
        totalQueryNum = sum*totalQueryBatchNum;
    }

    // 输入各列真实分布参数、总查询批次，输出符合描述参数分布的模拟确定查询参数集合
    public List<RangeQuery[]> getDefinite(List<Column_ian> CKdist) {
        List<RangeQuery[]> res = new ArrayList<>();
        for(int i=0;i<ckn;i++) {
            RangeQuery[] res_ = new RangeQuery[totalQueryBatchNum*qpernum[i]];
            res.add(res_);
        }

        for (int i = 0; i < ckn; i++) {
            Column_ian columnIan = CKdist.get(i);
            double[] dist_start = starts[i];
            double[] dist_length = lengths[i];
            RangeQuery[] res_ = res.get(i);
            int singleSum = res_.length;
            for (int j = 0; j < singleSum; j++) {
                // 确定这个batch内第i个ck列的单范围查询的描述参数数值
                int qck_r1_abs = (int)Math.round(getFromDist(dist_start) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
                int qck_r2_abs = (int)Math.round(qck_r1_abs + getFromDist(dist_length) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);

                int[] qck_p_abs = new int[ckn];
                for (int z = 0; z < ckn; z++) {// 单查询对点查列视为均匀随机取值吧
                    qck_p_abs[z] = (int)Math.round(Math.random() * (CKdist.get(z).xmax_ - CKdist.get(z).xmin_) + CKdist.get(z).xmin_);
                    if(qck_p_abs[z] >= CKdist.get(z).xmax_) {
                        qck_p_abs[z] = (int)CKdist.get(z).xmax_-1;
                    }
                    else if(qck_p_abs[z]<CKdist.get(z).xmin_) {
                        qck_p_abs[z] = (int)CKdist.get(z).xmin_;
                    }
                    // TODO round是为了sql时int点查安全起见 否则直接检查不存在返回了
                }
                res_[j] = new RangeQuery(i+1, qck_r1_abs, qck_r2_abs, true, true, qck_p_abs);
            }
        }
        return res;
    }
    public static String getSql(String ks,String cf,int pkey, List<Column_ian> CKdist, RangeQuery q) {
        String q_format = "select count(*) from "+ks+"."+cf+" where pkey="+pkey;
        int ckn = CKdist.size();
        for(int k=0;k<ckn;k++) {
            if(k==q.qckn-1) { // qckn从1开始
                String tmp = " and ck%d>=%d and ck%d<=%d";
                q_format+=String.format(tmp, k+1,q.qck_r1_abs,k+1,q.qck_r2_abs);
            }
            else {
                q_format+=" and ck"+(k+1)+"="+(int)q.qck_p_abs[k];
            }
        }
        q_format+=" allow filtering;";
        return q_format;
    }

    public RangeQuery[][] getSqls(String ks, String cf, int pkey, List<Column_ian> CKdist, List<String>sqls) {
        RangeQuery[][] res = new RangeQuery[totalQueryBatchNum][ckn]; //TODO 一个batch内查询参数就先用同一个重复若干次吧
        for (int i = 0; i < totalQueryBatchNum; i++) {
            res[i] = new RangeQuery[ckn];
        }
        for (int i = 0; i < ckn; i++) {
            Column_ian columnIan = CKdist.get(i);
            double[] dist_start = starts[i];
            double[] dist_length = lengths[i];
            for (int j = 0; j < totalQueryBatchNum; j++) {
                // 确定这个batch内第i个ck列的单范围查询的描述参数数值
                int qck_r1_abs = (int)Math.round(getFromDist(dist_start) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
                int qck_r2_abs = (int)Math.round(qck_r1_abs + getFromDist(dist_length) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
                int[] qck_p_abs = new int[ckn];
                for (int z = 0; z < ckn; z++) {// 单查询对点查列视为均匀随机取值吧
                    qck_p_abs[z] = (int)Math.round(Math.random() * (CKdist.get(z).xmax_ - CKdist.get(z).xmin_) + CKdist.get(z).xmin_);
                    // TODO round是为了sql时int点查安全起见 否则直接检查不存在返回了
                    if(qck_p_abs[z] >= CKdist.get(z).xmax_) {
                        qck_p_abs[z] = (int)CKdist.get(z).xmax_-1;
                    }
                    else if(qck_p_abs[z]<CKdist.get(z).xmin_) {
                        qck_p_abs[z] = (int)CKdist.get(z).xmin_;
                    }
                }
                res[j][i] = new RangeQuery(i, qck_r1_abs, qck_r2_abs, true, true, qck_p_abs);
                String q_format = "select * from "+ks+"."+cf+" where pkey="+pkey;
                for(int k=0;k<ckn;k++) {
                    if(k==i) {
                        String tmp = " and ck%d>=%.2f and ck%d<=%.2f";
                        q_format+=String.format(tmp, k+1,qck_r1_abs,k+1,qck_r2_abs);
                    }
                    else {
                        q_format+=" and ck"+(k+1)+"="+(int)qck_p_abs[k];
                    }
                }
                q_format+=" allow filtering;";
                for(int c=0;c<qpernum[i];c++) {// 单查询列之间的比重分布 以一批次内执行各几次为描述方式
                    sqls.add(q_format);
                }
            }
        }
        return res;

    }

    /**
     * @param dist 直方统计形式 10%间隔的概率分布
     * @return 符合分布概率的一个值
     */
    public double getFromDist(double[] dist) {
        double res = 0;
        double random = Math.random();
        double sum = 0;
        for (int i = 0; i < dist.length; i++) {
            sum += dist[i];
            if (random <= sum) { // i%~(i+1)%
                res = (double) (i + Math.random()) / 10;
                break;
            }
        }
        return res;
    }
}
