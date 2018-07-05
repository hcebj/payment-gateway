package com.hce.paymentgateway.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author Heling.Yao
 * @Date 18:34 2018/6/6
 */
@Target( { ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ConditionalMandatory {

    String associatedField();

    String associatedConditionValue() default "";

}
