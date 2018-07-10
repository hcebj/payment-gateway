package com.hce.paymentgateway.entity;

import com.hce.paymentgateway.api.dbs.RequestDetails;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

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

    private String productType;
    private String recordType;
    private String originatingAccountNumber;
    private String originatingAccountCurrency;
    private String customerOrBatchReference;
    private String batchID;
    private String paymentCurrency;
    private String paymentDate;
    private String bankCharges;//cts
    private String debitAccountForBankCharges;//cts
    private String receivingPartyName;
    private String receivingAccountNumberIBAN;
    private String receivingPartyAddress1;//cts
    private String receivingPartyAddress2;//cts
    private String receivingPartyAddress3;//cts
    private String beneficiaryBankSwiftBic;//cts用
    private String beneficiaryBankName;//cts用
    private String beneficiaryBankAddress;//cts
    private String beneficiaryBankCountry;//cts
    private String amountCurrency;
    private String amount;
    private String fxContractReference1;
    private String amounttobeUtilized1;
    private String fxContractReference2;
    private String amounttobeUtilized2;
    private String paymentDetails;
    private String beneficiaryCategory;
    private String payeeRole;
    private String purposeofPayment;
    private String supplementaryInfo;
    private String deliveryMode;
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
    private String invoiceDetails;
    private String clientReference1;
    private String clientReference2;
    private String clientReference3;
    private String clientReference4;
    private String specificPaymentPurpose;
    private String taxFreeGoodsRelated;
    private String paymentNature;
    private String chnlNo;
    private String chnlDate;
    private String chnlSn;
    private String transactionStatus;
    private String additionalInformation;

}
