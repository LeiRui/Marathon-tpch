package common;

import java.math.BigDecimal;

public class Constant {
    public static boolean isTranform =true; // 专门为tpch的c5和c7的从数字转换到真实的查询准备的

    public static String dataNum = "1500000";
//    public static String dataNum = "3000000";
//    public static String dataNum = "4500000";
//    public static String dataNum = "6000000";
//    public static String dataNum = "7500000";

    public static boolean isGetSqls = true;

    public static String[] ckname = new String[]{"c2","c5","c7"}; // TODO!!
    public static boolean[] isInt = new boolean[]{true, true, true}; // TODO!!

    public static int RF = 3;

    public static String ks = "tpch_ds1_loose";
//    public static String ks = "tpch_ds2";
//    public static String ks = "tpch_ds3";
//    public static String ks = "tpch_ds4";
//    public static String ks = "tpch_ds5";

    public static boolean isDiffReplica = true; // TODO 记得改这个！！！
//        public static boolean isDiffReplica = false;

//    public static String[] cf = new String[]{"df1", "df2", "df3"};
        public static String[] cf = new String[]{"sameopt", "sameopt", "sameopt"};
//        public static String[] cf = new String[]{"defaulttable", "defaulttable", "defaulttable"};
//    public static int[] pkey = new int[]{2, 3, 4};
    public static int[] pkey = new int[]{1, 1, 1};
//    public static int[] pkey = new int[]{13,13,13};


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
