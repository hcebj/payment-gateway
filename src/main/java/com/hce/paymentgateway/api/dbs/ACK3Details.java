package com.hce.paymentgateway.api.dbs;

import com.hce.paymentgateway.util.Order;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 16:54 2018/6/12
 */
@Getter
@Setter
public class ACK3Details implements Instr {

    @Order(order = 1)
    private String recordType;
    @Order(order = 2)
    private String productType;
    @Order(order = 3)
    private String originatingAccountNumber;
    @Order(order = 4)
    private String originatingAccountCurrency;
    @Order(order = 5)
    private String paymentCurrency;
    @Order(order = 6)
    private String customerReference;
    @Order(order = 7)
    private String paymentDate;
    @Order(order = 8)
    private String receivingPartyName;
    @Order(order = 9)
    private String beneficiaryBankSWIFTBIC;
    @Order(order = 10)
    private String amountCurrency;
    @Order(order = 11)
    private String amount;
    @Order(order = 12)
    private String transactionStatus;
    @Order(order = 13)
    private String additionalInformation;
    @Order(order = 14)
    private String batchId;
    @Order(order = 15)
    private String debitAccountForBankCharge;
    @Order(order = 16)
    private String chargeAmount;
    @Order(order = 17)
    private String bankReference;

}
