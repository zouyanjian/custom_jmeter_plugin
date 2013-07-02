package com.taobao.garuda.demo.processor.gui;

import com.taobao.garuda.demo.processor.LoadDataPreProcessor;
import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午2:06
 */
public class LoadDataPreProcessorGUI extends AbstractPreProcessorGui {

    public LoadDataPreProcessorGUI(){
        init();
    }

    private void init() {
        JPanel container = new JPanel(new BorderLayout());
        add(container, BorderLayout.CENTER);
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        LoadDataPreProcessor processor = new LoadDataPreProcessor();
        modifyTestElement(processor);
        processor.setComment("Data loading...");
        return processor;
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        super.configureTestElement(testElement);
    }

    @Override
    public String getStaticLabel() {
        return "LoadDataPreProcessor.garuda@taobao";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);

    }
}
