package com.fastcampus.programming.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.fastcampus.programming.spring.dto.SpringErrorResponse;
import com.fastcampus.programming.spring.exception.SpringException;

import lombok.extern.slf4j.Slf4j;

/*
 * @author Snow
 */
@Slf4j
@RestController
public class CMakerController {

    @ExceptionHandler(SpringException.class)
    public SpringErrorResponse handleException(SpringException e, HttpServletRequest request) {
        log.error("errorCode: {}, url: {}, message: {}}", e.getSpringErrorCode(), request.getRequestURI(), e.getDetailMessage());

        return SpringErrorResponse.builder()
                    .errCode(e.getSpringErrorCode())
                    .errorMessage(e.getDetailMessage())
                    .build();
    }

}