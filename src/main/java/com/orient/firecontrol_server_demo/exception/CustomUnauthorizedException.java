package com.orient.firecontrol_server_demo.exception;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 22:19
 * @func
 */
public class CustomUnauthorizedException extends RuntimeException {
    public CustomUnauthorizedException(String msg){
        super(msg);
    }
}
