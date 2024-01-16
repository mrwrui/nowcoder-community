package com.nowcoder.community.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AlphaConfig {

    //定义第三方的Bean
    @Bean
    //这个方法返回的对象将被装配到Bean中，Bean的名字是simpleDataFormat
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
