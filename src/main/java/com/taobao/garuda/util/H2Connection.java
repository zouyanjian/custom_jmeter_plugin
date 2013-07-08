package com.taobao.garuda.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * User: wb-zouyanjian
 * Date: 13-7-3
 * Time: 上午9:50
 */
public class H2Connection {
    private static String options;

    static {
        StringBuilder builder = new StringBuilder();
        builder.append(";UNDO_LOG=0");
        builder.append(";IFEXISTS=FALSE");
        builder.append(";DB_CLOSE_DELAY=-1");
        builder.append(";DB_CLOSE_ON_EXIT=FALSE");
        builder.append(";MULTI_THREADED=1");
        builder.append(";TRACE_LEVEL_SYSTEM_OUT=0");
        builder.append(";CACHE_SIZE=204800");
        builder.append(";QUERY_TIMEOUT=100000");
        builder.append(";NESTED_JOINS=FALSE");
        options = builder.toString();
    }

    public static Connection getTcpConnection(String host, int port, String dbName) {
        try {
            org.h2.Driver.load();
            String url = "jdbc:h2:tcp://" + host + ":" + port + "/mem:" + dbName + options;
            return DriverManager.getConnection(url, "sa", "");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
