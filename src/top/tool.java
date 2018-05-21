package top;

/*
turn ck4-ck3-ck5-ck8-ck1-ck6-ck2-ck7 to ck4 int,...
 */
public class tool {
    public static void main(String[] args) {
        String str = "ck4-ck3-ck5-ck8-ck1-ck6-ck2-ck7";
        String[] split = str.split("-");
        String res="";
        for(String s:split) {
            res = res+s+" int,";
        }
        System.out.println(res);
        String res2="PRIMARY KEY(pkey,";
        for(int i=0;i<split.length-1;i++) {
            res2 = res2+split[i]+",";
        }
        res2+=split[split.length-1]+")";
        System.out.println(res2);

    }
}
