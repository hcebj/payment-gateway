package com.hce.paymentgateway.entity;

import com.hce.paymentgateway.api.dbs.RequestDetails;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 所有数据属于{@link RequestDetails}的子集
 * @Author Heling.Yao
 * @Date 13:55 2018/5/24
 */
@Getter
@Setter
@Entity
@Table(name = "account_transfer")
public class AccountTransferEntity extends BaseEntity implements Serializable {

   
    private String recordType; // 记录类型
    private String productType; //付款类型
    private String batchId; //批次号
    private String customerOrBatchReference; //支付号
    private String originatingAccountNumber; //付款人账号
    private String originatingAccountCurrency; //付款人币种
    private String amount ; //付款金额
    private String paymentCurrency; //付款币种
    private String paymentDate; //付款日期
    private String bankCharges;// 手续费类型
    private String debitAccountForBankCharges;//扣款手续费账号
    private String receivingPartyName; //收款人名称
    private String receivingAccountNumberIBAN; //收款人账号
    private String receivingPartyAddress1;//收款人地址1
    private String receivingPartyAddress2;//cts
    private String receivingPartyAddress3;//cts
    private String beneficiaryBankSwiftBic;//收款银行SWIFT CODE 
    private String beneficiaryBankName;//收款银行名称
    private String beneficiaryBankAddress;//收款银行地址
    private String beneficiaryBankCountry;//收款银行国家
    private String intermediaryBankSwiftBic; //中转行号
    private String amountCurrency;//购汇币种
    private String fxContractReference1;//外汇合约号1
    private String amounttobeUtilized1;//外汇合约1项下使用金额
    private String fxContractReference2;//外汇合约号2
    private String amounttobeUtilized2;//外汇合约2项下使用金额
    private String paymentDetails;//付款明细
    private String beneficiaryCategory;//收款人类别
    /*private String payeeRole;
    private String purposeofPayment;
    private String supplementaryInfo;*/
    private String deliveryMode;//递送方式
    private String email1;
    private String email2;
    private String email3;
    private String email4;
    private String email5;
    private String phoneNumber1;
    private String phoneNumber2;
    private String phoneNumber3;
    private String phoneNumber4;
    private String phoneNumber5;
    private String invoiceDetails;//发票详情
    private String clientReference1;//客户参考号1
    private String clientReference2;
    private String clientReference3;
    private String clientReference4;
    /*private String specificPaymentPurpose;
    private String taxFreeGoodsRelated;
    private String paymentNature;*/
    private String ackFileType; //ACK1 ACK2 ACK3
    private String transactionStatus; //ack res
    private String additionalInformation; //ack res
    private String fileName; //发送文件名
    private String fileName1; // ACK1
    private String fileName2; // ACK2
    private String fileName3; // ACK3

}
