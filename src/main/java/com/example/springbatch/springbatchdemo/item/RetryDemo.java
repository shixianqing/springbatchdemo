package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.exception.CustomerRetryException;
import com.example.springbatch.springbatchdemo.process.RetryDemoProcess;
import com.example.springbatch.springbatchdemo.writer.RetryDemoWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 16:06
 * @Description: 错误重试
 **/
//@Configuration
public class RetryDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private RetryDemoWriter retryDemoWriter;

    @Autowired
    @Qualifier("retryDemoProcess")
    private RetryDemoProcess retryDemoProcess;


    @Bean
    public Job retryDemoJob(){
        return jobBuilderFactory.get("retryDemoJob")
                .start(retryDemoStep())
                .build();
    }

    @Bean
    public Step retryDemoStep() {
        return stepBuilderFactory.get("retryDemoStep")
                .<String,String>chunk(10)
                .reader(retryDemoReader())
                .processor(retryDemoProcess)
                .writer(retryDemoWriter)
                .faultTolerant()//容错
                .retry(CustomerRetryException.class)//出现CustomerRetryException异常时，进行重试
                .retryLimit(5)//重试次数，超过次数，任务结束
                .build();
    }


    @Bean
    public ListItemReader<String> retryDemoReader() {
        List<String> list = new ArrayList<>();

        for (int i=1;i<60;i++){
            list.add(String.valueOf(i));
        }

        return new ListItemReader<>(list);
    }
    
    
}
