package com.taobao.garuda.demo.sampler.gui;

import com.taobao.garuda.demo.sampler.LoadDataSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午4:56
 */
public class LoadDataSamplerGui extends AbstractSamplerGui {

    public LoadDataSamplerGui(){
        JPanel container = new JPanel(new BorderLayout());
        add(container, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "LoadDataSampler.garuda@taobao";
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        LoadDataSampler sampler = new LoadDataSampler();
        modifyTestElement(sampler);
        sampler.setComment("XXXXXX");
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        configureTestElement(testElement);
    }
}
