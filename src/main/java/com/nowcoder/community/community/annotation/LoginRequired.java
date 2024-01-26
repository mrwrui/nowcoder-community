package com.nowcoder.community.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 指这个注解可以写在方法之上，可以描述方法
@Target(ElementType.METHOD)
// 声明这个注解有效的时常
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
