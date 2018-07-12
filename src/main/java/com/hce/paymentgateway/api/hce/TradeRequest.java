package com.hce.paymentgateway.api.hce;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.validate.DBSData;
import com.hce.paymentgateway.validate.DataType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by zonga on 2018/5/24.
 */
@Getter
@Setter
public class TradeRequest {

    @NotNull(message = "交易流水不能为空")
    private String transId;

    @NotNull(message = "交易时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transTime;

    @NotNull(message = "应用ID")
    private String transCode;

    @DBSData(maxLength = 3, dateType = DataType.AN, enumValue = {"ACT","CTS","TT"}, message = "productType 格式不符合规则.")
    private String productType;

    private String recordType = Constant.PAYMENT;

    //@NotNull(message = "付款机构ID")
    @DBSData(maxLength = 4, dateType = DataType.AN, enumValue = {"9991","9992"}, message = "paymentOrgId 付款机构ID.")
    private String paymentOrgId;

}
