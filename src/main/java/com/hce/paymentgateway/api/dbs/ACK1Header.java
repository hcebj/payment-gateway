package com.hce.paymentgateway.api.dbs;

import com.hce.paymentgateway.util.Order;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 14:43 2018/6/12
 */
@Getter
@Setter
public class ACK1Header implements ACKHeader {

    @Order(order = 1)
    private String recordType;
    @Order(order = 2)
    private String dsgMessageId;
    @Order(order = 3)
    private String originalMessageId;
    @Order(order = 4)
    private String creationDateTime;
    @Order(order = 5)
    private String dbsBICCode;
    @Order(order = 6)
    private String originalMessageNameId;
    @Order(order = 7)
    private String senderName;
    @Order(order = 8)
    private String senderCountry;
    @Order(order = 9)
    private String identificationMailbox;
    @Order(order = 10)
    private String groupStatus;
    @Order(order = 11)
    private String additionalInformation;

}
