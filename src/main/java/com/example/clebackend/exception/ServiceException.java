package com.example.clebackend.exception;

import lombok.Getter;


@Getter
public class ServiceException extends RuntimeException{
    private int code;

    public ServiceException(String msg){
        super(msg);
    }

    public ServiceException(int code, String msg){
        super(msg);
        this.code = code;
    }
}
