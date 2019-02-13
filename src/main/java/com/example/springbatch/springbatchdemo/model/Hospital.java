package com.example.springbatch.springbatchdemo.model;

import lombok.Data;

/**
 * @Author:shixianqing
 * @Date:2019/2/13 18:39
 * @Description:
 **/
@Data
public class Hospital {

    private Integer id;
    private String orgName;
    private String orgType;
    private String addr;
    private String allowNo;
    private String certDept;
    private String startValidDate;
    private String endValidDate;
}
