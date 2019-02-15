package com.example.springbatch.springbatchdemo.writer;

import com.example.springbatch.springbatchdemo.model.Hospital;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author:shixianqing
 * @Date:2019/2/15 18:49
 * @Description:
 **/
@Component
public class MultiFileItemWriter implements ItemWriter<Hospital> {

    @Override
    public void write(List<? extends Hospital> items) throws Exception {

        for(Hospital hospital:items){
            System.out.println(hospital);
        }

        System.out.println("----------------------");
    }
}
