package com.hce.paymentgateway.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Date {

    String formatter();

    String message() default "日期格式异常, 请校验数据格式!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
