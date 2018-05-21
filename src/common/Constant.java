package common;

public class Constant {
//    public static String dataNum = "1600000";
//    public static String dataNum = "8000000";
//    public static String dataNum = "40000000";
//    public static String dataNum = "1000000000";
    public static String dataNum = "100000000";
//    public static String dataNum = "200000000";
//    public static String dataNum = "1000000000";


//    public static String[] ckname = new String[]{"c1","c2","c4"}; // TODO!!
//    public static boolean[] isInt = new boolean[]{true,true,false}; // TODO!!
    public static String[] ckname = new String[]{"ck1","ck2","ck3","ck4","ck5","ck6","ck7","ck8","ck9","ck10"}; // TODO!!
    public static boolean[] isInt = new boolean[]{true,true,true,true,true,true,true,true,true,true}; // TODO!!

    public static boolean isGetSqls = true; // TODO 记得改！！!!!!

    public static boolean isDiffReplica = true; // TODO 记得改这个！！！
//    public static boolean isDiffReplica   =  false; // TODO 记得改这个！！！

    public static String ks = "exp_ckn";
    //    public static String[] cf = new String[]{"defaulttable","defaulttable","defaulttable"};
//    public static String[] cf = new String[]{"sameopt","sameopt","sameopt"};
//    public static String[] cf = new String[]{"df1","df2","df3"};

    public static int RF = 1; // TODO 记得改这个！！！！！！！！！！！！！！

    public static String[] cf = new String[]{"sameopt6", "sameopt6", "sameopt6"};
//    public static String[] cf = new String[]{"df6_1", "df6_2", "df6_3"};
    public static int[] pkey = new int[]{4,4,4};

    //    public static String[] cf = new String[]{"df5_1","df5_2","df5_3","df5_4","df5_5"};
//    public static String[] cf = new String[]{"sameopt","sameopt","sameopt","sameopt","sameopt"};
//    public static String[] cf = new String[]{"defaulttable","defaulttable","defaulttable","defaulttable","defaulttable"};

    public static String SArecord = "SA_process_record.csv";

    public static boolean isAccelerate = true;

    public static boolean isPrint = true;

    public static boolean isRecordProcess = true;

    // 数据存储参数
    public static int rowSize = 100;

    //单查询代价模型参数
    public static int fetchRowCnt = 100;
    public static double costModel_k = 2.024;
    public static double costModel_b = 16862.946;
    public static double cost_session_around = 242.793;
    public static double cost_request_around = 808.423;
}
