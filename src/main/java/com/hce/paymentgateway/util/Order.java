package com.hce.paymentgateway.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author Heling.Yao
 * @Date 14:48 2018/5/24
 */
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Order {

    int order();

}
