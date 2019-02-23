package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.Hospital;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:shixianqing
 * @Date:2019/2/17 16:03
 * @Description: 从数据库读取数据写入到xml文件中
 **/
//@Configuration
public class XmlFileItemWriterJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job xmlFileItemWriterJob(){
        return jobBuilderFactory.get("xmlFileItemWriterJob")
                .start(xmlFileItemWriterStep())
                .build();
    }

    @Bean
    public Step xmlFileItemWriterStep() {

        return stepBuilderFactory.get("xmlFileItemWriterStep")
                .<Hospital,Hospital>chunk(100)
                .reader(jdbcItemReader())
                .writer(xmlFileItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Hospital> xmlFileItemWriter() {
        StaxEventItemWriter<Hospital> staxEventItemWriter = new StaxEventItemWriter<>();
        staxEventItemWriter.setEncoding("utf-8");
        staxEventItemWriter.setResource(new FileSystemResource("f:/hospital.xml"));
        staxEventItemWriter.setRootTagName("hospitals");

        //XML标签与实体对象映射
        Map<String,Class<Hospital>> alias = new HashMap<>();
        alias.put("hospital",Hospital.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(alias);

        staxEventItemWriter.setMarshaller(xStreamMarshaller);

        return staxEventItemWriter;

    }

    @Bean
    public ItemReader<Hospital> jdbcItemReader() {

        JdbcPagingItemReader<Hospital> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setFetchSize(100);
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

//        jdbcPagingItemReader.afterPropertiesSet();

        return jdbcPagingItemReader;

    }
}
