package com.hce.paymentgateway.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author Heling.Yao
 * @Date 16:36 2018/5/30
 */
@Constraint(validatedBy = DBSDataValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DBSData {

    // 默认不能为空
    boolean canBeNull() default false;

    int maxLength();

    String[] enumValue() default {};

    DataType dateType();

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
