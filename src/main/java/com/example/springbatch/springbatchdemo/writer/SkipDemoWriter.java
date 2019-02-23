package com.example.springbatch.springbatchdemo.writer;

import com.example.springbatch.springbatchdemo.exception.CustomerRetryException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 16:28
 * @Description:
 **/
@Component("skipDemoWriter")
public class SkipDemoWriter implements ItemWriter<String> {


    @Override
    public void write(List<? extends String> items) throws Exception {

       for (String item:items){
           System.out.println("----------输出："+item+"----------------");
       }
    }
}
