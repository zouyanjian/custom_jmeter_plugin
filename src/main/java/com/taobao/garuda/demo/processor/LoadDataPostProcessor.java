package com.taobao.garuda.demo.processor;

import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.JMeterContext;

import java.io.Serializable;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午4:15
 */
public class LoadDataPostProcessor  extends AbstractTestElement implements
        Cloneable, Serializable, PostProcessor, TestElement {
    @Override
    public void process() {
        System.out.println("into the PostProcessor");
        JMeterContext threadContext = getThreadContext();

        String responseString = threadContext.getPreviousResult().getResponseDataAsString();
        System.out.println("previous result:"+responseString);
//        try {
//            threadContext.getPreviousResult().setResponseData("12123123".getBytes(Charsets.UTF_8));
//        } catch (Exception e) {
//            System.out.println(e.getStackTrace());
//        }
    }
}
