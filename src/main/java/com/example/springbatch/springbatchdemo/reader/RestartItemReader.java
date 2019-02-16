package com.example.springbatch.springbatchdemo.reader;

import com.example.springbatch.springbatchdemo.model.Hospital;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

/**
 * @Author:shixianqing
 * @Date:2019/2/16 16:38
 * @Description: itemReader读取数据异常处理
 **/
@Component("restartItemReader")
public class RestartItemReader implements ItemStreamReader<Hospital> {

    private FlatFileItemReader<Hospital> flatFileItemReader = new FlatFileItemReader<>();
    private Long curLine = 0L;//记录当前读取行数
    private Boolean restart = false;
    private ExecutionContext executionContext;

    public void restartReader(){
        flatFileItemReader.setEncoding("utf-8");
        flatFileItemReader.setResource(new ClassPathResource("classpath:/data/hospital_bak.txt"));
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("id","org_name","org_type","addr","allow_no","cert_dept",
                "start_valid_date","end_invalid_date");

        DefaultLineMapper<Hospital> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Hospital>() {
            @Override
            public Hospital mapFieldSet(FieldSet fieldSet) throws BindException {
                Hospital hospital = new Hospital();
                hospital.setId(fieldSet.readInt("id"));
                hospital.setAddr(fieldSet.readString("addr"));
                hospital.setAllowNo(fieldSet.readString("allow_no"));
                hospital.setCertDept(fieldSet.readString("cert_dept"));
                hospital.setEndValidDate(fieldSet.readString("end_invalid_date"));
                hospital.setOrgName(fieldSet.readString("org_name"));
                hospital.setOrgType(fieldSet.readString("org_type"));
                hospital.setStartValidDate(fieldSet.readString("start_valid_date"));
                return hospital;
            }
        });
        defaultLineMapper.afterPropertiesSet();
        flatFileItemReader.setLineMapper(defaultLineMapper);
    }

    @Override
    public Hospital read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        this.curLine++;
        if (restart){
            flatFileItemReader.setLinesToSkip(curLine.intValue());
            restart = false;
            System.out.println("start reading from line："+curLine);
        }
        flatFileItemReader.open(executionContext);
        Hospital hospital = flatFileItemReader.read();
        if (hospital.getId() == 33){
            throw new RuntimeException("wrong！hospital's id is "+hospital.getId()+"，curline is "+this.curLine);
        }

        return hospital;
    }

    /**
     *  文件读取前执行
     * @param executionContext
     * @throws ItemStreamException
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        restartReader();
        this.executionContext = executionContext;
        if (executionContext.containsKey("curLine")){
            this.curLine = executionContext.getLong("curLine");
            this.restart = true;
        } else {
            this.curLine = 0L;
            executionContext.put("curLine",this.curLine);
        }

        System.out.println("start reading from line："+this.curLine);
    }

    /**
     * 一组数据处理完之后执行，出现异常后，不执行
     * @param executionContext
     * @throws ItemStreamException
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("curLine",this.curLine);
        System.out.println("update----------curLine："+this.curLine);
    }

    /**
     * 全部数据处理完之后执行
     * @throws ItemStreamException
     */
    @Override
    public void close() throws ItemStreamException {

    }
}
