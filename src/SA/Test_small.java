package SA;

import HModel.Column_ian;
import common.Constant;
import queries.QueryPicture;
import replicas.AckSeq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test_small {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal("1000000");
        int ckn=3;

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step = 1;
        List<Double> x = new ArrayList<Double>();
        for(int i = 1; i<=101; i++) {
            x.add((double)i);
        }
        List<Integer> y = new ArrayList<Integer>();
        for(int i = 1; i<=100; i++) {
            y.add(1);
        }
        Column_ian ck1 = new Column_ian(step, x, y);
        Column_ian ck2 = new Column_ian(step, x, y);
        Column_ian ck3 = new Column_ian(step, x, y);
        CKdist.add(ck1);
        CKdist.add(ck2);
        CKdist.add(ck3);


        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[]{5,1,1};
        //5000条

        for(int i=0;i<ckn;i++){
//            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
//            lengths[i] = new double[]{0,0,0.154,0.308,0.385,0.077,0.077,0,0,0};
            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
            lengths[i] = new double[]{0,0,0.154,0.308,0.385,0.077,0.077,0,0,0};
            //lengths[i] = new double[]{0,0,0,0,0,0,0,0,0,0};
//            qpernum[i] = 1;
        }
        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,100);

        int X = 3;
        Unify unify = new Unify(totalRowNumber,
                ckn, CKdist,
                Constant.rowSize,Constant.fetchRowCnt,Constant.costModel_k,Constant.costModel_b,Constant.cost_session_around,Constant.cost_request_around,
                queryPicture,
                X);
        unify.isDiffReplicated = true;
        unify.combine();

//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{1,2,3}),
//                new AckSeq(new int[]{1,2,3}),
//                new AckSeq(new int[]{1,2,3})});
    }
}
