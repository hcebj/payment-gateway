package com.hce.paymentgateway.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    
    private Date createTime = new Date();
    private Date modifyTime;
    
    private String paymentId;
    private String transCode; //渠道代码
    private Date transTime; //渠道时间
    private String transId; //渠道流水号
    private Integer queryCount = 0;
    private String paymentOrgId; //支付机构代码
    private Integer status;
    private String transFileName;
    private BigDecimal amount;
   

}
