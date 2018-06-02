package queries;

import HModel.Column_ian;
import common.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        for (int i = 0; i < qpernum.length; i++) {
            sum += qpernum[i];
        }
        totalQueryNum = sum * totalQueryBatchNum;
    }

    // 输入各列真实分布参数、总查询批次，输出符合描述参数分布的模拟确定查询参数集合
    public List<RangeQuery[]> getDefinite(List<Column_ian> CKdist) {
        List<RangeQuery[]> res = new ArrayList<>();
        for (int i = 0; i < ckn; i++) {
            RangeQuery[] res_ = new RangeQuery[totalQueryBatchNum * qpernum[i]];
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
                double qck_r1_abs = (int) Math.round(getFromDist(dist_start) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
//                double qck_r2_abs = (int)Math.round(qck_r1_abs + getFromDist(dist_length) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
                double qck_r2_abs = (int) Math.round(qck_r1_abs + getFromDist(dist_length) * (columnIan.xmax_ - columnIan.xmin_));
//                double qck_r2_abs;
//                if(i!=1) {
//                    qck_r2_abs = (int) Math.round(qck_r1_abs + getFromDist(dist_length) * (columnIan.xmax_ - columnIan.xmin_));
//                }
//                else {
//                    qck_r2_abs = 20500;
//                }
//                double qck_r2_abs = columnIan.xmax_;
//                TODO 记得修改这个bug
// System.out.println("r1="+qck_r1_abs+" r2="+qck_r2_abs);

                //TODO 记得改！
//                double qck_r1_abs =-1;
//                double qck_r2_abs = 5.5;

//                int qck_r2_abs = 3163;
//                int qck_r2_abs = 216;
//                int qck_r2_abs = 57;
//                int qck_r2_abs = 26;
//                int qck_r2_abs = 16;
//                int qck_r2_abs = 11;
//                int qck_r2_abs = 9;

                // NOTE 这里有可能qck_r1_abs~qck_r2_abs超出范围


                double[] qck_p_abs = new double[ckn];
                for (int z = 0; z < ckn; z++) {// 单查询对点查列视为均匀随机取值吧
//                    qck_p_abs[z] = (int)Math.round(Math.random() * (CKdist.get(z).xmax_ - CKdist.get(z).xmin_) + CKdist.get(z).xmin_);
//                    if(qck_p_abs[z] >= CKdist.get(z).xmax_) {
//                        qck_p_abs[z] = (int)CKdist.get(z).xmax_-1;
//                    }
//                    else if(qck_p_abs[z]<CKdist.get(z).xmin_) {
//                        qck_p_abs[z] = (int)CKdist.get(z).xmin_;
//                    }
                    // TODO 修改点查询的点值生成方式，尽量保证点差值有意义
                    Random random = new Random();
                    int totalStep = (int) ((CKdist.get(z).xmax_ - CKdist.get(z).xmin_) / CKdist.get(z).step_);
                    int posStep = random.nextInt(totalStep);
                    if (posStep == 0) {
                        posStep = 1;
                    }
                    qck_p_abs[z] = CKdist.get(z).xmin_ + posStep * CKdist.get(z).step_;
                    // TODO round是为了sql时int点查安全起见 否则直接检查不存在返回了
                }
                res_[j] = new RangeQuery(i + 1, qck_r1_abs, qck_r2_abs, true, true, qck_p_abs);
            }
        }
        return res;
    }

    public static String getSql(String ks, String cf, int pkey, List<Column_ian> CKdist, RangeQuery q) {
        String q_format = "select count(*) from " + ks + "." + cf + " where pkey=" + pkey;
        int ckn = CKdist.size();
        for (int k = 0; k < ckn; k++) {
            if (k == q.qckn - 1) { // qckn从1开始
//                String tmp = " and ck%d>=%d and ck%d<=%d";
//                q_format+=String.format(tmp, k+1,q.qck_r1_abs,k+1,q.qck_r2_abs);
                if (Constant.isInt[k]) {// int
                    if (Constant.isTranform) {
                        if (Constant.ckname[k].equals("c2")) {
                            String tmp = " and %s>=%d and %s<=%d";
                            q_format += String.format(tmp, Constant.ckname[k], (int) q.qck_r1_abs, Constant.ckname[k], (int) q.qck_r2_abs);
                        } else if (Constant.ckname[k].equals("c5")) { // 转换成时间
                            long val1 = (long) q.qck_r1_abs * 24 * 3600 * 1000;
                            Date date1 = new Date(val1);
                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                            String dateText1 = df1.format(date1);

                            long val2 = (long) q.qck_r2_abs * 24 * 3600 * 1000;
                            Date date2 = new Date(val2);
                            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                            String dateText2 = df2.format(date2);

                            String tmp = " and %s>='%s' and %s<='%s'";
                            q_format += String.format(tmp, Constant.ckname[k], dateText1, Constant.ckname[k], dateText2);
                        } else { // c7 转换成Clerk#...共15-byte
                            String number = Integer.toString((int) q.qck_r1_abs);
                            int zeroNum = 15 - 6 - number.length();
                            String str = "Clerk#";
                            for (int i = 0; i < zeroNum; i++) {
                                str += "0";
                            }
                            str += number;

                            String number2 = Integer.toString((int) q.qck_r2_abs);
                            int zeroNum2 = 15 - 6 - number2.length();
                            String str2 = "Clerk#";
                            for (int i = 0; i < zeroNum2; i++) {
                                str2 += "0";
                            }
                            str2 += number2;

                            String tmp = " and %s>='%s' and %s<='%s'";
                            q_format += String.format(tmp, Constant.ckname[k], str, Constant.ckname[k], str2);
                        }
                    } else {
                        String tmp = " and %s>=%d and %s<=%d";
                        q_format += String.format(tmp, Constant.ckname[k], (int) q.qck_r1_abs, Constant.ckname[k], (int) q.qck_r2_abs);
                    }
                } else {
                    String tmp = " and %s>=%.2f and %s<=%.2f";
                    q_format += String.format(tmp, Constant.ckname[k], q.qck_r1_abs, Constant.ckname[k], q.qck_r2_abs);
                }
            } else {
//                q_format+=" and ck"+(k+1)+"="+(int)q.qck_p_abs[k];
                if (Constant.isInt[k]) {
                    if (Constant.isTranform) {
                        if (Constant.ckname[k].equals("c2")) {
                            String tmp = " and %s=%d";
                            q_format += String.format(tmp, Constant.ckname[k], (int) q.qck_p_abs[k]);
                        } else if (Constant.ckname[k].equals("c5")) { // 转换成时间
                            long val1 = (long) q.qck_p_abs[k] * 24 * 3600 * 1000;
                            Date date1 = new Date(val1);
                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                            String dateText1 = df1.format(date1);

                            String tmp = " and %s='%s'";
                            q_format += String.format(tmp, Constant.ckname[k], dateText1);
                        } else { // c7 转换成Clerk#...共15-byte
                            String number = Integer.toString((int) q.qck_p_abs[k]);
                            int zeroNum = 15 - 6 - number.length();
                            String str = "Clerk#";
                            for (int i = 0; i < zeroNum; i++) {
                                str += "0";
                            }
                            str += number;

                            String tmp = " and %s='%s'";
                            q_format += String.format(tmp, Constant.ckname[k], str);
                        }
                    } else {
                        q_format += " and " + Constant.ckname[k] + "=" + (int) q.qck_p_abs[k];
                    }
                } else {
                    String tmp = " and %s=%.2f";
                    q_format += String.format(tmp, Constant.ckname[k], q.qck_p_abs[k]);
                }
            }
        }
        q_format += " allow filtering;";
        return q_format;
    }

    @Deprecated
    public RangeQuery[][] getSqls(String ks, String cf, int pkey, List<Column_ian> CKdist, List<String> sqls) {
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
                int qck_r1_abs = (int) Math.round(getFromDist(dist_start) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
                int qck_r2_abs = (int) Math.round(qck_r1_abs + getFromDist(dist_length) * (columnIan.xmax_ - columnIan.xmin_) + columnIan.xmin_);
                double[] qck_p_abs = new double[ckn];
                for (int z = 0; z < ckn; z++) {// 单查询对点查列视为均匀随机取值吧
//                    qck_p_abs[z] = (int)Math.round(Math.random() * (CKdist.get(z).xmax_ - CKdist.get(z).xmin_) + CKdist.get(z).xmin_);
//                    // TODO round是为了sql时int点查安全起见 否则直接检查不存在返回了
//                    if(qck_p_abs[z] >= CKdist.get(z).xmax_) {
//                        qck_p_abs[z] = (int)CKdist.get(z).xmax_-1;
//                    }
//                    else if(qck_p_abs[z]<CKdist.get(z).xmin_) {
//                        qck_p_abs[z] = (int)CKdist.get(z).xmin_;
//                    }
                    Random random = new Random();
                    int totalStep = (int) ((CKdist.get(z).xmax_ - CKdist.get(z).xmin_) / CKdist.get(z).step_);
                    int posStep = random.nextInt(totalStep);
                    if (posStep == 0) {
                        posStep = 1;
                    }
                    qck_p_abs[z] = CKdist.get(z).xmin_ + posStep * CKdist.get(z).step_;
                }
                res[j][i] = new RangeQuery(i, qck_r1_abs, qck_r2_abs, true, true, qck_p_abs);
                String q_format = "select * from " + ks + "." + cf + " where pkey=" + pkey;
                for (int k = 0; k < ckn; k++) {
                    if (k == i) {
                        String tmp = " and ck%d>=%.2f and ck%d<=%.2f";
                        q_format += String.format(tmp, k + 1, qck_r1_abs, k + 1, qck_r2_abs);
                    } else {
                        q_format += " and ck" + (k + 1) + "=" + (int) qck_p_abs[k];
                    }
                }
                q_format += " allow filtering;";
                for (int c = 0; c < qpernum[i]; c++) {// 单查询列之间的比重分布 以一批次内执行各几次为描述方式
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
