//package SA;
//
//import HModel.Column_ian;
//import queries.QueryPicture;
//import queries.RangeQuery;
//import replicas.AckSeq;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Test {
//    public static void main(String[] args) {
//        // 数据分布参数
//        BigDecimal totalRowNumber = new BigDecimal("1000000000");
//        int ckn=10;
//
//        List<Column_ian> CKdist = new ArrayList<Column_ian>();
//        double step = 1;
//        List<Double> x = new ArrayList<Double>();
//        for(int i = 1; i<=101; i++) {
//            x.add((double)i);
//        }
//        List<Integer> y = new ArrayList<Integer>();
//        for(int i = 1; i<=100; i++) {
//            y.add(1);
//        }
//        Column_ian ck1 = new Column_ian(step, x, y);
//        Column_ian ck2 = new Column_ian(step, x, y);
//        Column_ian ck3 = new Column_ian(step, x, y);
//        Column_ian ck4 = new Column_ian(step, x, y);
//        Column_ian ck5 = new Column_ian(step, x, y);
//        Column_ian ck6 = new Column_ian(step, x, y);
//        Column_ian ck7 = new Column_ian(step, x, y);
//        Column_ian ck8 = new Column_ian(step, x, y);
//        Column_ian ck9 = new Column_ian(step, x, y);
//        Column_ian ck10 = new Column_ian(step, x, y);
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
//        // 数据存储参数
//        int rowSize = 52;
//        int fetchRowCnt = 100;
//        double costModel_k = 2.34;
//        double costModel_b = 18927.55;
//        double cost_session_around = 242.79;
//        double cost_request_around = 808.42;
//
//        // 查询参数
//        double[][] starts = new double[ckn][];
//        double[][] lengths = new double[ckn][];
//        int[] qpernum = new int[]{1000,900,800,700,600,500,300,200,100,100};
//        // 增大一个批次中的num比直接增加totalQueryBatchNum要计算的快一些，因为一个batch之内的qcki是一样的，choose用同一组随机选
//        // 但是不可以 还是要增大totalQueryBatchNum 这样才是100次随机符合分布 否则则是比如1000条重复固定参数的查询 可能刚好范围分布时选的
//        // 参数比较极端 就干扰了qpermnum作用 除非starts和length都固定 不要随机
//        //5000条
//
//        for(int i=0;i<ckn;i++){
////            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
////            lengths[i] = new double[]{0,0,0.154,0.308,0.385,0.077,0.077,0,0,0};
//            starts[i] = new double[]{0,0,0,0,0,0,0,0,0,0};
//            lengths[i] = new double[]{0,0,0,0,0,0,0,0,0,1};
//            //lengths[i] = new double[]{0,0,0,0,0,0,0,0,0,0};
////            qpernum[i] = 1;
//        }
//        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,1);
//
//        int X = 3;
//        Unify_new unify = new Unify_new(totalRowNumber,
//                ckn, CKdist,
//                rowSize,fetchRowCnt,costModel_k,costModel_b,cost_session_around,cost_request_around,
//                queryPicture,
//                X);
//        unify.isDiffReplicated = false;
//        unify.combine();
//
////        unify.calculate(new AckSeq[]{new AckSeq(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1}),
////                new AckSeq(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1}),
////                new AckSeq(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1})
////        });
//
//    }
//}
