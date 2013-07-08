package com.taobao.garuda;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;
import org.junit.Test;

/**
 * User: wb-zouyanjian
 * Date: 13-7-3
 * Time: 下午3:12
 */
public class ZkClientTest {
    private CuratorFramework client;
    @Test
    public void test() throws Exception {
        System.setProperty("zkURL", "10.232.36.107:12181,10.232.36.107:12182,10.232.36.107:12183/stg");
        client = CuratorFrameworkFactory.builder().connectString("10.235.161.64:2181")
                .connectionTimeoutMs(20000)
                .sessionTimeoutMs(10000)
                .retryPolicy(new RetryOneTime(2000)).build();

        client.start();

        System.out.println(client.getData().forPath("/renfu/case_1"));
        client.close();
    }
}
