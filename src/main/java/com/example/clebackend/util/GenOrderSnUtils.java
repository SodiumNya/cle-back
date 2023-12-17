package com.example.clebackend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.UUID;

public class GenOrderSnUtils {
    private static final AtomicLong counter = new AtomicLong(0);

    public static String generate() {
        long timestamp = System.currentTimeMillis();
        long orderNumber = counter.getAndIncrement();

        String uuid = UUID.randomUUID().toString();

        // Generate a UUID for the shop
        String orderSn = generateShortUUID(uuid + "-" + timestamp + "-" + orderNumber);

        // Ensure uniqueness across different shops

        return "cle"+orderSn;
    }


    //采用哈希算法压缩uuid成16位
    private static String generateShortUUID(String uuid) {
        //生成uuid
//        String uuid = UUID.randomUUID().toString();
        try {
            // 使用 SHA-1 哈希算法生成 16 个字符的哈希
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(uuid.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte hashByte : hashBytes) {
                sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }

            // 返回经过哈希处理的 UUID 的前 16 个字符
            return sb.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating short UUID", e);
        }
    }
}


