package com.hce.paymentgateway.validate;

import com.hce.paymentgateway.util.JsonUtil;

/**
 * @Author Heling.Yao
 * @Date 11:09 2018/5/29
 */
public class ValidatorResult {

    private boolean available = true;
    private String message;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

}
