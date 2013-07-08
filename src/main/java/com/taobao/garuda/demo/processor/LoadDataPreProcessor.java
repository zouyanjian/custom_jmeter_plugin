package com.taobao.garuda.demo.processor;

import com.google.common.base.Stopwatch;
import com.taobao.garuda.tools.ZkClient;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.util.ClusterUtil;
import test.util.LoaderUtil;
import test.util.ZkUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午1:53
 */
public class LoadDataPreProcessor extends AbstractTestElement implements PreProcessor, NoThreadClone, TestStateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataPreProcessor.class);
    private int loadJobTimeOut = 30 * 60 * 1000;

    public static void main(String[] args){
        System.out.println("Main....");
        LoadDataPreProcessor processor = new LoadDataPreProcessor();
        processor.testStarted();
    }
    private String zkURL ="10.232.36.107:12181,10.232.36.107:12182,10.232.36.107:12183/stg";

    private static final String[] entireOnlineTables = new String[]{"test4dmp.test"};
    private ZkClient zkClient;
    @Override
    public void process() {

        System.out.println("pre deal with...... process()..");
        LOGGER.info("pre deal with...... process()..");
        final JMeterVariables vars = JMeterContextService.getContext().getVariables();
        if (vars != null) {
            vars.put("sql", "select count(1) from test4dmp.test__0;");
        }
    }

    //TODO 抽取配置到GUI界面上
    @Override
    public void testStarted() {
        System.out.println("testStarted()...");
        LOGGER.info("testStarted()...");
        //TODO zookeeper 地址
        System.setProperty("zkURL", zkURL);
        try {
            zkClient = new ZkClient();
            clearHistorySubmitLoadJob();
            restartMonitorNode();
            clearCurrentLoadJob();
            restartLoaclNodes();
            clearGarudaCache();
            sendLoadJob();
            entireOnlineLoadTables(zkClient);
        } catch (Exception e) {
            logExceptionMessage(e);
        }finally {
            closeAll();
        }
    }

    private void logExceptionMessage(Exception e) {
        LOGGER.info("error:",e);
        System.out.println(e.getMessage());
        System.out.println("系统初始化失败!!");
    }

    private void closeAll() {
        System.out.println("close ZKClient....");
        if(zkClient !=null)
        zkClient.close();
    }

    private void sendLoadJob() throws Exception {
        String fileList = "/garuda/test_d/ds=20120311/checksum/check.8";
        String meta = "/garuda/test_d/meta/trade.ini";
        String activeMonitor = ZkUtil.getActiveMonitor(zkClient);
        LoaderUtil.submitLoaderJob(activeMonitor, fileList, meta);
        waitLoadJobDone();
    }

    private void clearGarudaCache() {
        ClusterUtil.cleanCache("stg", "10.235.168.153:8080");
    }

    //TODO restart Monitor Node
    private void restartMonitorNode() {
        //重启monitor
            /*for (String monitor : monitors) {
                StafWrapper.restartNode(monitor);
            }*/
    }

    //TODO 重启LocalNode节点.
    private void restartLoaclNodes() {
        //StafWrapper.restartNode(ClusterUtil.extractIpFromNodeName(loaderNode));
        //System.out.println("sleep 30s waiting nodes restart");
        //Thread.currentThread().sleep(30000);
    }

    /**
     * 删除当前提交的load任务
     * @throws Exception
     */
    private void clearCurrentLoadJob() throws Exception {
        ZkUtil.clearLoaderJob(zkClient);
    }

    /**
     * 清除历史的Loader载入的JOB信息
     * @throws Exception
     */
    private void clearHistorySubmitLoadJob() throws Exception {
        try {
            ZkUtil.deleteConfig(zkClient, "/monitor/loadsubmitqueue");
        } catch (KeeperException.NoNodeException e) {
        }
    }

    /**
     * 数据上线,整体切换
     * @param zkClient
     */
    private void entireOnlineLoadTables(ZkClient zkClient) {
        try {
            String[] tables = entireOnlineTables;
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            System.out.println("into entireOnlineLoadTables");
            ZkUtil.setEntirelyOnlineTable(zkClient,tables);
            String activeMonitor = ZkUtil.getActiveMonitor(zkClient);
            String ret = ClusterUtil.entirelyOnline(activeMonitor);
            stopwatch.stop();
            System.out.println("EntireOnline Spend time:"+stopwatch.elapsedTime(TimeUnit.SECONDS)+"s");
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待导入任务完成.
     * @throws Exception
     */
    private void waitLoadJobDone() throws Exception {
        //等待任务完成
        long current = System.currentTimeMillis();
        out: while (System.currentTimeMillis() - current < loadJobTimeOut) { //超时时间30分钟
            List<HashMap<String, Object>> loaders = ZkUtil.getAllLoaders(zkClient);
            if(loaders.size() < 1) {
                System.out.println("loader has not been put into queue. sleep...");
                Thread.currentThread().sleep(10000);
                continue out;
            }
            for (HashMap<String, Object> loader : loaders) {
                Short state = (Short) loader.get("state");
                if (state == null || state < 3) {
                    System.out.println("loader job has not complete. state = " + state);
                    Thread.currentThread().sleep(10000);
                    continue out;
                } else {
                    System.out.println("loader job has complete. state = " + state);
                    continue;
                }
            }
            break;
        }
    }

    @Override
    public void testStarted(String s) {
        LOGGER.info("pre deal with...... testStarted("+s+")..");
        System.out.println("pre deal with...... testStarted("+s+")..");
    }

    @Override
    public void testEnded() {
        LOGGER.info("testEnd().....");
        System.out.println("testEnd().....");
    }

    @Override
    public void testEnded(String s) {
        System.out.println("pre deal with...... testEnded("+s+")..");
    }
}
