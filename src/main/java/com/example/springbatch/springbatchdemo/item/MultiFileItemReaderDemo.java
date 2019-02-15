package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.Hospital;
import com.example.springbatch.springbatchdemo.writer.MultiFileItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

/**
 * @Author:shixianqing
 * @Date:2019/2/15 16:29
 * @Description: 多文件读取
 **/
@Configuration
public class MultiFileItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("classpath:/data/file*.txt")
    private Resource[] resources;


    @Bean
    public Job multiFileJob(){

        return jobBuilderFactory.get("multiFileJob")
                .start(multiFileStep())
                .build();
    }

    @Bean
    public Step multiFileStep() {

        return stepBuilderFactory.get("multiFileStep")
                .<Hospital,Hospital>chunk(10)
                .reader(multiFileItemReader())
                .writer(multiFileItemWriter())
                .build();
    }

    public ItemWriter<Hospital> multiFileItemWriter() {

        return new MultiFileItemWriter();
    }

    @Bean
    public MultiResourceItemReader<Hospital> multiFileItemReader() {

        MultiResourceItemReader<Hospital> multiResourceItemReader = new MultiResourceItemReader<>();
        //多文件读取，其实也是一个一个文件进行读取，需要设置文件读取器
        multiResourceItemReader.setDelegate(flatFileItemReader());
        multiResourceItemReader.setResources(resources);
        return multiResourceItemReader;
    }

    @Bean
    public FlatFileItemReader<Hospital> flatFileItemReader(){
        FlatFileItemReader<Hospital> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setEncoding("utf-8");

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("id","org_name","org_type","addr","allow_no","cert_dept",
                "start_valid_date","end_invalid_date");

        DefaultLineMapper<Hospital> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Hospital>() {
            @Override
            public Hospital mapFieldSet(FieldSet fieldSet) throws BindException {
                Hospital hospital = new Hospital();
                hospital.setStartValidDate(fieldSet.readString("start_valid_date"));
                hospital.setOrgType(fieldSet.readString("org_type"));
                hospital.setOrgName(fieldSet.readString("org_name"));
                hospital.setEndValidDate(fieldSet.readString("end_invalid_date"));
                hospital.setCertDept(fieldSet.readString("cert_dept"));
                hospital.setAllowNo(fieldSet.readString("allow_no"));
                hospital.setAddr(fieldSet.readString("addr"));
                hospital.setId(fieldSet.readInt("id"));
                return hospital;
            }
        });

        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    }
}
