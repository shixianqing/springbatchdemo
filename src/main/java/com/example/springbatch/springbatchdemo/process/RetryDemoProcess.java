package com.example.springbatch.springbatchdemo.process;

import com.example.springbatch.springbatchdemo.exception.CustomerRetryException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 16:14
 * @Description:
 **/
@Component("retryDemoProcess")
public class RetryDemoProcess implements ItemProcessor<String,String> {

    private int attemptCount;

    @Override
    public String process(String item) throws Exception {

        System.out.println("processing item is "+item);

        if ("26".equals(item)){
            attemptCount++;
            if (attemptCount >= 3){
                System.out.println("retried "+attemptCount+" times success");
                return String.valueOf(Integer.valueOf(item) * -1);
            } else {

                throw new CustomerRetryException("process failed，attempt："+attemptCount);
            }
        }
        return String.valueOf(Integer.valueOf(item) * -1);
    }
}
