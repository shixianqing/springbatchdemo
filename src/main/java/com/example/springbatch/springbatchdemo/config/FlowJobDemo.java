package com.example.springbatch.springbatchdemo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:shixianqing
 * @Date:2019/1/3117:28
 * @Description: 每个flow可以由多个step组成，一个job可以由多个flow或step组成
 * 每个flow是由FlowBuilder创建
 **/
//@Configuration
public class FlowJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJob1(){
        return jobBuilderFactory.get("flowJob1").start(flow()).next(flowStep33()).end().build();
    }

    @Bean
    public Step flowStep11(){
        return stepBuilderFactory.get("flowStep11").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

                System.out.println("flowStep11");
                return RepeatStatus.FINISHED;
            }
        }).build();
    }

    @Bean
    public Step flowStep22(){
        return stepBuilderFactory.get("flowStep22").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

                System.out.println("flowStep22");
                int i = 1/0;
                return RepeatStatus.FINISHED;
            }
        }).build();
    }

    @Bean
    public Step flowStep33(){
        return stepBuilderFactory.get("flowStep33").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

                System.out.println("flowStep33");
                return RepeatStatus.FINISHED;
            }
        }).build();
    }

    @Bean
    public Flow flow(){
        return new FlowBuilder<Flow>("flow").start(flowStep11())
                .next(flowStep22()).build();
    }


}
