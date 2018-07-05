package com.hce.paymentgateway.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @Author Heling.Yao
 * @Date 15:19 2018/5/28
 */
@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transId;
    private Date transTime;
    private String applicationId;
    private String paymentOrgId;
    private Integer status;
    private Integer queryCount = 0;
    private String amount = "0.00";
    private String transFileName;
    private Date modifyTime;

}
