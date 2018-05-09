package SA;

import HModel.Column_ian;
import common.Constant;
import queries.QueryPicture;
import replicas.AckSeq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test_calculate {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal("1000000000");
        int ckn = 10;

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step = 1;
        List<Double> x = new ArrayList<Double>();
        for (int i = 1; i <= 101; i++) {
            x.add((double) i);
        }
        List<Integer> y = new ArrayList<Integer>();
        for (int i = 1; i <= 100; i++) {
            y.add(1);
        }
        Column_ian ck1 = new Column_ian(step, x, y);
        Column_ian ck2 = new Column_ian(step, x, y);
        Column_ian ck3 = new Column_ian(step, x, y);
        Column_ian ck4 = new Column_ian(step, x, y);
        Column_ian ck5 = new Column_ian(step, x, y);
        Column_ian ck6 = new Column_ian(step, x, y);
        Column_ian ck7 = new Column_ian(step, x, y);
        Column_ian ck8 = new Column_ian(step, x, y);
        Column_ian ck9 = new Column_ian(step, x, y);
        Column_ian ck10 = new Column_ian(step, x, y);
        CKdist.add(ck1);
        CKdist.add(ck2);
        CKdist.add(ck3);
        CKdist.add(ck4);
        CKdist.add(ck5);
        CKdist.add(ck6);
        CKdist.add(ck7);
        CKdist.add(ck8);
        CKdist.add(ck9);
        CKdist.add(ck10);

        // 数据存储参数
        int rowSize = 52;
        int fetchRowCnt = 100;
        double costModel_k = 2.34;
        double costModel_b = 18927.55;
        double cost_session_around = 242.79;
        double cost_request_around = 808.42;

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[]{10, 10, 8, 8, 4, 4, 2, 2, 1, 1}; // 增大一个批次中的num比直接增加totalQueryBatchNum要计算的快一些，因为一个batch之内的qcki是一样的，choose用同一组随机选
//        int[] qpernum = new int[]{30,0,0,0,0,0,0,0,0,0}; // 增大一个批次中的num比直接增加totalQueryBatchNum要计算的快一些，因为一个batch之内的qcki是一样的，choose用同一组随机选

        for (int i = 0; i < ckn; i++) {
            starts[i] = new double[]{0,0,0,0,0,0,0,0,0,0};
            lengths[i] = new double[]{0,0,0,0,0,0,0,0,0,1};
//            starts[i] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
//            lengths[i] = new double[]{0, 0, 0.154, 0.308, 0.385, 0.077, 0.077, 0, 0, 0};
            //lengths[i] = new double[]{0,0,0,0,0,0,0,0,0,0};
//            qpernum[i] = 1;
        }
        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 10);

        int X = 3;
        Unify unify = new Unify(totalRowNumber,
                ckn, CKdist,
                rowSize, fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around,
                queryPicture,
                X);
        unify.isDiffReplicated = false;
//        unify.combine();
        unify.calculate(new AckSeq[]{new AckSeq(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1}),
                new AckSeq(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1}),
                new AckSeq(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1})
        });
        List<String> r1=new ArrayList<>();
        List<String> r2=new ArrayList<>();
        List<String> r3=new ArrayList<>();
        for(int i=0;i<queryPicture.totalQueryBatchNum;i++){
            for(int k=0;k<ckn;k++){
                for(int j=0;j<queryPicture.qpernum[k];j++){
                    int direct = unify.qChooseX[i][k][j];
                    String sql = queryPicture.getSql(Constant.ks, Constant.cf[direct],Constant.pkey,
                            CKdist,unify.rangeQueries[i][k]);
                    if(direct==0) {
                        r1.add(sql);
                    }
                    if(direct==1) {
                        r2.add(sql);
                    }
                    if(direct==2) {
                        r3.add(sql);
                    }
                    //ystem.out.println(sql);
                }
            }
        }
        System.out.println("R1");
        System.out.println(r1.size());
        for(String sql:r1) {
            System.out.println(sql);
        }
        System.out.println("R2");
        System.out.println(r2.size());
        for(String sql:r2) {
            System.out.println(sql);
        }
        System.out.println("R3");
        System.out.println(r3.size());
        for(String sql:r3) {
            System.out.println(sql);
        }

    }
}
