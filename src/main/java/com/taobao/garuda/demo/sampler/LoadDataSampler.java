package com.taobao.garuda.demo.sampler;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午4:31
 */
public class LoadDataSampler extends AbstractSampler implements Interruptible {
    @Override
    public boolean interrupt() {
        Thread.currentThread().interrupt();
        return true;
    }

    @Override
    public SampleResult sample(Entry entry) {
        System.out.println("into sample:");
        System.out.println(entry.toString());
        SampleResult res = new SampleResult();
        res.sampleStart();
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res.sampleEnd();
        res.setResponseCode("2222");
        res.setResponseData("xxxxx","UTF-8");
        res.setResponseCodeOK();
        return res;
    }
}
