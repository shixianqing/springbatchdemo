package com.example.springbatch.springbatchdemo.writer;

import com.example.springbatch.springbatchdemo.model.Hospital;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/13 19:16
 * @Description:
 **/
@Component
public class FlatFileWriter implements ItemWriter<Hospital> {

    @Override
    public void write(List<? extends Hospital> items) throws Exception {
        Iterator iterator = items.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

        System.out.println("=====================");
    }
}
