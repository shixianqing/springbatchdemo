package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.exception.CustomerRetryException;
import com.example.springbatch.springbatchdemo.listener.MySkipListener;
import com.example.springbatch.springbatchdemo.process.SkipDemoProcess;
import com.example.springbatch.springbatchdemo.writer.SkipDemoWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 17:55
 * @Description:
 **/
@Configuration
public class SkipListenerDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SkipDemoProcess skipDemoProcess;

    @Autowired
    private SkipDemoWriter skipDemoWriter;

    @Autowired
    private MySkipListener mySkipListener;

    @Bean
    public Job skipListenerDemoJob(){
        return jobBuilderFactory.get("skipListenerDemoJob")
                .start(skipListenerDemoStep())
                .build();
    }

    @Bean
    public Step skipListenerDemoStep() {

        return stepBuilderFactory.get("skipListenerDemoStep")
                .<String,String>chunk(10)
                .reader(skipListenerDemoReader())
                .processor(skipDemoProcess)
                .writer(skipDemoWriter)
                .faultTolerant()
                .skip(CustomerRetryException.class)
                .skipLimit(5)
                .listener(mySkipListener)
                .build();
    }

    @Bean
    public ListItemReader<String> skipListenerDemoReader() {
        List<String> list = new ArrayList<>();

        for (int i=1;i<60;i++){
            list.add(String.valueOf(i));
        }

        return new ListItemReader<>(list);
    }
}
