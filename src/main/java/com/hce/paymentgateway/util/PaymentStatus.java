package com.hce.paymentgateway.util;

/**
 * @Author Heling.Yao
 * @Date 10:01 2018/5/25
 */
public enum PaymentStatus {

    PROCESSING(0, "处理中"),
    SUCCESS(1, "成功"),
    FAILED(-1, "失败");

    private int status;
    private String description;

    PaymentStatus(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

}
