package com.orient.firecontrol_server_demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 22:20
 * @func
 */
@RestControllerAdvice
public class ExceptionAdvice {







//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(TokenExpiredException.class)
//    public ResultBean handle401(TokenExpiredException e) {
//        return new ResultBean(HttpStatus.UNAUTHORIZED.value(), "无权访问(Unauthorized):" + e.getMessage(), null);
//    }




    /**
     * 获取状态码
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
