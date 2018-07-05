package com.hce.paymentgateway.api.dbs;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 16:56 2018/6/12
 */
@Getter
@Setter
public class ACK1Response {

    private ACK1Header ack1Header;
    private Trailer trailer;

}
