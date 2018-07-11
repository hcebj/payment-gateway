package com.hce.paymentgateway.api.dbs;

import java.math.BigDecimal;

import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.util.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trailer implements Instr {

    @Order(order = 1)
    private String recordType = Constant.TRAILER;
    @Order(order = 2)
    private String totalTransactionNo = "1";
    @Order(order = 3)
    private String totalTransactionAmount;

}
