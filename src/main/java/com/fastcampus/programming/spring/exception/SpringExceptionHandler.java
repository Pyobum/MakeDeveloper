package com.fastcampus.programming.spring.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fastcampus.programming.spring.dto.SpringErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class SpringExceptionHandler {
    @ExceptionHandler(SpringException.class)
    public SpringErrorResponse handleException(SpringException e, HttpServletRequest request) {
        log.error("errorCode: {}, url: {}, message: {}}", e.getSpringErrorCode(), request.getRequestURI(), e.getDetailMessage());

        return SpringErrorResponse.builder()
                    .errCode(e.getSpringErrorCode())
                    .errorMessage(e.getDetailMessage())
                    .build();
    }

    @ExceptionHandler(value = {
        HttpRequestMethodNotSupportedException.class,
        MethodArgumentNotValidException.class
    })
    public SpringErrorResponse handleBadRequest(Exception e, HttpServletRequest request) {
        log.error("url: {}, message: {}}", request.getRequestURI(), e.getMessage());

        return SpringErrorResponse.builder()
                    .errCode(SpringErrorCode.INVAILD_REQUEST)
                    .errorMessage(SpringErrorCode.INVAILD_REQUEST.getMessage())
                    .build();
    }

    @ExceptionHandler(Exception.class)
    public SpringErrorResponse handleException(Exception e, HttpServletRequest request) {
        log.error("url: {}, message: {}}", request.getRequestURI(), e.getMessage());

        return SpringErrorResponse.builder()
                    .errCode(SpringErrorCode.INTERNAL_SERVER_ERROR)
                    .errorMessage(SpringErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                    .build();
    }
}
