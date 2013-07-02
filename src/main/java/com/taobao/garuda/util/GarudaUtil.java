package com.taobao.garuda.util;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * User: wb-zouyanjian
 * Date: 13-7-2
 * Time: 上午10:04
 */
public class GarudaUtil {



    public static long getCRC32(String value) {
        Checksum checksum = new CRC32();
        byte[] bytes = value.getBytes();
        checksum.update(bytes,0,bytes.length);
        return checksum.getValue();
    }
}
