package com.example.clebackend.common;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.Data;


@Data
public class RestBean<T>{
    private int code;
    private T data;
    private String message;

    public RestBean(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> RestBean<T> success(T data){
        return new RestBean<>(ResponseCode.SUCCESS.getCode(), data, ResponseCode.SUCCESS.getMsg());
    }

    public static <T> RestBean<T> error(int code, String message){
        return new RestBean<>(code, null, message);
    }

    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}

