package com.example.clebackend.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeFormatUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");


    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static String coverToDate(Timestamp timestamp){
        Date time = new Date(timestamp.getTime());
        return dateFormat.format(time);
    }

    public static String coverToSecondData(Timestamp timestamp){
        Date time = new Date(timestamp.getTime());
        return secondFormat.format(time);
    }

    public static String coverToTime(Timestamp timestamp){
        Date time = new Date(timestamp.getTime());
        return sdf.format(time);
    }

    public static Timestamp getTimestampFromTimeString(String timeString) {
        // 获取当前日期
        LocalDateTime currentDate = LocalDateTime.now().toLocalDate().atStartOfDay();

        // 将时间字符串解析为LocalTime对象
        LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));

        // 创建带有当前日期的LocalDateTime对象
        LocalDateTime localDateTime = currentDate.with(time);

        // 将LocalDateTime对象转换为Timestamp
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp getTimeStamp(){
        return Timestamp.valueOf(LocalDateTime.now());

    }

    public static LocalDate CoverToLocalDate(Timestamp time){
        return time.toLocalDateTime().toLocalDate();
    }


}
