package SA;

import HModel.Column_ian;
import HModel.H_ian;
import common.Constant;
import queries.QueryPicture;
import queries.RangeQuery;
import replicas.AckSeq;
import replicas.XAckSeq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

public class Unify_new_fast{
    public int margin_us = 0; // 查询路由采取cost最小原则 模糊限margin 单位us

    public boolean isDiffReplicated = false; // 统一无异构优化数据存储结构和异构
    public int noDiffChooseIndex = 0;

    // 数据分布参数
    private BigDecimal totalRowNumber;
    private int ckn;
    private List<Column_ian> CKdist;
    // 数据存储参数
    private int rowSize;// unit: byte
    private int fetchRowCnt; // 工程实践设置的分批一次取结果最大行数
    private double costModel_k; // merged cost: f(x)=k*x+b
    private double costModel_b; // merged cost: f(x)=k*x+b
    private double cost_session_around; // Cost的其余组成部分  unit:us
    private double cost_request_around; // Cost的其余组成部分  unit:us

    // 查询参数
    QueryPicture queryPicture; // 描述参数
    public List<RangeQuery[]> rangeQueries; // 符合描述分布的确定一组查询集合,依次是对cki列的范围查询数组
    public List<double[][]> recordCostBoard; //记忆性计算cost中间结果保存在这里
    List<int[]> qChooseX; // 记录单查询计算过程中的路由结果 [qbatch][ckn][qper]
    List<PrintWriter> pws; // X个副本分流的sqls最终结果记录

    // SA
    // X个异构副本组成一个状态解
    // 分流策略：简单的代价最小原则
    public double Cost; // 某个状态解的代价评价：查询按照分流策略分流之后累计的查询代价
    public double Cost_best;// SA每次内圈得到新状态之后维护的最优状态值
    public double Cost_best_bigloop; // SA外圈循环记录每次外圈退温时记忆中保持的最优状态值
    public Set<XAckSeq> ackSeq_best_step;//记忆性 SA维护的最优状态解集


    public int X; // 给定的异构副本数量

    public int[] pos_1; // 产生新状态的变化范围起点
    public int[] pos_2; // 产生新状态的变化范围起点

    public Unify_new_fast(BigDecimal totalRowNumber, int ckn, List<Column_ian>CKdist,
                     int rowSize, int fetchRowCnt, double costModel_k, double costModel_b, double cost_session_around, double cost_request_around,
                     QueryPicture queryPicture,
                     int X) {
        this.totalRowNumber = totalRowNumber;
        this.ckn = ckn;
        this.CKdist = CKdist;

        this.rowSize = rowSize;
        this.fetchRowCnt = fetchRowCnt;
        this.costModel_k = costModel_k;
        this.costModel_b = costModel_b;
        this.cost_session_around = cost_session_around;
        this.cost_request_around = cost_request_around;

        this.queryPicture = queryPicture;
        rangeQueries = queryPicture.getDefinite(CKdist);
        recordCostBoard = new ArrayList<>();
        qChooseX = new ArrayList<>();
        for(int i=0;i<ckn;i++){
            int qckinum = queryPicture.totalQueryBatchNum*queryPicture.qpernum[i];
            double[][] qckiCost = new double[qckinum][];
            for(int j=0;j<qckinum;j++) {
                qckiCost[j] = new double[X]; // X个副本
            }
            recordCostBoard.add(qckiCost);

            int[] choose = new int[qckinum];
            qChooseX.add(choose);
        }

        this.ackSeq_best_step = new HashSet();
        this.X = X;
        pws = new ArrayList<>();

        pos_1 = new int[X];
        pos_2 = new int[X];

    }

//    /**
//     * 关键的状态评价函数
//     *
//     * 输入：一个状态,X个异构副本组成一个状态解
//     * 分流策略：最小Cost
//     * 状态目标函数值：TotalCost = max(sum Cost)
//     * 输出：输入状态的目标函数值TotalCost
//     *
//     * @param xackSeq X个异构副本组成一个状态解
//     */
//    public void calculate(AckSeq[] xackSeq) {
//        double[] XCostload = new double[X]; // 用于后面取max(sum HB)
//        for(int i=0;i<X;i++) {
//            XCostload[i] = 0;
//        }
//        for(int k=0;k<ckn;k++) { // 遍历一个batch内所有列范围查询 TODO 增量式可以在这里缩小范围
//            RangeQuery[] singleQueries = rangeQueries.get(k); // qcki下所有查询
//            int[] singleChoose = qChooseX.get(k); // qcki下所有查询的最终路由选择记录
//            for(int i=0;i<singleQueries.length;i++) {
//                RangeQuery q = singleQueries[i];
//
//                double[] recordCost = new double[X]; // 单查询在X个副本上的代价
//                List<Integer> chooseX = new ArrayList(); // 记录中间可选路由结果，代价一样的副本平分负载
//                chooseX.add(0);
//                H_ian h = new H_ian(totalRowNumber, ckn, CKdist,
//                        q.qckn, q.qck_r1_abs, q.qck_r2_abs, q.r1_closed, q.r2_closed, q.qck_p_abs,
//                        xackSeq[0].ackSeq);
//                double chooseCost = h.calculate(fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around);
//
//                if (isDiffReplicated) {
//                    recordCost[0] = chooseCost;
//                    double tmpCost;
//                    for (int j = 1; j < X; j++) { // 遍历X个副本，按照最小HB原则对q分流
//                        h = new H_ian(totalRowNumber, ckn, CKdist,
//                                q.qckn, q.qck_r1_abs, q.qck_r2_abs, q.r1_closed, q.r2_closed, q.qck_p_abs,
//                                xackSeq[j].ackSeq);
//                        tmpCost = h.calculate(fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around);
//                        recordCost[j] = tmpCost; // 记录
//                        double res = tmpCost-chooseCost;
//                        if (res < 0) {
//                            chooseCost = tmpCost; // note 引用
//                            chooseX.clear();
//                            chooseX.add(j);
//                        } else if (res == 0) {
//                            chooseX.add(j);
//                        }
//                    }//X个副本遍历结束，现在已经确定了这个query按照精确最小Cost原则分流到的副本chooseX，以及这个最小Cost等于多少
//                    List<Double> chooseCostList = new ArrayList<Double>();
//                    for (int c = 0; c < chooseX.size(); c++) {
//                        chooseCostList.add(chooseCost);
//                    }
//                    for (int j = 0; j < X; j++) { //重新遍历一遍，对分流模糊一层
//                        if (chooseX.contains(j)) {
//                            continue;
//                        }
//                        if (recordCost[j]-chooseCost-margin_us < 0) {
//                            chooseX.add(j);
//                            chooseCostList.add(recordCost[j]);
//                        }
//                    }
//                    // 得到扩展版的chooseX&chooseCostList, 接下来更新XCostload
//                    int chooseNumber = chooseX.size();
//                    Random random = new Random();
//                    int pos = random.nextInt(chooseNumber);
//                    int ultimateChoose = chooseX.get(pos); // NOTE! chooseX.get不能少
//                    singleChoose[i] = ultimateChoose;
//                    XCostload[ultimateChoose] += chooseCostList.get(pos);
//                } else {
//                    if (noDiffChooseIndex >= X) {
//                        noDiffChooseIndex = 0;
//                    }
//                    int ultimateChoose = noDiffChooseIndex; //算法负责查询路由时要做到均匀分 真的均分很重要 因为这里无异构时代价计算会收到均衡的影响
//                    noDiffChooseIndex++;
//
//                    singleChoose[i] = ultimateChoose;
//                    XCostload[ultimateChoose] += chooseCost;
//                }
//            }
//        }
//
//        // X个结点副本cost load中的最大值
//        if(isDiffReplicated) {// X个结点副本cost load中的最大值
//            List<Integer> maxC = new ArrayList<Integer>();
//            Cost = XCostload[0];
//            maxC.add(0);
//            for (int i = 1; i < X; i++) {
//                double res = XCostload[i]-Cost;
//                if (res > 0) {
//                    maxC.clear();
//                    maxC.add(i);
//                    Cost = XCostload[i];
//                } else if (res == 0) {
//                    maxC.add(i);
//                }
//            }
//        }
//        else { // 同构时为了减小算法中随机均分还是有的不均对结果的干扰，采用求和平均，重要，因为之前代价相等副本随机均分时的不均取max还是有很大影响
//            Cost = 0;
//            for(int i=0;i<XCostload.length;i++){
//                Cost+=XCostload[i];
//            }
//            Cost /= X;
//        }
//
//        //打印结果
//        System.out.print(String.format("Cost:%.2f s| ",Cost));
//        for(int i=0;i<X;i++) {
//            System.out.print(String.format("R%d%s:%.2f s,",i+1,xackSeq[i],XCostload[i])); // 用查询执行耗时作为load负载评价
//        }
//        System.out.println("");
//    }

    /**
     * 关键的状态评价函数 recordCostBoard全盘重新计算
     *
     * 输入：一个状态,X个异构副本组成一个状态解
     * 分流策略：最小Cost
     * 状态目标函数值：TotalCost = max(sum Cost)
     * 输出：输入状态的目标函数值TotalCost
     *
     * @param xackSeq X个异构副本组成一个状态解
     */
    public void calculate(AckSeq[] xackSeq) {
        double[] XCostload = new double[X]; // 用于后面取max(sum HB)
        for(int i=0;i<X;i++) {
            XCostload[i] = 0;
        }
        for(int k=0;k<ckn;k++) { // 遍历一个batch内所有列范围查询 TODO 增量式可以在这里缩小范围
            RangeQuery[] singleQueries = rangeQueries.get(k); // qcki下所有查询
            int[] singleChoose = qChooseX.get(k); // qcki下所有查询的最终路由选择记录
            for(int i=0;i<singleQueries.length;i++) {
                RangeQuery q = singleQueries[i];
                List<Integer> chooseX = new ArrayList(); // 记录中间可选路由结果，代价一样的副本平分负载
                chooseX.add(0);
                H_ian h = new H_ian(totalRowNumber, ckn, CKdist,
                        q.qckn, q.qck_r1_abs, q.qck_r2_abs, q.r1_closed, q.r2_closed, q.qck_p_abs,
                        xackSeq[0].ackSeq);
                double chooseCost = h.calculate(fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around);
                if (isDiffReplicated) {
                    recordCostBoard.get(k)[i][0]=chooseCost;
                    double tmpCost;
                    for (int j = 1; j < X; j++) { // 遍历X个副本，按照最小HB原则对q分流
                        h = new H_ian(totalRowNumber, ckn, CKdist,
                                q.qckn, q.qck_r1_abs, q.qck_r2_abs, q.r1_closed, q.r2_closed, q.qck_p_abs,
                                xackSeq[j].ackSeq);
                        tmpCost = h.calculate(fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around);
                        recordCostBoard.get(k)[i][j]=tmpCost;
                        double res = tmpCost-chooseCost;
                        if (res < 0) {
                            chooseCost = tmpCost; // note 引用
                            chooseX.clear();
                            chooseX.add(j);
                        } else if (res == 0) {
                            chooseX.add(j);
                        }
                    }//X个副本遍历结束，现在已经确定了这个query按照精确最小Cost原则分流到的副本chooseX，以及这个最小Cost等于多少
                    //即已确定recordCostBoard.get(k)[i]的所有X个cost值

//                    List<Double> chooseCostList = new ArrayList<Double>();
//                    for (int c = 0; c < chooseX.size(); c++) {
//                        chooseCostList.add(chooseCost);
//                    }
//                    for (int j = 0; j < X; j++) { //重新遍历一遍，对分流模糊一层
//                        if (chooseX.contains(j)) {
//                            continue;
//                        }
//                        if (recordCost[j]-chooseCost-margin_us < 0) {
//                            chooseX.add(j);
//                            chooseCostList.add(recordCost[j]);
//                        }
//                    }
//                    // 得到扩展版的chooseX&chooseCostList, 接下来更新XCostload

                    int chooseNumber = chooseX.size(); // 在代价相同最小的里面随机选
                    Random random = new Random();
                    int ultimateChoose = chooseX.get(random.nextInt(chooseNumber)); // chooseX.get不能少
                    singleChoose[i] = ultimateChoose;
                    XCostload[ultimateChoose] += recordCostBoard.get(k)[i][ultimateChoose];
                } else {
                    if (noDiffChooseIndex >= X) {
                        noDiffChooseIndex = 0;
                    }
                    int ultimateChoose = noDiffChooseIndex; //算法负责查询路由时要做到均匀分 真的均分很重要 因为这里无异构时代价计算会收到均衡的影响
                    noDiffChooseIndex++;

                    singleChoose[i] = ultimateChoose;
                    XCostload[ultimateChoose] += chooseCost;
                }
            }
        }

        // X个结点副本cost load中的最大值
        if(isDiffReplicated) {// X个结点副本cost load中的最大值
            List<Integer> maxC = new ArrayList<Integer>();
            Cost = XCostload[0];
            maxC.add(0);
            for (int i = 1; i < X; i++) {
                double res = XCostload[i]-Cost;
                if (res > 0) {
                    maxC.clear();
                    maxC.add(i);
                    Cost = XCostload[i];
                } else if (res == 0) {
                    maxC.add(i);
                }
            }
        }
        else { // 同构时为了减小算法中随机均分还是有的不均对结果的干扰，采用求和平均，重要，因为之前代价相等副本随机均分时的不均取max还是有很大影响
            Cost = 0;
            for(int i=0;i<XCostload.length;i++){
                Cost+=XCostload[i];
            }
            Cost /= X;
        }

        //打印结果
        System.out.print(String.format("Cost:%.2f s| ",Cost));
        for(int i=0;i<X;i++) {
            System.out.print(String.format("R%d%s:%.2f s,",i+1,xackSeq[i],XCostload[i])); // 用查询执行耗时作为load负载评价
        }
        System.out.println("");
    }

    /**
     * 关键的状态评价函数 利用recordCostBoard实现记忆式更新计算
     *
     */
    public void calculate_remember(AckSeq[] xackSeq) {
        double[] XCostload = new double[X]; // 用于后面取max(sum HB)
        for(int i=0;i<X;i++) {
            XCostload[i] = 0;
        }

        for(int i=0;i<X;i++) {
            int pos1 = pos_1[i];
            int pos2 = pos_2[i];
            AckSeq ackSeq = xackSeq[i];
            for(int j=0;j<ckn;j++) {
                if(!ifcontains(ackSeq,pos1,pos2,j+1)) {
                    continue;
                }
                // 开始更新
                RangeQuery[] singleQueries = rangeQueries.get(j); // qckj下所有查询
                for(int z=0;z<singleQueries.length;z++) {
                    RangeQuery q = singleQueries[z];
                    H_ian h = new H_ian(totalRowNumber, ckn, CKdist,
                            q.qckn, q.qck_r1_abs, q.qck_r2_abs, q.r1_closed, q.r2_closed, q.qck_p_abs,
                            ackSeq.ackSeq);
                    //更新
                    recordCostBoard.get(j)[z][i] = h.calculate(fetchRowCnt, costModel_k, costModel_b, cost_session_around, cost_request_around);
                }
            }
        }
        //recordCostBoard更新完毕，下面根据recordCostBorad重新计算qchooseX和XCostload
        for(int i=0;i<ckn;i++){
            double[][] cost = recordCostBoard.get(i);
            int num = rangeQueries.get(i).length;
            for(int j=0;j<num;j++){
                double[] qcostX = cost[j];

                //选择最小值集合
                double minqcost = qcostX[0];
                List<Integer> minchoose = new ArrayList<>();
                minchoose.add(0);
                for(int z=1;z<X;z++){
                    if(qcostX[z] < minqcost) {
                        minqcost = qcostX[z];
                        minchoose.clear();
                        minchoose.add(z);
                    }
                    else if(qcostX[z] == minqcost) {
                        minchoose.add(z);
                    }
                }

                //从等量代价最小副本中随机选一个路由
                Random random = new Random();
                int minNum = minchoose.size();
                int ultimateChoose = minchoose.get(random.nextInt(minNum));
                qChooseX.get(i)[j] = ultimateChoose;
                XCostload[ultimateChoose] += minqcost;
            }
        }

        // qchooseX和XCostload更新完毕
        // 下面取出XCostload中的最大值作为输入状态的评价值
        if(isDiffReplicated) {
            List<Integer> maxC = new ArrayList<Integer>();
            Cost = XCostload[0];
            maxC.add(0);
            for (int i = 1; i < X; i++) {
                double res = XCostload[i]-Cost;
                if (res > 0) {
                    maxC.clear();
                    maxC.add(i);
                    Cost = XCostload[i];
                } else if (res == 0) {
                    maxC.add(i);
                }
            }
        }
        else { // 同构时为了减小算法中随机均分还是有的不均对结果的干扰，采用求和平均，重要，因为之前代价相等副本随机均分时的不均取max还是有很大影响
            Cost = 0;
            for(int i=0;i<XCostload.length;i++){
                Cost+=XCostload[i];
            }
            Cost /= X;
        }

        //打印结果
        System.out.print(String.format("Cost:%.2f s| ",Cost));
        for(int i=0;i<X;i++) {
            System.out.print(String.format("R%d%s:%.2f s,",i+1,xackSeq[i],XCostload[i])); // 用查询执行耗时作为load负载评价
        }
        System.out.println("");

    }

    /**
     *
     * @param ackSeq
     * @param pos1
     * @param pos2
     * @param qcki 从1开始 ck1 ck2 ... ckn 因为xcakSeq也是从1开始的
     * @return
     */
    private boolean ifcontains(AckSeq ackSeq, int pos1, int pos2, int qcki) {
        int[] ackSeq_ = ackSeq.ackSeq;
        for(int i=pos1; i<=pos2; i++) {
            if(ackSeq_[i] == qcki) {
                return true;
            }
        }
        return false;
    }




    /*
     直接给定一个排序，给出结果包括查询集合
     */
    public void calculate_unit(AckSeq[] xackSeq) {
        calculate(xackSeq);

        pws=new ArrayList<>();
        for(int i=0;i<X;i++) {
            try {
                PrintWriter pw_ = new PrintWriter(new FileOutputStream(Constant.ks+"_"+Constant.cf[i]
                        +"_R" + (i + 1)+ xackSeq[i]+String.format("_%.2f",Cost)+ "_sqls.txt"));
                pws.add(pw_);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        for(int k=0;k<ckn;k++) {
            RangeQuery[] singleQueries = rangeQueries.get(k);
            int[] singleChoose = qChooseX.get(k);
            for(int j=0;j<singleChoose.length;j++) {
                int ultimateChoose = singleChoose[j];
                pws.get(ultimateChoose).println(queryPicture.getSql(Constant.ks, Constant.cf[ultimateChoose],Constant.pkey[ultimateChoose],
                        CKdist,singleQueries[j]));
            }
        }

        for(PrintWriter pw_: pws) {
            pw_.close();
        }

        if(!isDiffReplicated){ // 如果同构 再写一个集合全部查询的大文件
            PrintWriter pw_=null;
            try {
                pw_ = new PrintWriter(new FileOutputStream(Constant.ks+"_"+Constant.cf[0]
                        +"_R" + xackSeq[0]+String.format("_%.2f",Cost)+ "_sqlsALL.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for(int k=0;k<ckn;k++) {
                RangeQuery[] singleQueries = rangeQueries.get(k);
                int[] singleChoose = qChooseX.get(k);
                for(int j=0;j<singleChoose.length;j++) {
                    int ultimateChoose = singleChoose[j];
                    pw_.println(queryPicture.getSql(Constant.ks, Constant.cf[ultimateChoose],Constant.pkey[ultimateChoose],
                            CKdist,singleQueries[j]));
                }
            }
            pw_.close();
        }
    }


    /**
     * 模拟退火算法
     * 搜索排序键排列，找到使得在给定数据和查询集的条件下，H模型代价最小（已经验证H模型代价和真实查询代价的一致性）
     * 关键环节：
     * 1. 初温，初始解
     * 2. 状态产生函数
     * 3. 状态接受函数
     * 4. 退温函数
     * 5. 抽样稳定准则
     * 6. 收敛准则
     *
     * X个异构副本组成一个状态解
     * 分流策略：简单的代价最小原则
     *
     * SA一步式尚未考虑负载均衡代价：找到HR近似最小的一组解
     * 状态目标值为HR
     *
     */
    public double SA(boolean isRecordProcess) {
        PrintWriter pw = null;
        if(isRecordProcess) {
            try {
                pw = new PrintWriter(new FileOutputStream(Constant.SArecord));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pw.write("cost_current,cost_best_sofar,temperature\n");
        }

        //确定初温：
        // 随机产生一组状态，确定两两状态间的最大目标值差，然后依据差值，利用一定的函数确定初温
        int setNum = 20;
        List<AckSeq[]> xackSeqList = new ArrayList();
        for(int i=0; i< setNum; i++) {
            AckSeq[] xackSeq = new AckSeq[X];
            shuffle(xackSeq);
            xackSeqList.add(xackSeq);
        }
        double maxDeltaC = 0; // 两两状态间的最大目标值差
        for(int i=0;i<setNum-1;i++) {
            for(int j =i+1;j<setNum;j++) {
                calculate(xackSeqList.get(i));
                double tmp = Cost;
                calculate(xackSeqList.get(j));
                tmp = Math.abs(tmp - Cost);
                if((tmp-maxDeltaC) >0) // tmp > maxDeltaB
                    maxDeltaC = tmp;
            }
        }
        double pr = 0.8;
        if(maxDeltaC == 0) {
            maxDeltaC = 0.001;
        }
        double t0 = -maxDeltaC/Math.log(pr);
        System.out.println("初温为："+t0);

        long elapsed = System.nanoTime();
        //确定初始解
        AckSeq[] currentAckSeq  = new AckSeq[X];
        shuffle(currentAckSeq);
        calculate(currentAckSeq);
        Cost_best = Cost; // 至于把currentAckSeq加进Set在后面完成的
        Cost_best_bigloop = Cost;

        int endCriteria = 30;// 终止准则: BEST SO FAR连续20次退温保持不变
        int endCount = 0;
        int sampleCount = 20;// 抽样稳定准则：20步定步长
        double deTemperature = 0.7; // 指数退温系数
        while(endCount < endCriteria) {
            // 抽样稳定
            // 记忆性：注意中间最优结果记下来
            for(int sampleloop = 0; sampleloop < sampleCount; sampleloop++) {
                //增加记忆性
                if(isRecordProcess) {
                    pw.write(Cost+","+Cost_best+","+t0+"\n");
                }

                double comp = Cost-Cost_best;
                if(comp<0) { //<
                    Cost_best = Cost;
                    ackSeq_best_step.clear();
                    ackSeq_best_step.add(new XAckSeq(currentAckSeq));
                    System.out.println("清空最优集合 更新");
                }
                else if(comp==0) {
                    ackSeq_best_step.add(new XAckSeq(currentAckSeq));
                }

                double currentCost = Cost; // 当前状态的状态值保存在Cost，暂时转存备份到currentCost
                //由当前状态产生新状态
                AckSeq[] nextAckSeq = generateNewStateX(currentAckSeq);
//                System.out.println(pos_1[0]+"-"+pos_2[0]+" "+pos_1[1]+"-"+pos_2[1]+" "+pos_1[2]+"-"+pos_2[2]);
//                calculate_remember(nextAckSeq); // 增量式 加速一点点 Cost会被改变 bug。。。。。
                calculate(nextAckSeq); // 增量式 加速一点点 Cost会被改变
                double delta = Cost-currentCost; // 新旧状态的目标函数值差
                double threshold;
                if(delta<=0) {
                    threshold = 1;
                    System.out.println("新状态不比现在状态差");
                }
                else {
                    //threshold = Math.exp(-delta/t0);
                    threshold = Math.exp(-delta/t0); //TODO
                    System.out.println("新状态比现在状态差");
                }

                if(Math.random() <= threshold) { // 概率接受，替换当前状态
                    currentAckSeq = nextAckSeq;
                    System.out.println("接受新状态");
                    // HR就是现在更新后的HR
                }
                else {// 否则保持当前状态不变
                    Cost = currentCost;//恢复原来解的状态值
                    System.out.println("维持当前状态不变");
                    // currentAckSeq就是原本的
                }
            }

            if(Cost_best!=Cost_best_bigloop) {
                endCount = 0; // 重新计数
                Cost_best_bigloop = Cost_best; // 把当前最小值传递给外圈循环
                System.out.println("【这次退温BEST SO FAR改变】");
            }
            else { // 这次退温后best_so_far和上次比没有改变
                endCount++;
                System.out.println(String.format("【这次退温BEST SO FAR连续%d次没有改变】",endCount));
            }
            //退温
            t0 = t0*deTemperature;
        }
        //终止 输出结果
        if(isRecordProcess) {
            pw.close();
        }
        elapsed = System.nanoTime() - elapsed;
        double time = elapsed / (double) Math.pow(10, 6); // unit: ms
        return time;
    }


    public double combine() {
        System.out.println("----------------------------------------------------");
        // 第一步： SA找到Cost代价近似最小的一组解
        double time = SA(true);
        System.out.println("耗时 "+time+" ms"); // NOTE: 这部分时间里包括了pw保存sqls写文件的时间
        System.out.println("step完成: SA找到Cost近似最小的" + ackSeq_best_step.size() + "个近似最优解:");
        System.out.println("选择最后一个解作为输出");
        Iterator iterator = ackSeq_best_step.iterator();
        XAckSeq xackSeq = (XAckSeq) iterator.next();
        while (iterator.hasNext()) {
            xackSeq = (XAckSeq) iterator.next();
        }
        calculate(xackSeq.xackSeq);
        System.out.println(String.format("目标值Cost近似最小为：%.2f (s)", Cost));

        //记录最后一个最优解的查询路由
        pws = new ArrayList<>();
        for(int i=0;i<X;i++) {
            try {
                PrintWriter pw_ = new PrintWriter(new FileOutputStream(Constant.ks+"_"+Constant.cf[i]
                        +"_R" + (i + 1)+ xackSeq.xackSeq[i]+String.format("_%.2f",Cost)+ "_sqls.txt"));
                pws.add(pw_);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        for(int k=0;k<ckn;k++) {
            RangeQuery[] singleQueries = rangeQueries.get(k);
            int[] singleChoose = qChooseX.get(k);
            for(int j=0;j<singleChoose.length;j++) {
                int ultimateChoose = singleChoose[j];
                pws.get(ultimateChoose).println(queryPicture.getSql(Constant.ks, Constant.cf[ultimateChoose],Constant.pkey[ultimateChoose],
                        CKdist,singleQueries[j]));
            }
        }
        for(PrintWriter pw_: pws) {
            pw_.close();
        }

        if(!isDiffReplicated){ // 如果同构 再写一个集合全部查询的大文件
            PrintWriter pw_=null;
            try {
                pw_ = new PrintWriter(new FileOutputStream(Constant.ks+"_"+Constant.cf[0]
                        +"_R" + xackSeq.xackSeq[0]+String.format("_%.2f",Cost)+ "_sqlsALL.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for(int k=0;k<ckn;k++) {
                RangeQuery[] singleQueries = rangeQueries.get(k);
                int[] singleChoose = qChooseX.get(k);
                for(int j=0;j<singleChoose.length;j++) {
                    pw_.println(queryPicture.getSql(Constant.ks, Constant.cf[0],Constant.pkey[0],
                            CKdist,singleQueries[j]));
                }
            }
            pw_.close();
        }
        return time;
    }

    private void shuffle(AckSeq[] xackSeq) {
        if(isDiffReplicated) {// 副本异构
            for (int j = 0; j < X; j++) {
                List<Integer> ackList = new ArrayList();
                xackSeq[j] = new AckSeq(new int[ckn]);
                for (int i = 1; i <= ckn; i++) { // 这里必须从1开始，因为表示ck排序输入参数从1开始
                    ackList.add(i);
                }
                Collections.shuffle(ackList); //JAVA的Collections类中shuffle的用法
                for (int i = 0; i < ckn; i++) {
                    xackSeq[j].ackSeq[i] = ackList.get(i);
                }
            }
        }
        else { // 无异构
            List<Integer> ackList = new ArrayList();
            for (int i = 1; i <= ckn; i++) { // 这里必须从1开始，因为表示ck排序输入参数从1开始
                ackList.add(i);
            }
            Collections.shuffle(ackList); //JAVA的Collections类中shuffle的用法
            for (int j = 0; j < X; j++) {
                xackSeq[j] = new AckSeq(new int[ckn]);
                for (int i = 0; i < ckn; i++) {
                    xackSeq[j].ackSeq[i] = ackList.get(i);
                }
            }
        }
    }


    private AckSeq[] generateNewStateX(AckSeq[] xackSeq) {
        AckSeq[] nextXAckSeq = new AckSeq[X];
        if(isDiffReplicated) { // 副本异构
            for (int i = 0; i < X; i++) {
                Random random = new Random();
                int pos1 = random.nextInt(ckn); // 0~ckn-1
                int pos2 = random.nextInt(ckn);
                while(true) {
                    if(pos2!=pos1)
                        break;
                    else {
                        pos2 = random.nextInt(ckn);
                    }
                }
                if(pos1<pos2) {
                    pos_1[i] = pos1;
                    pos_2[i] = pos2;
                } else {
                    pos_1[i] = pos2;
                    pos_2[i] = pos1;
                }
                nextXAckSeq[i] = new AckSeq(generateNewState(xackSeq[i].ackSeq, pos_1[i], pos_2[i]));
            }
        }
        else { // 无异构
            Random random = new Random();
            int pos1 = random.nextInt(ckn); // 0~ckn-1
            int pos2 = random.nextInt(ckn);
            while(true) {
                if(pos2!=pos1)
                    break;
                else {
                    pos2 = random.nextInt(ckn);
                }
            }
            if(pos1>pos2){
                int tmp = pos1;
                pos1 = pos2;
                pos2 = tmp;
            }
            for (int i = 0; i < X; i++) {
                pos_1[i] = pos1;
                pos_2[i] = pos2;
            }
            int[] tmp = generateNewState(xackSeq[0].ackSeq,pos_1[0],pos_2[0]);
            for (int i = 0; i < X; i++) {
                nextXAckSeq[i] = new AckSeq(tmp);
            }
        }
        return nextXAckSeq;
    }

    /**
     * 状态产生函数/邻域函数：由产生候选解的方式和候选解产生的概率分布两部分组成。
     * 出发点：尽可能保证产生的候选解遍布全部解空间
     *
     * @param ackSeq
     */
    private int[] generateNewState(int[] ackSeq,int pos1, int pos2) {
        int [] nextAckSeq = new int[ckn];
        for(int i=0;i<ckn;i++) {
            nextAckSeq[i] = ackSeq[i];
        }

        double p_swap = 0.4; // [0,0.3)
        double p_inverse = 0.7; // [0.3,0.6)
        double p_insert = 1; // [0.6,1)

        double r = Math.random();
        if(r<p_swap) {
            generateNewState_swap(nextAckSeq,pos1,pos2);
        }
        else if(r<p_inverse) {
            generateNewState_inverse(nextAckSeq,pos1,pos2);
        }
        else {
            generateNewState_insert(nextAckSeq,pos1,pos2);
        }
        return  nextAckSeq;
    }

    //随机交换状态中两个不同位置的ck
    private void generateNewState_swap(int[] ackSeq, int pos1, int pos2){
//        System.out.println("swap");
        int tmp = ackSeq[pos1];
        ackSeq[pos1] = ackSeq[pos2];
        ackSeq[pos2] = tmp;
    }

    //将两个不同位置间的串逆序
    private void generateNewState_inverse(int[] ackSeq, int pos1, int pos2) {
//        System.out.println("inverse");
        //已经保证pos1<pos2
        int[] backup = new int[pos2-pos1+1];
        for(int i=pos1; i<= pos2; i++) {
            backup[i-pos1] = ackSeq[i];
        }
        for(int i=pos1; i<=pos2; i++) {
            ackSeq[i] = backup[pos2-i];
        }
    }

    //随机选择某一位置的ck并插入到另一随机位置
    private void generateNewState_insert(int[] ackSeq, int pos1, int pos2) {
        //把pos1位置的ck插入到pos2位置
        //已经保证pos1<pos2

//        System.out.println("insert");
        if(pos2-pos1==1) { // 直接当作swap处理
            int tmp = ackSeq[pos1];
            ackSeq[pos1] = ackSeq[pos2];
            ackSeq[pos2] = tmp;
            return;
        }
        int[] tmp = new int[ckn];
        for(int i=0;i<ckn;i++) {
            tmp[i] = ackSeq[i];
        }
        for(int i =0; i<ckn;i++) {
            if(i<pos1) {
                ackSeq[i] = tmp[i];
            }
            else if(i<pos2) {
                ackSeq[i] = tmp[i+1];
            }
            else if(i==pos2) {
                ackSeq[i] = tmp[pos1];
            }
            else {
                ackSeq[i] = tmp[i];
            }
        }
    }
}

