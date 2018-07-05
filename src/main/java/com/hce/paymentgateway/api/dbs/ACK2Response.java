package com.hce.paymentgateway.api.dbs;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 16:57 2018/6/12
 */
@Getter
@Setter
public class ACK2Response {

    private ACK2Header ack2Header;
    private ACK2Details ack2Details;
    private Trailer trailer;

}
