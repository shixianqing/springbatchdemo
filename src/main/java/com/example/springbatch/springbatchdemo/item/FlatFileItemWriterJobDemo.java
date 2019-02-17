package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.Hospital;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:shixianqing
 * @Date:2019/2/17 14:37
 * @Description:
 **/
//@Configuration
public class FlatFileItemWriterJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job flatFileItemWriterJob() throws Exception {
        return jobBuilderFactory.get("flatFileItemWriterJob")
                .start(flatFileItemWriterStep())
                .build();
    }

    @Bean
    public Step flatFileItemWriterStep() throws Exception {

        return stepBuilderFactory.get("flatFileItemWriterStep")
                .<Hospital,Hospital>chunk(10)
                .reader(flatFileItemWriterReader())
                .writer(flatFileItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Hospital> flatFileItemWriter() {

        FlatFileItemWriter<Hospital> flatFileItemWriter = new FlatFileItemWriter<>();
        flatFileItemWriter.setEncoding("utf-8");
        //设置存放数据的文件路径
        flatFileItemWriter.setResource(new FileSystemResource("f:/hospital_generate.txt"));

        flatFileItemWriter.setLineAggregator(new LineAggregator<Hospital>() {
            ObjectMapper objectMapper = new ObjectMapper();
            @Override
            public String aggregate(Hospital item) {
                String result = null;
                try {
                    result = objectMapper.writeValueAsString(item);//将对象转json字符串
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                return result;
            }
        });
        return flatFileItemWriter;

    }

    @Bean
    public ItemReader<? extends Hospital> flatFileItemWriterReader() throws Exception {

        JdbcPagingItemReader<Hospital> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setFetchSize(10);
        jdbcPagingItemReader.setDataSource(dataSource);

        MySqlPagingQueryProvider mySqlPagingQueryProvider = new MySqlPagingQueryProvider();
        mySqlPagingQueryProvider.setSelectClause("id,org_name,org_type,addr,allow_no,cert_dept,start_valid_date,end_invalid_date");
        mySqlPagingQueryProvider.setFromClause("hospital");

        Map<String, Order> orderMap = new HashMap<>();
        orderMap.put("id",Order.ASCENDING);
        mySqlPagingQueryProvider.setSortKeys(orderMap);

        jdbcPagingItemReader.setQueryProvider(mySqlPagingQueryProvider);

        jdbcPagingItemReader.setRowMapper(new RowMapper<Hospital>() {
            @Override
            public Hospital mapRow(ResultSet rs, int rowNum) throws SQLException {
                Hospital hospital = new Hospital();
                hospital.setId(rs.getInt(1));
                hospital.setOrgName(rs.getString(2));
                hospital.setOrgType(rs.getString(3));
                hospital.setAddr(rs.getString(4));
                hospital.setAllowNo(rs.getString(5));
                hospital.setCertDept(rs.getString(6));
                hospital.setStartValidDate(rs.getString(7));
                hospital.setEndValidDate(rs.getString(8));
                return hospital;
            }
        });

        jdbcPagingItemReader.afterPropertiesSet();

        return jdbcPagingItemReader;

    }


}
