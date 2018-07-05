package com.hce.paymentgateway.api.hce;

import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.util.ResponseCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by zonga on 2018/5/24.
 */
@Getter
@Setter
public class TradeResponse extends TradeRequest {
    private String code = ResponseCode.SUCCESS.name();
    private String message;
}
