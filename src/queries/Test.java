package queries;

import HModel.Column_ian;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        int ckn = 3;
        double[][] starts = new double[ckn][];
        double[][] lengths = new double[ckn][];
        int[] qpernum = new int[]{1,1,10};

        for(int i=0;i<ckn;i++){
            starts[i] = new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
            lengths[i] = new double[]{0,0,0.154,0.308,0.385,0.077,0.077,0,0,0};
            //lengths[i] = new double[]{0,0,0,0,0,0,0,0,0,0};
//            qpernum[i] = 1;
        }
        QueryPicture queryPicture = new QueryPicture(starts,lengths,qpernum,10);
        queryPicture.getFromDist(lengths[1]);

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
        Column_ian ck1 = new Column_ian(step, x, y);
        Column_ian ck2 = new Column_ian(step, x, y);
        Column_ian ck3 = new Column_ian(step, x, y);
        CKdist.add(ck1);
        CKdist.add(ck2);
        CKdist.add(ck3);
        List<String > sqls = new ArrayList<>();
        queryPicture.getSqls("cestbon","tb2",1,CKdist, sqls);
        for(String sql:sqls){
            System.out.println(sql);
        }

    }
}
