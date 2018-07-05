package com.hce.paymentgateway.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Heling.Yao
 * @Date 16:19 2018/6/6
 */
@Getter
@Setter
@Entity
@Table(name = "account_info")
public class AccountInfoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String organizationId;
    private String senderName;
    private boolean enabled = true;
    private String paymentOrgId;

}
