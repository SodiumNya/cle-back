package com.example.clebackend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.UUID;

public class InvitationCodeUtils {

    private static final int EXPIRATION_PERIOD_MINUTES = 60; // 过期时间，单位为分钟

    public static class InvitiationCodeInfo {
        private final String code;
        private final Timestamp creationTime;

        public InvitiationCodeInfo(String code, Timestamp creationTime) {
            this.code = code;
            this.creationTime = creationTime;
        }

        public String getCode() {
            return code;
        }

        public Timestamp getCreationTime() {
            return creationTime;
        }
    }

    // 生成带有过期时间的邀请码信息
    public static InvitiationCodeInfo generateInvitationCode(int length) {
        if (length != 6 && length != 8) {
            throw new IllegalArgumentException("Invitation code length must be 6 or 8");
        }

        // 生成唯一标识符
        String uniqueId = UUID.randomUUID().toString().replace("-", "");


        // 截取指定长度的部分作为邀请码
        String invitationCode = uniqueId.substring(0, length);

        return new InvitiationCodeInfo(invitationCode, TimeFormatUtils.getTimeStamp());
    }

    // 对邀请码进行哈希处理
    public static String hashInvitationCode(String invitationCode) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(invitationCode.getBytes());

        // 将字节数组转换为十六进制字符串
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }

//    public static void main(String[] args) {
//        try {
//            // 生成带过期时间的邀请码信息示例
//            InvitiationCodeInfo codeInfo = generateInvitationCode(6);
//            System.out.println("Generated invitation code: " + codeInfo.getCode());
//
//            // 验证邀请码是否过期示例
//            boolean isExpired = isInvitationCodeExpired(codeInfo);
//            System.out.println("Is invitation code expired? " + isExpired);
//
//            // 对邀请码进行哈希处理示例
//            String hashedCode = hashInvitationCode(codeInfo.getCode());
//            System.out.println("Hashed invitation code: " + hashedCode);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }
}
