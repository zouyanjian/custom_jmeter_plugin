package com.taobao.garuda.util.demo;

import com.taobao.garuda.tools.ZkClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.util.ZkUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: wb-zouyanjian
 * Date: 13-7-2
 * Time: 上午11:52
 */
public class ZKUtilTest {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ZkClient zkClient;

    @Before
    public void setUp(){
        //设置zkURl
        System.setProperty("zkURL", "10.232.36.107:12181,10.232.36.107:12182,10.232.36.107:12183/stg");
        try {
            zkClient = new ZkClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void should_connect_to_ZK_and_show_monitor_ip_success() throws Exception {
         String monitorIp = ZkUtil.getActiveMonitor(zkClient);
         logger.info(monitorIp);
    }


    @Test
    public void should_get_all_loaders_success() throws Exception {
        List<HashMap<String,Object>> allLoaders = ZkUtil.getAllLoaders(zkClient);
        for (HashMap<String,Object> allLoader : allLoaders) {
            logger.info("loader-info--start");
            for (Map.Entry<String,Object> stringObjectEntry : allLoader.entrySet()) {
                logger.info("key:{},value:{}",stringObjectEntry.getKey(),stringObjectEntry.getValue());
            }
            logger.info("loader-info--end");
        }
    }

    @Test
    public void should_get_init_plans_success() throws Exception {
        List<HashMap<String, Object>> initPlans = ZkUtil.getInitPlans(zkClient);

        for (HashMap<String,Object> initPlan : initPlans) {
            logger.info("initPlan-info--start");
            for (Map.Entry<String,Object> entry : initPlan.entrySet()) {
                logger.info("key:{},value:{}",entry.getKey(), entry.getValue());
            }

        }
    }

    @Test
    public void should_set_table_entireOnline_success() throws Exception {
        ZkUtil.setEntirelyOnlineTable(zkClient,new String[]{"test4dmp.test"});
    }

}
