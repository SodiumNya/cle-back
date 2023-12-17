package com.example.clebackend.dto;


import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class UserReviews {

    private Integer id;

    private String orderSn;

    private Double score;

    private String message;

    private Timestamp time;

}
