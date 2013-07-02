package test.util;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFMarshallingContext;

import java.util.List;
import java.util.Map;

/**
 * STAF操作封装
 */

public class StafWrapper {

    /**
     * 返回STAF执行结果接口
     */
    @SuppressWarnings("rawtypes")
    public static String getExecuteResult(String machine, String service, String request) throws Exception {
        // 实例化STAF的句柄
        STAFHandle handle = new STAFHandle("GarudaTest");
        // sleep 1s
        Thread.sleep(1000);
        // 执行STAF命令
        String result = handle.submit(machine, service, request);
        // 返回STAF的marshall
        STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(result);
        Map processCompletionMap = (Map) mc.getRootObject();
        String processRC = (String) processCompletionMap.get("rc");
        if (!processRC.equals("0")) {
            System.out.println("ERROR:  Process RC is " + processRC + " instead of 0.");
            System.exit(1);
        }
        List returnedFileList = (List) processCompletionMap.get("fileList");
        Map stdoutMap = (Map) returnedFileList.get(0);
        String stdoutRC = (String) stdoutMap.get("rc");
        if (!stdoutRC.equals("0")) {
            System.out.println("ERROR retrieving process Stdout data. RC=" + stdoutRC);
            System.exit(1);
        }
        String stdoutData = (String) stdoutMap.get("data");
        handle.unRegister();
        return stdoutData;
    }

    /**
     * 开启driver的jboss服务接口
     */
    public static void startDriver(String hostIp) throws Exception {
        String request_start = "START SHELL COMMAND sh parms jbossctl restart workdir "
                + "/home/admin/alisataskdriver/bin username admin";
        executeCommand(hostIp, request_start);
    }

    /**
     * 停掉driver的jboss服务接口
     */
    public static void stopDriver(String hostIp) throws Exception {
        String request_stop = "START SHELL COMMAND sh parms jbossctl stop workdir "
                + "/home/admin/alisataskdriver/bin username admin";
        executeCommand(hostIp, request_stop);
    }

    /**
     * 对driver执行闪断
     */
    public static void flashCutMachine(String hostIp) throws InterruptedException, STAFException {
        String command = "start shell command service network restart username root";
        executeCommand(hostIp, command);
    }

    /**
     * 开启node的jboss服务接口
     */
    public static void startNode(String hostIp) throws Exception {
        String request_start = "START SHELL COMMAND sh parms garuda.sh restart workdir "
                + "/home/admin/garuda/bin username admin";
        executeCommand(hostIp, request_start);
    }

    /**
     * 停掉node的jboss服务接口
     */
    public static void stopNode(String hostIp) throws Exception {
        String request_stop = "START SHELL COMMAND sh parms garuda.sh stop workdir "
                + "/home/admin/garuda/bin username admin";
        executeCommand(hostIp, request_stop);
    }

    /**
     * 重启node的jboss服务接口
     */
    public static void restartNode(String hostIp) throws Exception {
        String request_stop = "START SHELL COMMAND sh parms garuda.sh restart workdir "
                + "/home/admin/garuda/bin  username admin";
        executeCommand(hostIp, request_stop);
    }

    /**
     * 中断机器网卡3分钟
     * @param hostIp
     * @throws STAFException
     * @throws InterruptedException
     */
    public static void InterruptNetwork(String hostIp) throws STAFException, InterruptedException {
//        String command = "START SHELL COMMAND sh parms stop_start_network.sh workdir "
//                + TestConstants.NETWORK_RESTART_DIR + " username root";
//        executeCommand(hostIp, command);

    }

    /**
     * 执行STAF命令接口
     */
    public static void executeCommand(String hostIp, String stafCommand) throws STAFException, InterruptedException {
        // 实例化STAF的句柄
        STAFHandle handle = new STAFHandle("GarudaTest");
        String service = "PROCESS";

        // 执行STAF命令
        System.out.println("执行staf命令 IP:" + hostIp + " Command:" + service + " " + stafCommand);
        handle.submit(hostIp, service, stafCommand);

        // sleep 1s
        Thread.sleep(1000);
        handle.unRegister();
    }
}
