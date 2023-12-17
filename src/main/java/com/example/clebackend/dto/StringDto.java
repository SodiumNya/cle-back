package com.example.clebackend.dto;


import com.example.clebackend.exception.ServiceException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author wangqi
 * 专门用来接收字符串数据
 */



public class StringDto {

    @NotNull(message = "传输数据不能为空")
    private Object data;

    public StringDto() {}

    //传进来的是全数字时，会默认是int, 此时需要它
    StringDto(int data){
        this.data = data;
    }
    StringDto(Object data){
        this.data = data;
    }

    StringDto(String data){
        this.data = data;
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
