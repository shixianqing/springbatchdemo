package com.example.springbatch.springbatchdemo.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 16:28
 * @Description:
 **/
@Component("retryDemoWriter")
public class RetryDemoWriter implements ItemWriter<String> {


    @Override
    public void write(List<? extends String> items) throws Exception {

       for (String item:items){
           System.out.println("----------输出："+item+"----------------");
       }
    }
}
