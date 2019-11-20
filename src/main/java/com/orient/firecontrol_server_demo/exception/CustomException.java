package com.orient.firecontrol_server_demo.exception;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 10:12
 * @func 自定义异常类
 */
public class CustomException extends RuntimeException {
    public CustomException(String msg){
        super(msg);
    }
}
