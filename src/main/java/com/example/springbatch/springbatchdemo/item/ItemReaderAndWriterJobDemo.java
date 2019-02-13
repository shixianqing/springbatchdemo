package com.example.springbatch.springbatchdemo.item;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/13 14:38
 * @Description:
 **/
//@Configuration
public class ItemReaderAndWriterJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemReaderAndWriterJob(){

        return jobBuilderFactory.get("itemReaderAndWriterJob")
                .start(itemReaderAndWriterStep())
                .build();
    }

    @Bean
    public Step itemReaderAndWriterStep() {
        return stepBuilderFactory.get("itemReaderAndWriterStep")
                .<String,String>chunk(1)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemWriter<String> itemWriter() {

        return new ItemWriter<String>() {
            @Override
            public void write(List items) throws Exception {
                Iterator iterator = items.iterator();
                if (iterator.hasNext()){
                    System.out.println(iterator.next());
                }
            }
        };
    }

    @Bean
    public ItemReader<String> itemReader() {
        return new ListItemReader<String>(Arrays.asList("zhangsan","lisi","wangwu","zhaoliu","nihao"));
    }
}
