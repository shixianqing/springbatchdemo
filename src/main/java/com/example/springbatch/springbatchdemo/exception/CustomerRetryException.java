package com.example.springbatch.springbatchdemo.exception;

/**
 * @Author:shixianqing
 * @Date:2019/2/23 16:20
 * @Description:
 **/
public class CustomerRetryException extends RuntimeException {

    private String msg;

    public CustomerRetryException(){
        super();
    }

    public CustomerRetryException(String msg){

        this.msg = msg;
    }
}
