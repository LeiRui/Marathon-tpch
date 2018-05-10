package Man1_1;

import HModel.Column_ian;
import SA.Unify_new_fast;
import SA.Unify_new_fast;
import common.Constant;
import queries.QueryPicture;
import replicas.AckSeq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
157.95s vs 441.45
 */
public class Test_calculate{
        public static void main(String[] args) {
            // 数据分布参数
            BigDecimal totalRowNumber = new BigDecimal("40000000");

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

            int ckn=CKdist.size();

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
            int[] qpernum = new int[]{21,12,9,5,4,3,2,2,1,1};

            for(int i=0;i<ckn;i++){
                starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
                lengths[i] = new double[]{0.08,0.2,0.28,0.16,0.12,0.04,0.04,0.04,0.04,0};
            }
            QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,15);

            int X = 3;
            Unify_new_fast unify_new = new Unify_new_fast(totalRowNumber,
                    ckn, CKdist,
                    rowSize, fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around,
                    queryPicture,
                    X);

//            unify_new.isDiffReplicated = false;
//            unify_new.calculate_unit(new AckSeq[]{new AckSeq(new int[]{6,3,7,10,9,4,5,2,1,8}),
//                    new AckSeq(new int[]{6,3,7,10,9,4,5,2,1,8}),
//                    new AckSeq(new int[]{6,3,7,10,9,4,5,2,1,8})
//            });

            //Cost:6.53 s| R1[ck6,ck4,ck5,ck8,ck7,ck1,ck2,ck3,ck9,ck10]:6.53 s,R2[ck2,ck9,ck6,ck8,ck4,ck1,ck10,ck7,ck5,ck3]:6.03 s,R3[ck9,ck3,ck2,ck8,ck6,ck1,ck7,ck4,ck5,ck10]:5.73 s,

            unify_new.isDiffReplicated = true;
            unify_new.calculate(new AckSeq[]{new AckSeq(new int[]{6,7,8,9,2,5,1,4,3,10}),
                    new AckSeq(new int[]{6,9,5,7,1,2,4,8,10,3}), // 10,5,6,2,8,3,1,4,7,9
                    new AckSeq(new int[]{5,4,1,7,10,9,8,6,3,2}) //9,8,3,4,1,7,10,6,5,2
            });
            unify_new.pos_1 = new int[]{0,3,2};
            unify_new.pos_2 = new int[]{0,5,9};
            unify_new.calculate_remember(new AckSeq[]{new AckSeq(new int[]{6,7,8,9,2,5,1,4,3,10}),
                    new AckSeq(new int[]{6,9,5,2,7,1,4,8,10,3}), // 10,5,6,2,8,3,1,4,7,9
                    new AckSeq(new int[]{5,4,2,7,10,9,8,6,3,1}) //9,8,3,4,1,7,10,6,5,2
            });

            /*
            Cost:8.97 s| R1[ck6,ck7,ck8,ck9,ck2,ck5,ck1,ck4,ck3,ck10]:8.97 s,
            R2[ck6,ck9,ck5,ck7,ck1,ck2,ck4,ck8,ck10,ck3]:2.62 s,
            R3[ck5,ck4,ck1,ck7,ck10,ck9,ck8,ck6,ck3,ck2]:6.39 s,
新状态比现在状态差
维持当前状态不变
Cost:6.23 s| R1[ck6,ck7,ck8,ck9,ck2,ck5,ck1,ck4,ck3,ck10]:5.71 s,
R2[ck6,ck9,ck5,ck2,ck7,ck1,ck4,ck8,ck10,ck3]:6.03 s,
R3[ck5,ck4,ck2,ck7,ck10,ck9,ck8,ck6,ck3,ck1]:6.23 s,
新状态不比现在状态差
             */



            //5,8,6,9,2,10,3,1,4,7

            //R1[ck7,ck10,ck1,ck3,ck2,ck5,ck8,ck4,ck6,ck9]:1.74 s,
            // R2[ck8,ck10,ck3,ck7,ck4,ck2,ck5,ck9,ck1,ck6]:10.51 s,R3[ck1,ck7,ck10,ck8,ck5,ck2,ck9,ck4,ck3,ck6]:5.80 s,

            //Cost:85.02 s| R1[ck8,ck1,ck9,ck3,ck6,ck5,ck7,ck10,ck4,ck2]:5.67 s,R2[ck7,ck2,ck5,ck6,ck8,ck9,ck1,ck4,ck10,ck3]:85.02 s,
            // R3[ck9,ck3,ck2,ck7,ck4,ck8,ck1,ck5,ck10,ck6]:5.71 s,

//            unify_new.isDiffReplicated = false;
//            unify_new.calculate_unit(new AckSeq[]{new AckSeq(new int[]{10,9,8,7,6,5,4,3,2,1}),
//                    new AckSeq(new int[]{10,9,8,7,6,5,4,3,2,1}), // 10,5,6,2,8,3,1,4,7,9
//                    new AckSeq(new int[]{10,9,8,7,6,5,4,3,2,1}) //9,8,3,4,1,7,10,6,5,2
//            });//5,8,6,9,2,10,3,1,4,7
//            unify_new.calculate_unit(new AckSeq[]{new AckSeq(new int[]{9,10,8,7,6,5,4,2,3,1}),
//                    new AckSeq(new int[]{9,10,8,7,6,5,4,2,3,1}), // 10,5,6,2,8,3,1,4,7,9
//                    new AckSeq(new int[]{9,10,8,7,6,5,4,2,3,1}) //9,8,3,4,1,7,10,6,5,2
//            });//5,8,6,9,2,10,3,1,4,7


        }
}
