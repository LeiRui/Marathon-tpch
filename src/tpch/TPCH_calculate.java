package tpch;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;
import replicas.AckSeq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TPCH_calculate {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal(Constant.dataNum);

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step1 = 1;
        List<Double> x1 = new ArrayList<Double>();
        for (int i = 1; i <= 150001; i=i+1000) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 300001; i=i+1000) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 450001; i=i+1000) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 600001; i=i+1000) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 750001; i = i + 1000) { //TODO:这个不同datasize要改的！！！！
            x1.add((double) i);
        }
        List<Double> y1 = new ArrayList<Double>();
        for (int i = 1; i < 150001; i=i+1000) {
//        for (int i = 1; i < 300001; i=i+1000) {
//        for (int i = 1; i < 450001; i=i+1000) {
//        for (int i = 1; i < 600001; i=i+1000) {
//        for (int i = 1; i < 750001; i = i + 1000) {
            y1.add(1.0);
        }
        Column_ian ck1 = new Column_ian(step1, x1, y1);
        CKdist.add(ck1);

        /*
           Data: [1500000×1 int32]
           Values: [134208 155018 155115 156089 156308 156414 155841 156230 156245 118532]
          NumBins: 10
         BinEdges: [8000 8250 8500 8750 9000 9250 9500 9750 10000 10250 10500]
         BinWidth: 250
        BinLimits: [8000 10500]
    Normalization: 'count'
        FaceColor: 'auto'
        EdgeColor: [0 0 0]
         */
        double step2 = 1;
        List<Double> x2 = new ArrayList<Double>();
        for (int i = 8000; i <= 10500; i = i + 250) {
            x2.add((double) i);
        }
        List<Double> y2 = new ArrayList<Double>();
        y2.add(134.208);
        y2.add(155.018);
        y2.add(155.115);
        y2.add(156.089);
        y2.add(156.308);
        y2.add(156.414);
        y2.add(155.841);
        y2.add(156.230);
        y2.add(156.245);
        y2.add(118.532);
        Column_ian ck2 = new Column_ian(step2, x2, y2);
        CKdist.add(ck2);

        double step3 = 1;
        List<Double> x3 = new ArrayList<Double>();
        for (int i = 1; i <= 1001; i=i+10) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 2001; i=i+10) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 3001; i=i+10) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 4001; i=i+10) { //TODO:这个不同datasize要改的！！！！
//        for (int i = 1; i <= 5001; i = i + 10) { //TODO:这个不同datasize要改的！！！！
            x3.add((double) i);
        }
        List<Double> y3 = new ArrayList<Double>();
        for (int i = 0; i < 100; i++) {
//        for (int i = 0; i < 200; i++) {
//        for (int i = 0; i < 300; i++) {
//        for (int i = 0; i < 400; i++) {
//        for (int i = 0; i < 500; i++) {
            y3.add(1.0);
        }
        Column_ian ck3 = new Column_ian(step3, x3, y3);
        CKdist.add(ck3);

        int ckn = CKdist.size();

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
//        int[] qpernum = new int[]{1, 1, 1};
        int[] qpernum = new int[]{0, 1, 0};

        //谨慎修改列内分布参数
//        for (int i = 0; i < ckn; i++) {
//            starts[i] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
//            lengths[i] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
//        }

        starts[0] = new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        lengths[0] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}; // c2顾客全范围查询

        starts[1] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
//        lengths[1] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1}; // c5时间随机
        lengths[1] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}; // c2顾客全范围查询


        starts[2] = new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        lengths[2] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}; // c7员工全范围查询

//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 167);//167*3=501个
        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 167);//167*3=501个

        int X = 3;

        Unify_new_fast unify = new Unify_new_fast(totalRowNumber,
                ckn, CKdist,
                Constant.rowSize, Constant.fetchRowCnt, 2.04979, 17242.52183, Constant.cost_session_around, Constant.cost_request_around,
                queryPicture,
                X);
//        unify.isDiffReplicated = true;
//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{1,2,3}),
//                new AckSeq(new int[]{3,1,2}),
//                new AckSeq(new int[]{2,3,1})
//        });

        unify.isDiffReplicated = false;
        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{2,3,1}),
                new AckSeq(new int[]{2,3,1}),
                new AckSeq(new int[]{2,3,1})
        });

//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{1,2,3}),
//                new AckSeq(new int[]{1,2,3}),
//                new AckSeq(new int[]{1,2,3})
//        });
//
//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{3,2,1}),
//                new AckSeq(new int[]{3,2,1}),
//                new AckSeq(new int[]{3,2,1})
//        });

//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{2,1,3}),
//                new AckSeq(new int[]{2,1,3}),
//                new AckSeq(new int[]{2,1,3})
//        });
//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{2,3,1}),
//                new AckSeq(new int[]{2,3,1}),
//                new AckSeq(new int[]{2,3,1})
//        });


//        unify.isDiffReplicated = false;
//
//        //R1[ck2-ck1-ck3]:3.06 s,
//        //R2[ck3-ck1-ck2]:3.06 s,
//        //R3[ck2-ck3-ck1]:3.06 s,
//        unify.calculate_unit(new AckSeq[]{new AckSeq(new int[]{2,3,1}),
//                new AckSeq(new int[]{2,3,1}),
//                new AckSeq(new int[]{2,3,1})
//        });

    }
}
