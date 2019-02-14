package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.User;
import com.example.springbatch.springbatchdemo.writer.XmlFileWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:shixianqing
 * @Date:2019/2/14 19:57
 * @Description: 从xml文件读取数据
 **/
@Configuration
public class XmlItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job xmlItemJob(){

        return jobBuilderFactory.get("xmlItemJob")
                .start(xmlItemStep())
                .build();
    }

    @Bean
    public Step xmlItemStep() {

        return stepBuilderFactory.get("xmlItemStep")
                .<User,User>chunk(10)
                .reader(xmlItemReader())
                .writer(xmlItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<User> xmlItemWriter() {

        return new XmlFileWriter();
    }

    @Bean
    public ItemReader<User> xmlItemReader() {

        StaxEventItemReader<User> staxEventItemReader = new StaxEventItemReader<>();
        staxEventItemReader.setFragmentRootElementName("user");//设置根标签
        staxEventItemReader.setResource(new ClassPathResource("/data/user.xml"));//设置文件路径

        //将xml转成实体对象
        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        Map<String,Class> alias = new HashMap<>();
        alias.put("user",User.class);//key 为根标签，class是key标签下的所有标签映射到的对象
        xStreamMarshaller.setAliases(alias);

        staxEventItemReader.setUnmarshaller(xStreamMarshaller);

        return staxEventItemReader;
    }


}
