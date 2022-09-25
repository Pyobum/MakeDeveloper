package com.fastcampus.programming.spring.exception;

import lombok.Getter;

@Getter
public class SpringException extends RuntimeException {
    private SpringErrorCode springErrorCode;
    private String detailMessage;

    public SpringException(SpringErrorCode errorCode) {
        super(errorCode.getMessage());
        this.springErrorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }
    
    public SpringException(SpringErrorCode errorCode, String detaillMessage) {
        super(errorCode.getMessage());
        this.springErrorCode = errorCode;
        this.detailMessage = detaillMessage;
    }
}
