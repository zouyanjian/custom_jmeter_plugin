package com.taobao.garuda.demo.sampler;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

/**
 * User: wb-zouyanjian
 * Date: 13-7-3
 * Time: 上午10:45
 */
public class LoadDataSamplerTest {
    @Test
    public void should_print_results_success() throws Exception {
        JMeterVariables variables = new JMeterVariables();
        variables.put("sql", "select count(1) from test4dmp.test__0;");
        JMeterContextService.getContext().setVariables(variables);
        LoadDataSampler loadDataSampler = new LoadDataSampler();
        SampleResult sample = loadDataSampler.sample(null);
        String result="Record Count:1\n" +"COUNT(1)\n9\n";
        Assertions.assertThat(result).isEqualTo(new String(sample.getResponseData()));
        System.out.println(new String(sample.getResponseData()));
    }
}
