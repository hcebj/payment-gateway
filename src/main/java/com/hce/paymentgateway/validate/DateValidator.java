package com.hce.paymentgateway.validate;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date, String> {

    private String datePattern;

    @Override
    public void initialize(Date data) {
        this.datePattern = data.formatter();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(value)) {
            return true;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(datePattern);
            df.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

}
