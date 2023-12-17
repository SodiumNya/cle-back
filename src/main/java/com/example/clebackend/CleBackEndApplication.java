package com.example.clebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com/example/clebackend/mapper")
public class CleBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleBackEndApplication.class, args);
    }

}
