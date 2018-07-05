package com.hce.paymentgateway.api.dbs;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 16:58 2018/6/12
 */
@Getter
@Setter
public class ACK3Response {

    private ACK3Header ack3Header;
    private ACK3Details ack3Details;
    private Trailer trailer;

}
