package com.thor.springboot.server.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

/**
 * @author huangpin
 * @date 2018-12-10
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BindException.class)
    public String bindException(BindException ex) {
        return Optional.ofNullable(ex.getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("系统异常");
    }

    @ExceptionHandler(value = Exception.class)
    public String defaultExceptionHandler(Exception ex) {
        return "抛出了基础异常";
    }

}