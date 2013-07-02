package com.taobao.garuda.demo.processor;

import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午1:53
 */
public class LoadDataPreProcessor extends AbstractTestElement implements PreProcessor, NoThreadClone, TestStateListener {
    public static void main(String[] args){
        System.out.println("Main....");
        LoadDataPreProcessor processor = new LoadDataPreProcessor();
        processor.process();
    }
    @Override
    public void process() {
        System.out.println("pre deal with...... process()..");
    }

    @Override
    public void testStarted() {
        System.out.println("testStarted()...");
        this.testStarted("start...xxx");
    }

    @Override
    public void testStarted(String s) {
        System.out.println("pre deal with...... testStarted("+s+")..");
    }

    @Override
    public void testEnded() {
        System.out.println("testEnd().....");
        this.testEnded("end...xxx");
    }

    @Override
    public void testEnded(String s) {
        System.out.println("pre deal with...... testEnded("+s+")..");
    }
}
