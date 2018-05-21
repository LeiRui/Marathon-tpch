package top;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
orders
 */
public class Test_tpch2 {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal("1500000");

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step1 = 1;
        List<Double> x1 = new ArrayList<Double>();
        for (int i = 0; i <= 6000000; i=i+50000) {
            x1.add((double) i);
        }
        List<Integer> y1 = new ArrayList<Integer>();
        for (int i = 0; i < 6000000; i=i+50000) {
            y1.add(1);
        }
        Column_ian ck1 = new Column_ian(step1, x1, y1);
        CKdist.add(ck1);

        double step2 = 1;
        List<Double> x2 = new ArrayList<Double>();
        for (int i = 0; i <= 150000; i=i+1000) {
            x2.add((double) i);
        }
        List<Integer> y2 = new ArrayList<Integer>();
        for (int i = 0; i < 150000; i=i+1000) {
            y2.add(1);
        }
        Column_ian ck2 = new Column_ian(step2, x2, y2);
        CKdist.add(ck2);


        //[246362 321103 312837 284007 203277 98551 28907 4593 350 13]
        double step3 = 0.01;
        List<Double> x3 = new ArrayList<Double>();
        for (int i = 0; i <= 560000; i=i+56000) {
            x3.add((double) i);
        }
        List<Integer> y3 = new ArrayList<Integer>();
        y3.add(246362);
        y3.add(321103);
        y3.add(312837);
        y3.add(284007);
        y3.add(203277);
        y3.add(98551);
        y3.add(28907);
        y3.add(4593);
        y3.add(350);
        y3.add(13);

        Column_ian ck3 = new Column_ian(step3, x3, y3);
        CKdist.add(ck3);

        int ckn = CKdist.size();

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[]{1,1,1};
        /*
        TODO: 当qck1的比重没有那么大的时候，无异构优化也会把ck1列放到中间，
        之前做实验的时候qperm在等比和极端之间没有过渡实验
        当DB*QB时，DB应该是会倾向于把ck1放前面，QB应该是会倾向于把ck1放后面，
        当QB极端到73:1:1:....时候，QB的作用就很强，优化结果ck1排在最后，
        但是稍微调整，让ck1的qperm从1开始慢慢增加，中间大概是可以出现ck1从前往后移动的过程，
        异构的时候大概也会出现一个把ck1排在最前面，一个把ck1排在最后面的情况了
        （当qperm太极端时，两副本异构优化结果还是会把两个副本的ck1都排在最后）
         */


        //谨慎修改列内分布参数
        for (int i = 0; i < ckn; i++) {
            starts[i] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
            lengths[i] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
        }
        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 167);//167*3=501个

        //控制变量
        int X = Constant.RF;

        Unify_new_fast unify = new Unify_new_fast(totalRowNumber,
                ckn, CKdist,
                Constant.rowSize, Constant.fetchRowCnt, Constant.costModel_k, Constant.costModel_b, Constant.cost_session_around, Constant.cost_request_around,
                queryPicture,
                X);
        unify.isDiffReplicated = Constant.isDiffReplica;
        unify.combine();

    }
}
