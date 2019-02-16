package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.Hospital;
import com.example.springbatch.springbatchdemo.reader.RestartItemReader;
import com.example.springbatch.springbatchdemo.writer.FlatFileWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:shixianqing
 * @Date:2019/2/16 17:49
 * @Description:
 **/
@Configuration
public class RestartItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private RestartItemReader restartItemReader;

    @Autowired
    private FlatFileWriter flatFileWriter;

    @Bean
    public Job restartItemReaderDemoJob(){
        return jobBuilderFactory.get("restartItemReaderDemoJob")
                .start(restartItemReaderDemoStep())
                .build();
    }

    @Bean
    public Step restartItemReaderDemoStep() {

        return stepBuilderFactory.get("restartItemReaderDemoStep")
                .<Hospital,Hospital>chunk(10)
                .reader(restartItemReader)
                .writer(flatFileWriter)
                .build();
    }
}
