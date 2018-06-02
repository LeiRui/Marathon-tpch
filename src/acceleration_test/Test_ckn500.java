package acceleration_test;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test_ckn500 {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal(Constant.dataNum);

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step = 1;
        List<Double> x = new ArrayList<Double>();
        for(int i = 1; i<=101; i++) {
            x.add((double)i);
        }
        List<Double> y = new ArrayList<Double>();
        for(int i = 1; i<=100; i++) {
            y.add(1.0);
        }

        // ckn change
        for(int i=0;i<200;i++) {
            Column_ian ck1 = new Column_ian(step, x, y);
            CKdist.add(ck1);
        }

        int ckn=CKdist.size();

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[ckn];
        for(int i=0;i<ckn;i++) {
            qpernum[i] = 1;
        }

        for(int i=0;i<ckn;i++){
            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
            lengths[i] = new double[]{0.08,0.2,0.28,0.16,0.12,0.04,0.04,0.04,0.04,0};
        }
        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,15);//10*50=500个查询

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
