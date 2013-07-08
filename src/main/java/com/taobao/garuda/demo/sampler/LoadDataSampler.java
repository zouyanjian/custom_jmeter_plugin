package com.taobao.garuda.demo.sampler;

import com.google.common.base.Stopwatch;
import com.taobao.garuda.util.H2Connection;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.TimeUnit;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午4:31
 */
public class LoadDataSampler extends AbstractSampler implements Interruptible {
    //TODO need Move to GUI Configuration
    private final String H2IPADDRESS = "10.232.129.79";
    private final int H2PORT = 14001;
    private final String DBNAME = "test4dmp";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataSampler.class);

    @Override
    public boolean interrupt() {
        Thread.currentThread().interrupt();
        return true;
    }

    @Override
    public SampleResult sample(Entry entry) {

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        System.out.println("into sample:");
        LOGGER.info("into sample:");
        if (entry != null)
            System.out.println(entry.toString());

        final JMeterVariables vars = JMeterContextService.getContext().getVariables();
        String sql = vars.get("sql");
        System.out.println("getProperty From Sql:" + sql);
        LOGGER.info("getProperty From Sql:" + sql);
        SampleResult res = new SampleResult();
        res.sampleStart();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = H2Connection.getTcpConnection(H2IPADDRESS, H2PORT, DBNAME);
            if(connection == null){
                return null;
            }
            synchronized (connection){

                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                stopwatch.stop();
                System.out.println("Connect to DB Spend:" + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + "ms");
                LOGGER.info("Connect to DB Spend:" + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + "ms");
                stopwatch.reset();
                stopwatch.start();
                ResultSet resultSet = statement.executeQuery(sql);
                stopwatch.stop();
                LOGGER.info("execute query Spend:"+stopwatch.elapsedTime(TimeUnit.MILLISECONDS)+"ms");
                stopwatch.reset();
                stopwatch.start();
                if (resultSet == null) {
                } else {
                    String outPutData = transformResultSet(resultSet);
                    res.setResponseData(outPutData, "UTF-8");
                    resultSet.close();
                    stopwatch.stop();
                    LOGGER.info("transform query Spend:"+stopwatch.elapsedTime(TimeUnit.MILLISECONDS)+"ms");
                }
                closeResource(connection, statement);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            closeResource(connection,statement);
        }
        res.sampleEnd();
        res.setContentType("plain");
        res.setDataEncoding("UTF-8");
        res.setResponseCode("200");
        res.setResponseCodeOK();

        return res;
    }

    private String transformResultSet(ResultSet resultSet) {
        if(resultSet == null) return "";
        StringBuilder builder = new StringBuilder();
        int recordCount = getRecordCount(resultSet);
        builder.append("Record Count:").append(recordCount).append("\n");
        int columnCount =0;
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            StringBuilder columns = new StringBuilder();
            if(resultSetMetaData != null){
                columnCount = resultSetMetaData.getColumnCount();
                for(int index=1;index<=columnCount;index++){
                    columns.append(resultSetMetaData.getColumnName(index)).append(",");
                }
                int length = columns.length();
                if(length>0){
                    columns.setCharAt(length-1,'\n');
                }
                builder.append(columns.toString());
                builder.append(getResultData(resultSet,columnCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private String getResultData(ResultSet resultSet, int columnCount) {
        if(resultSet == null) return "";
        StringBuilder dataBuilder = new StringBuilder();
        try {
            resultSet.beforeFirst();
            while(resultSet.next()){
                for(int i=1;i<=columnCount;i++){
                    dataBuilder.append(resultSet.getString(i)).append(",");
                }
                int length = dataBuilder.length();
                dataBuilder.setCharAt(length-1,'\n');
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return dataBuilder.toString();
    }

    private int getRecordCount(ResultSet resultSet) {
        int recordCount=0;
        try {
            resultSet.last();
            recordCount = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordCount;
    }

    private void closeResource(Connection connection, Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
