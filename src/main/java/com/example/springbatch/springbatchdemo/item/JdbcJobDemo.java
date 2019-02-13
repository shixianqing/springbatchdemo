package com.example.springbatch.springbatchdemo.item;

import com.example.springbatch.springbatchdemo.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author:shixianqing
 * @Date:2019/2/13 15:13
 * @Description: 从数据库中读取
 **/
//@Configuration
public class JdbcJobDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job jdbcJob(){

        return jobBuilderFactory.get("jdbcJob")
                .start(jdbcStep())
                .build();
    }

    @Bean
    public Step jdbcStep() {
        return stepBuilderFactory.get("jdbcStep")
                .<User,User>chunk(2)
                .reader(jdbcItemReader())
                .writer(jdbcItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<User> jdbcItemWriter() {
        FlatFileItemWriter flatFileItemWriter = new FlatFileItemWriter<>();
        flatFileItemWriter.setEncoding("utf-8");
        DelimitedLineAggregator<User> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.doAggregate(new String[]{"id","name","password"});
        flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
        flatFileItemWriter.setResource(new ClassPathResource("user.txt"));
        return flatFileItemWriter;
    }

    @Bean
    public ItemReader<User> jdbcItemReader() {
        JdbcPagingItemReader<User> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(3);//每次从数据库拉取条数

        MySqlPagingQueryProvider pagingQueryProvider = new MySqlPagingQueryProvider();
        pagingQueryProvider.setSelectClause("id,name,password");
        pagingQueryProvider.setFromClause("user");
        Map<String, Order> orderMap = new HashMap<>();
        orderMap.put("password",Order.DESCENDING);//设置排序字段
        pagingQueryProvider.setSortKeys(orderMap);
        jdbcPagingItemReader.setQueryProvider(pagingQueryProvider);

        //设置行映射器
        jdbcPagingItemReader.setRowMapper(new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt(1));
                user.setName(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
                return user;
            }
        });
        return jdbcPagingItemReader;
    }
}
