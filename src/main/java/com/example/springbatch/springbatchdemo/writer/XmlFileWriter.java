package com.example.springbatch.springbatchdemo.writer;

import org.springframework.batch.item.ItemWriter;

import java.util.Iterator;
import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/14 20:19
 * @Description:
 **/
public class XmlFileWriter implements ItemWriter {

    @Override
    public void write(List items) throws Exception {
        Iterator iterator = items.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

        System.out.println("==============");
    }
}
