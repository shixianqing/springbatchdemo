package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.Hospital;
import com.example.springbatch.springbatchdemo.writer.FlatFileWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;

/**
 * @Author:shixianqing
 * @Date:2019/2/13 17:58
 * @Description: 从文件中读取数据
 **/
@Configuration
public class FileItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileJob(){
        return jobBuilderFactory.get("fileJob")
                .start(fileStep())
                .build();
    }

    @Bean
    public Step fileStep() {
        return stepBuilderFactory.get("fileStep")
                .<Hospital,Hospital>chunk(100)
                .reader(fileReader())
                .writer(fileWriter())
                .build();
    }

    @Bean
    public ItemWriter<Hospital> fileWriter() {

        return new FlatFileWriter();
    }

    @Bean
    public ItemReader<Hospital> fileReader() {

        FlatFileItemReader<Hospital> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setEncoding("utf-8");
        flatFileItemReader.setLinesToSkip(1);//跳过第一行
        flatFileItemReader.setResource(new ClassPathResource("/data/hospital.txt"));

        //解析数据  按行分词
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("id","org_name","org_type","addr","allow_no","cert_dept",
                "start_valid_date","end_invalid_date");

        //行映射器
        DefaultLineMapper<Hospital> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Hospital>() {
            @Override
            public Hospital mapFieldSet(FieldSet fieldSet) throws BindException {
                Hospital hospital = new Hospital();
                hospital.setId(fieldSet.readInt("id"));
                hospital.setAddr(fieldSet.readString("addr"));
                hospital.setAllowNo(fieldSet.readString("allow_no"));
                hospital.setCertDept(fieldSet.readString("cert_dept"));
                hospital.setEndValidDate(fieldSet.readString("end_invalid_date"));
                hospital.setOrgName(fieldSet.readString("org_name"));
                hospital.setOrgType(fieldSet.readString("org_type"));
                hospital.setStartValidDate(fieldSet.readString("start_valid_date"));
                return hospital;
            }
        });
        defaultLineMapper.afterPropertiesSet();
        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;

    }
}
