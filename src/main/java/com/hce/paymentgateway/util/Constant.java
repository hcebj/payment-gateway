package com.hce.paymentgateway.util;

/**
 * @Author Heling.Yao
 * @Date 10:24 2018/5/25
 */
public interface Constant {

    // 交易类型ACT
    String ACCOUNT_TRANSFER = "ACT";
    
    // 交易类型CTS
    String CHATS_PAY = "CTS";
    
    // 交易类型TT
    String TELEGRAPHIC_TRANSFER = "TT";

    // recordType
    String HEADER = "HEADER";
    String PAYMENT = "PAYMENT";
    String TRAILER = "TRAILER";

    String LINUX_LINE_BREAK = "\n";

    Integer MAX_QUERY_COUNT = 200;

}
