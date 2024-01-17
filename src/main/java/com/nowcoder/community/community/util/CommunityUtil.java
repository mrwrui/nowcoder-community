package com.nowcoder.community.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // MD5算法只能加密不能解密 hello -> abc123def456
    // hello + 3e4a8 -> abc123def456abc 密码的后面加上一个随机的字符串然后再去加密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        } else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }
}
