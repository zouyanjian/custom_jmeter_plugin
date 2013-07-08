package com.taobao.garuda.util;

import org.junit.Test;

import java.util.Date;

/**
 * User: wb-zouyanjian
 * Date: 13-7-2
 * Time: 上午10:04
 */
public class GarudaUtilTest {

    @Test
    public void testCheckSum(){
        GarudaUtil util = new GarudaUtil();
        System.out.println(util.getCRC32("pangu://localcluster/garuda/test_d/ds=20120311/pid=0/data/test")% 500);
        System.out.println(new Date(1372150531368L));
    }

}
