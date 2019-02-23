package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.exception.CustomerRetryException;
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
import sun.dc.pr.PRError;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 17:20
 * @Description: 错误跳过
 **/
//@Configuration
public class SkipDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SkipDemoProcess skipDemoProcess;

    @Autowired
    private SkipDemoWriter skipDemoWriter;

    @Bean
    public Job skipDemoJob(){
        return jobBuilderFactory.get("skipDemoJob")
                .start(skipDemoStep())
                .build();
    }

    @Bean
    public Step skipDemoStep() {

       return stepBuilderFactory.get("skipDemoStep")
                .<String,String>chunk(10)
                .reader(skipDemoReader())
                .processor(skipDemoProcess)
                .writer(skipDemoWriter)
                .faultTolerant()
                .skip(CustomerRetryException.class)
                .skipLimit(5)
                .build();
    }

    @Bean
    public ListItemReader<String> skipDemoReader() {
        List<String> list = new ArrayList<>();

        for (int i=1;i<60;i++){
            list.add(String.valueOf(i));
        }

        return new ListItemReader<>(list);
    }
}
