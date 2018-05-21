package top;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
lineitem
 */
public class Test_tpch {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal("6001215");

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step1 = 30000;
        List<Double> x1 = new ArrayList<Double>();
        for (int i = 0; i <= 6000000; i=i+30000) {
            x1.add((double) i);
        }
        List<Integer> y1 = new ArrayList<Integer>();
        for (int i = 0; i < 6000000; i=i+30000) {
            y1.add(1);
        }
        Column_ian ck1 = new Column_ian(step1, x1, y1);
        CKdist.add(ck1);

        double step2 = 1000;
        List<Double> x2 = new ArrayList<Double>();
        for (int i = 0; i <= 200000; i=i+1000) {
            x2.add((double) i);
        }
        List<Integer> y2 = new ArrayList<Integer>();
        for (int i = 0; i < 200000; i=i+1000) {
            y2.add(1);
        }
        Column_ian ck2 = new Column_ian(step2, x2, y2);
        CKdist.add(ck2);

        double step3 = 50;
        List<Double> x3 = new ArrayList<Double>();
        for (int i = 0; i <= 10000; i=i+50) {
            x3.add((double) i);
        }
        List<Integer> y3 = new ArrayList<Integer>();
        for (int i = 0; i < 10000; i=i+50) {
            y3.add(1);
        }
        Column_ian ck3 = new Column_ian(step3, x3, y3);
        CKdist.add(ck3);

        double step4 = 0.01;
        List<Double> x4 = new ArrayList<Double>();
        for (int i = 1; i <= 8; i=i+1) {
            x4.add((double) i);
        }
        List<Integer> y4 = new ArrayList<Integer>();
        y4.add(1500000);
        y4.add(1285828);
        y4.add(1071394);
        y4.add(857015);
        y4.add(643287);
        y4.add(429070);
        y4.add(214621);
        Column_ian ck4 = new Column_ian(step4, x4, y4);
        CKdist.add(ck4);

        double step5 = 1;
        List<Double> x5 = new ArrayList<Double>();
        for (double i = 1; i <= 51; i=i+1) {
            x5.add(i);
        }
        List<Integer> y5 = new ArrayList<Integer>();
        for (double i = 1; i < 51; i=i+1) {
            y5.add(1);
        }
        Column_ian ck5 = new Column_ian(step5, x5, y5);
        CKdist.add(ck5);

        /*
        Values: [854463 917755 916971 918126 882019 678507 456446 267616 102603 6709]
        BinEdges: [0 11000 22000 33000 44000 55000 66000 77000 88000 99000 110000]
         */
        double step6 = 0.5;//TODO
        List<Double> x6 = new ArrayList<Double>();
        for (double i = 0; i <= 110000; i=i+11000) {
            x6.add(i);
        }
        List<Integer> y6 = new ArrayList<Integer>();
        y6.add(854463);
        y6.add(917755);
        y6.add(916971);
        y6.add(918126);
        y6.add(882019);
        y6.add(678507);
        y6.add(456446);
        y6.add(267616);
        y6.add(102603);
        y6.add(6709);
        Column_ian ck6 = new Column_ian(step6, x6, y6);
        CKdist.add(ck6);

        double step7 = 0.01;
        List<Double> x7 = new ArrayList<Double>();
        for (double i = 0; i <= 0.1; i=i+0.01) {
            x7.add(i);
        }
        List<Integer> y7 = new ArrayList<Integer>();
        for (double i = 0; i < 0.1; i=i+0.01) {
            y7.add(1);
        }
        Column_ian ck7 = new Column_ian(step7, x7, y7);
        CKdist.add(ck7);

        //[665254 666678 667458 667175 666908 665972 667463 1334307]
        double step8 = 0.01;
        List<Double> x8 = new ArrayList<Double>();
        for (double i = 0; i <= 0.08; i=i+0.01) {
            x8.add(i);
        }
        List<Integer> y8 = new ArrayList<Integer>();
        for (double i = 0; i < 0.08; i=i+0.01) {
            y8.add(1);
        }
        Column_ian ck8 = new Column_ian(step8, x8, y8);
        CKdist.add(ck8);

        int ckn = CKdist.size();

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[]{1,1,1,1,1,1,1,1};
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
        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 63);//63*8=504个

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
