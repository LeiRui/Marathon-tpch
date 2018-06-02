package exp621;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test_new {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal(Constant.dataNum);

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step1 = 1;
        List<Double> x1 = new ArrayList<Double>();
        for(int i = 1; i<=11; i++) {
            x1.add((double)i);
        }
        List<Double> y1 = new ArrayList<Double>();
        for(int i = 1; i<=10; i++) {
            y1.add(1.0);
        }
        for(int i=0;i<2;i++) {
            Column_ian ck1 = new Column_ian(step1, x1, y1);
            CKdist.add(ck1);
        }
        double step2 = 1;
        List<Double> x2 = new ArrayList<Double>();
        for(int i = 1; i<=100001; i++) {
            x2.add((double)i);
        }
        List<Double> y2 = new ArrayList<Double>();
        for(int i = 1; i<=100000; i++) {
            y2.add(1.0);
        }
        Column_ian ck2 = new Column_ian(step2, x2, y2);
        CKdist.add(ck2);
        int ckn=CKdist.size();

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
//        int[] qpernum = new int[]{21,12,9,5,4,3,2,2,1,1};
//        int[] qpernum = new int[]{21,12,9,5,4};
//        int[] qpernum = new int[]{50,50,50,50,100};
        int[] qpernum = new int[]{50,50,50};
        for(int i=0;i<ckn;i++){
            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
            lengths[i] = new double[]{0.08,0.2,0.28,0.16,0.12,0.04,0.04,0.04,0.04,0};
        }
        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,1);//2*50=100个查询

        int X = 3;
        Unify_new_fast unify = new Unify_new_fast(totalRowNumber,
                ckn, CKdist,
                Constant.rowSize,Constant.fetchRowCnt,Constant.costModel_k,Constant.costModel_b,Constant.cost_session_around,Constant.cost_request_around,
                queryPicture,
                X);
        unify.isDiffReplicated = Constant.isDiffReplica;
        unify.combine();
    }
}
