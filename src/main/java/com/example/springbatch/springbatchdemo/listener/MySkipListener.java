package com.example.springbatch.springbatchdemo.listener;

import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 17:45
 * @Description:
 **/
@Component
public class MySkipListener implements SkipListener<String,String> {

    @Override
    public void onSkipInRead(Throwable t) {

    }

    @Override
    public void onSkipInWrite(String item, Throwable t) {

    }

    @Override
    public void onSkipInProcess(String item, Throwable t) {

        System.out.println("processing "+item+" 时，出现异常，异常信息："+t);
    }
}
