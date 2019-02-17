package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.Hospital;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
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

import javax.sql.DataSource;

/**
 * @Author:shixianqing
 * @Date:2019/2/17 13:21
 * @Description: 从文件中读取数据并写入到数据库中
 **/
//@Configuration
public class JdbcItemWriterJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job jdbcItemWriterJob(){
        return jobBuilderFactory.get("jdbcItemWriterJob")
                .start(jdbcItemWriterStep())
                .build();
    }

    @Bean
    public Step jdbcItemWriterStep() {

        return stepBuilderFactory.get("jdbcItemWriterStep")
                .<Hospital,Hospital>chunk(10)
                .reader(flatFileReader())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<Hospital> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Hospital> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setSql(
                "INSERT INTO `hospital` (`id`, `org_name`, `org_type`, `addr`, `allow_no`, `cert_dept`, `start_valid_date`, `end_invalid_date`) " +
                        "VALUES (:id, :orgName, :orgType, :addr, :allowNo, :certDept, :startValidDate, :endValidDate)"
        );
        //将对象属性值映射到sql参数中
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return jdbcBatchItemWriter;
    }

    @Bean
    public ItemReader<Hospital> flatFileReader() {
        FlatFileItemReader<Hospital> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setEncoding("utf-8");
        flatFileItemReader.setResource(new ClassPathResource("/data/hospital.txt"));
        flatFileItemReader.setLinesToSkip(1);//跳过第一行

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        //设置表头
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
