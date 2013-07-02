package com.taobao.garuda.demo.processor.gui;

import com.taobao.garuda.demo.processor.LoadDataPostProcessor;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午4:26
 */
public class LoadDataPostProcessorGUI extends AbstractPostProcessorGui {

    public LoadDataPostProcessorGUI(){
        JPanel vertPanel = new VerticalPanel();
        add(vertPanel, BorderLayout.NORTH);
    }

    @Override
    public String getStaticLabel() {
        return "LoadDataPostProcessor.garuda@taobao";
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        LoadDataPostProcessor processor = new LoadDataPostProcessor();
        modifyTestElement(processor);
        processor.setComment("Comment.....");
        return processor;
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        configureTestElement(testElement);
    }
}
