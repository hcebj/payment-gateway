package com.hce.paymentgateway.util;

import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.service.TransactionService;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 15:38 2018/5/25
 */
@Getter
@Setter
public class ServiceWrapper {

    private TransactionService transactionService;
    private Class<? extends TradeRequest> parameterType;

    public ServiceWrapper(TransactionService transactionService,
                          Class<? extends TradeRequest> parameterType) {
        this.transactionService = transactionService;
        this.parameterType = parameterType;
    }

}
