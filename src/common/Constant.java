package common;

public class Constant {
    public static String dataNum = "1000000000";
    public static boolean isDiffReplica   =  true; // TODO 记得改这个！！！
//    public static String ks = "Wilson_diff_1b";
    public static String ks = "pearson_xxx";
//    public static String[] cf = new String[]{"defaulttable","defaulttable","defaulttable"};
//    public static String[] cf = new String[]{"sameopt","sameopt","sameopt"};
    public static String[] cf = new String[]{"df1","df2","df3"};
    public static int[] pkey = new int[]{3,4,13};
    public static String SArecord = "SA_process_record.csv";

    public static boolean isRecordProcess = true;

    // 数据存储参数
    public static int rowSize = 52;
    public static int fetchRowCnt = 100;
    public static double costModel_k = 2.34;
    public static double costModel_b = 18927.55;
    public static double cost_session_around = 242.79;
    public static double cost_request_around = 808.42;
}
