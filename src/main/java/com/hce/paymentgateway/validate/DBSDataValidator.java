package com.hce.paymentgateway.validate;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author Heling.Yao
 * @Date 09:18 2018/5/31
 */
public class DBSDataValidator implements ConstraintValidator<DBSData, String> {

    private boolean canBeNull;
    private int maxLength;
    private String[] enumValue;
    private DataType dataType;

    @Override
    public void initialize(DBSData data) {
        this.canBeNull = data.canBeNull();
        this.maxLength = data.maxLength();
        this.enumValue = data.enumValue();
        this.dataType = data.dateType();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(value)) {
            if(canBeNull)
                return true;
            else
                return false;
        }
        if(value.length() > maxLength) {
            return false;
        }
        if(enumValue != null && enumValue.length > 0) {
            boolean validValue = false;
            for (String ev : enumValue) {
                if(ev.equals(value)) {
                    validValue = true;
                    break;
                }
            }
            if(!validValue) {
                return false;
            }
        }
        return value.matches(dataType.getRegex());
    }

}
