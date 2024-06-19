package com.curtis.userspherebackend.exception;

import com.curtis.userspherebackend.common.BaseResponse;

import com.curtis.userspherebackend.common.ErrorCode;
import com.curtis.userspherebackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    public BaseResponse rentimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException: " + e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }

}
