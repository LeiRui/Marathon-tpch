package top;

import HModel.Column_ian;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test_ckn2 {
    public static void main(String[] args) {
        // 数据分布参数
        BigDecimal totalRowNumber = new BigDecimal("9765625"); // 25^5 min

        List<Column_ian> CKdist = new ArrayList<Column_ian>();
        double step1 = 1;
        List<Double> x1 = new ArrayList<Double>();
//        for (int i = 1; i <= 3163; i++) {
//        for (int i = 1; i <= 216; i++) {
//        for (int i = 1; i <= 57; i++) {
//        for (int i = 1; i <= 26; i++) {
        for (int i = 1; i <= 16; i++) {
//        for (int i = 1; i <= 11; i++) {
//        for (int i = 1; i <= 9; i++) {
            x1.add((double) i);
        }
        List<Integer> y1 = new ArrayList<Integer>();
//        for (int i = 1; i <= 3162; i++) {
//        for (int i = 1; i <= 215; i++) {
//        for (int i = 1; i <= 56; i++) {
//        for (int i = 1; i <= 25; i++) {
        for (int i = 1; i <= 15; i++) {
//        for (int i = 1; i <= 10; i++) {
//        for (int i = 1; i <= 8; i++) {
            y1.add(1);
        }
//        for (int i = 0; i < 2; i++) {
//        for (int i = 0; i < 3; i++) {
//        for (int i = 0; i < 4; i++) {
//        for (int i = 0; i < 5; i++) {
        for (int i = 0; i < 6; i++) {
//        for (int i = 0; i < 7; i++) {
//        for (int i = 0; i < 8; i++) {
            Column_ian ck1 = new Column_ian(step1, x1, y1);
            CKdist.add(ck1);
        }

        int ckn = CKdist.size();

        // 查询参数
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[ckn];
        for (int i = 0; i < ckn; i++) {
            qpernum[i] = 1;
        }

        //谨慎修改列内分布参数
        for (int i = 0; i < ckn; i++) {
//            starts[i] = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
//            lengths[i] = new double[]{0.08, 0.2, 0.28, 0.16, 0.12, 0.04, 0.04, 0.04, 0.04, 0};
            starts[i] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            lengths[i] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        }
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 420);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 280);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 210);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 168);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 140);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 120);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 105);
        //应该是控制查询结果行数不变
        // ckn=2: <840*3162行 改成2*?*3162=2*5*3162=31620
        // ckn=3: <840*215行     3*?*215=3*49*215=31605
        // ckn=4: <840*56行      4*?*56=4*141*56=31584
        // ckn=5: =840*25行      5*?*25=5*253*25=31625
        // ckn=6: <840*15行      6*?*15=6*351*15=31590
        // ckn=7: <840*10行      7*?*10=7*452*10=31640
        // ckn=8: <840*8行       8*?*8=8*494*8=31616
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 5);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 49);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 141);
//        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 253);
        QueryPicture queryPicture = new QueryPicture(starts, lengths, qpernum, 351);



        //840=2*420
        //840=3*280
        //840=4*210
        //840=5*168
        //840=6*140
        //840=7*120
        //840=8*105


        //固定三副本
        int X = 3;

        Unify_new_fast unify = new Unify_new_fast(totalRowNumber,
                ckn, CKdist,
                Constant.rowSize, Constant.fetchRowCnt, Constant.costModel_k, Constant.costModel_b, Constant.cost_session_around, Constant.cost_request_around,
                queryPicture,
                X);
        unify.isDiffReplicated = true;
//        unify.isDiffReplicated = false;
        unify.combine();

    }
}
