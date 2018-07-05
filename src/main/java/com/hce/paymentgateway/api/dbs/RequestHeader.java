package com.hce.paymentgateway.api.dbs;

import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.util.Order;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class RequestHeader implements Instr {

    @Order(order = 1)
    private String recordType = Constant.HEADER;
    @Order(order = 2)
    private String fileCreationDate = (new SimpleDateFormat("ddMMyyyy")).format(new Date());
    @Order(order = 3)
    private String organizationId;
    @Order(order = 4)
    private String senderName;

}

