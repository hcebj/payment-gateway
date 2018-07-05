package com.hce.paymentgateway.api.dbs;

import com.hce.paymentgateway.util.Order;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 15:02 2018/6/12
 */
@Setter
@Getter
public class ACK2Header implements ACKHeader {

    @Order(order = 1)
    private String recordType;
    @Order(order = 2)
    private String dbsMessageId;
    @Order(order = 3)
    private String originalMessageId;
    @Order(order = 4)
    private String fileCreationDateTime;
    @Order(order = 5)
    private String organizationID;
    @Order(order = 6)
    private String senderName;
    @Order(order = 7)
    private String groupStatus;
    @Order(order = 8)
    private String additionalInformation;

}
