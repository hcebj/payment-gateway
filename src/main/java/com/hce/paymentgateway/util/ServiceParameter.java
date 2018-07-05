package com.hce.paymentgateway.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author Heling.Yao
 * @Date 10:18 2018/5/25
 */
@Target({ ElementType.TYPE })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ServiceParameter {

    String productType();

}
