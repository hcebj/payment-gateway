package com.hce.paymentgateway.validate;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * @Author Heling.Yao
 * @Date 11:07 2018/5/29
 */
public class ValidatorUtil {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory()
        .getValidator();

    public static <T> ValidatorResult validate(T obj) {
        ValidatorResult result = new ValidatorResult();
        if(obj == null) {
            result.setAvailable(true);
            result.setMessage("请求参数为空, 请查看参数信息!");
        } else {
            try {
                Set<ConstraintViolation<T>> set = VALIDATOR.validate(obj, Default.class);
                if (!CollectionUtils.isEmpty(set)) {
                    result.setAvailable(false);
                    StringBuilder message = new StringBuilder();
                    message.append("参数校验不通过.");
                    message.append(obj.getClass().getSimpleName()).append(", 异常参数如下 \n");
                    for (ConstraintViolation<T> cv : set) {
                        message.append(cv.getMessage());
                        message.append(".\n");
                    }
                    result.setMessage(message.toString());
                }
            } catch (Exception e) {
                result.setAvailable(false);
                result.setMessage(e.getMessage());
            }
        }
        return result;
    }

}
