package exp621_new;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
 人造数据分布A下改变数据量实验
 */
public class Test {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal(Constant.dataNum);

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step = 1;
        List<Double> x = new ArrayList<Double>();
        for (int i = 1; i <= 15; i++) {
            x.add((double) i);
        }
        List<Double> y = new ArrayList<Double>();
        for (int i = 1; i <= 14; i++) {
            y.add(1.0);
        }
        Column_ian ck1 = new Column_ian(step, x, y);
        Column_ian ck2 = new Column_ian(step, x, y);
        Column_ian ck3 = new Column_ian(step, x, y);
        Column_ian ck4 = new Column_ian(step, x, y);
        Column_ian ck5 = new Column_ian(step, x, y);
        Column_ian ck6 = new Column_ian(step, x, y);
        Column_ian ck7 = new Column_ian(step, x, y);
        Column_ian ck8 = new Column_ian(step, x, y);
        CKdist.add(ck1);
        CKdist.add(ck2);
        CKdist.add(ck3);
        CKdist.add(ck4);
        CKdist.add(ck5);
        CKdist.add(ck6);
        CKdist.add(ck7);
        CKdist.add(ck8);

        int ckn=CKdist.size();

        // 数据存储参数
        int rowSize = Constant.rowSize;
        int fetchRowCnt = Constant.fetchRowCnt;
        double costModel_k = Constant.costModel_k;
        double costModel_b = Constant.costModel_b;
        double cost_session_around = Constant.cost_session_around;
        double cost_request_around = Constant.cost_request_around;

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[]{10,10,10,10,10,10,10,10};

        for(int i=0;i<ckn;i++){
            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
            lengths[i] = new double[]{0.08,0.2,0.28,0.16,0.12,0.04,0.04,0.04,0.04,0};
        }
        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,2);//80*2=160条查询

        int X = 3;
        Unify_new_fast unify = new Unify_new_fast(totalRowNumber,
                ckn, CKdist,
                rowSize, fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around,
                queryPicture,
                X);

        unify.isDiffReplicated = Constant.isDiffReplica;
        unify.combine();
    }
}
