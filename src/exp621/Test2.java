//package exp621;
//
//import HModel.Column_ian;
//import SA.Unify_new_fast;
//import common.Constant;
//import queries.QueryPicture;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
///*
// 人造数据分布A下改变数据量实验
// */
//public class Test2 {
//    public static void main(String[] args) {
//        // 数据分布参数
//        BigDecimal totalRowNumber = new BigDecimal(Constant.dataNum);
//
//        List<Column_ian> CKdist = new ArrayList<Column_ian>();
//        double step1 = 1;
//        List<Double> x1 = new ArrayList<Double>();
//        for(int i = 1; i<=11; i++) {
//            x1.add((double)i);
//        }
//        List<Double> y1 = new ArrayList<Double>();
//        for(int i = 1; i<=10; i++) {
//            y1.add(1);
//        }
//        Column_ian ck1 = new Column_ian(step1, x1, y1);
//
//        double step2 = 1;
//        List<Double> x2 = new ArrayList<Double>();
//        for(int i = 1; i<=21; i++) {
//            x2.add((double)i);
//        }
//        List<Integer> y2 = new ArrayList<Integer>();
//        for(int i = 1; i<=20; i++) {
//            y2.add(1);
//        }
//        Column_ian ck2 = new Column_ian(step2, x2, y2);
//
//        double step3 = 1;
//        List<Double> x3 = new ArrayList<Double>();
//        for(int i = 1; i<=31; i++) {
//            x3.add((double)i);
//        }
//        List<Integer> y3 = new ArrayList<Integer>();
//        for(int i = 1; i<=30; i++) {
//            y3.add(1);
//        }
//        Column_ian ck3 = new Column_ian(step3, x3, y3);
//
//        double step4 = 1;
//        List<Double> x4 = new ArrayList<Double>();
//        for(int i = 1; i<=41; i++) {
//            x4.add((double)i);
//        }
//        List<Integer> y4 = new ArrayList<Integer>();
//        for(int i = 1; i<=40; i++) {
//            y4.add(1);
//        }
//        Column_ian ck4 = new Column_ian(step4, x4, y4);
//
//        double step5 = 1;
//        List<Double> x5 = new ArrayList<Double>();
//        for(int i = 1; i<=51; i++) {
//            x5.add((double)i);
//        }
//        List<Integer> y5 = new ArrayList<Integer>();
//        for(int i = 1; i<=50; i++) {
//            y5.add(1);
//        }
//        Column_ian ck5 = new Column_ian(step5, x5, y5);
//
//        double step6 = 1;
//        List<Double> x6 = new ArrayList<Double>();
//        for(int i = 1; i<=61; i++) {
//            x6.add((double)i);
//        }
//        List<Integer> y6 = new ArrayList<Integer>();
//        for(int i = 1; i<=60; i++) {
//            y6.add(1);
//        }
//        Column_ian ck6 = new Column_ian(step6, x6, y6);
//
//        double step7 = 1;
//        List<Double> x7 = new ArrayList<Double>();
//        for(int i = 1; i<=71; i++) {
//            x7.add((double)i);
//        }
//        List<Integer> y7 = new ArrayList<Integer>();
//        for(int i = 1; i<=70; i++) {
//            y7.add(1);
//        }
//        Column_ian ck7 = new Column_ian(step7, x7, y7);
//
//        double step8 = 1;
//        List<Double> x8 = new ArrayList<Double>();
//        for(int i = 1; i<=71; i++) {
//            x8.add((double)i);
//        }
//        List<Integer> y8 = new ArrayList<Integer>();
//        for(int i = 1; i<=70; i++) {
//            y8.add(1);
//        }
//        Column_ian ck8 = new Column_ian(step8, x8, y8);
//
//        double step9 = 1;
//        List<Double> x9 = new ArrayList<Double>();
//        for(int i = 1; i<=71; i++) {
//            x9.add((double)i);
//        }
//        List<Integer> y9 = new ArrayList<Integer>();
//        for(int i = 1; i<=70; i++) {
//            y9.add(1);
//        }
//        Column_ian ck9 = new Column_ian(step9, x9, y9);
//
//        double step10 = 1;
//        List<Double> x10 = new ArrayList<Double>();
//        for(int i = 1; i<=71; i++) {
//            x10.add((double)i);
//        }
//        List<Integer> y10 = new ArrayList<Integer>();
//        for(int i = 1; i<=70; i++) {
//            y10.add(1);
//        }
//        Column_ian ck10 = new Column_ian(step10, x10, y10);
//
//        CKdist.add(ck1);
//        CKdist.add(ck2);
//        CKdist.add(ck3);
//        CKdist.add(ck4);
//        CKdist.add(ck5);
//        CKdist.add(ck6);
//        CKdist.add(ck7);
//        CKdist.add(ck8);
//        CKdist.add(ck9);
//        CKdist.add(ck10);
//
//        int ckn=CKdist.size();
//
//        // 查询参数
//        double[][] starts = new double[ckn][];
//        double[][] lengths = new double[ckn][];
//        int[] qpernum = new int[]{21,12,9,5,4,3,2,2,1,1};
//
//        for(int i=0;i<ckn;i++){
//            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
//            lengths[i] = new double[]{0.08,0.2,0.28,0.16,0.12,0.04,0.04,0.04,0.04,0};
//        }
//        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,15);//15*60=900个查询
//
//        int X = 3;
//        Unify_new_fast unify = new Unify_new_fast(totalRowNumber,
//                ckn, CKdist,
//                Constant.rowSize,Constant.fetchRowCnt,Constant.costModel_k,Constant.costModel_b,Constant.cost_session_around,Constant.cost_request_around,
//                queryPicture,
//                X);
//        unify.isDiffReplicated = Constant.isDiffReplica;
//        unify.combine();
//    }
//}
