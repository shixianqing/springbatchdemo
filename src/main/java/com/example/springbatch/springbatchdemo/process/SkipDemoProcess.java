package com.example.springbatch.springbatchdemo.process;

import com.example.springbatch.springbatchdemo.exception.CustomerRetryException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 16:14
 * @Description:
 **/
@Component("skipDemoProcess")
public class SkipDemoProcess implements ItemProcessor<String,String> {

    @Override
    public String process(String item) throws Exception {

        System.out.println("processing item is "+item);

        if ("27".equals(item)){
            throw new CustomerRetryException("process failed");
        }
        return String.valueOf(Integer.valueOf(item) * -1);
    }
}
