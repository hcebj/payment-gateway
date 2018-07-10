package com.hce.paymentgateway.api.hce;

import com.hce.paymentgateway.validate.ConditionalMandatory;
import com.hce.paymentgateway.validate.DBSData;
import com.hce.paymentgateway.validate.DataType;
import com.hce.paymentgateway.validate.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heling.Yao
 * @Date 17:39 2018/5/24
 */
@Getter
@Setter
public class AccountTransferRequest extends TradeRequest {

    @DBSData(maxLength = 35, dateType = DataType.AN, message = "originatingAccountNumber 格式不符合规则.")
    private String originatingAccountNumber;

    @DBSData(maxLength = 3, dateType = DataType.A, message = "originatingAccountCurrency 格式不符合规则.")
    private String originatingAccountCurrency;

    @DBSData(maxLength = 16, dateType = DataType.S, canBeNull = true, message = "customerOrBatchReference 格式不符合规则.")
    private String customerOrBatchReference;

    @DBSData(maxLength = 3, dateType = DataType.A, message = "paymentCurrency 格式不符合规则.")
    private String paymentCurrency;

    @Date(formatter = "ddMMyyyy", message = "paymentDate 格式不符合规则.")
    private String paymentDate;

    @DBSData(maxLength = 35, dateType = DataType.AN, canBeNull = true, message = "debitAccountForBankCharges 格式不符合规则.")
    private String debitAccountForBankCharges;

    @DBSData(maxLength = 5, dateType = DataType.N, canBeNull = true, message = "batchID 格式不符合规则.")
    private String batchID;
    
    @DBSData(maxLength = 35, dateType = DataType.ANY, message = "receivingPartyName 格式不符合规则.")
    private String receivingPartyName;

    @DBSData(maxLength = 34, dateType = DataType.AN, message = "receivingAccountNumberIBAN 格式不符合规则.")
    private String receivingAccountNumberIBAN;

    @DBSData(maxLength = 1, dateType = DataType.N, canBeNull = true, message = "amountCurrency 格式不符合规则.")
    private String amountCurrency;

    @DBSData(maxLength = 12, dateType = DataType.MONEY, message = "amount 格式不符合规则.")
    private String amount;

    @DBSData(maxLength = 50, dateType = DataType.AND, canBeNull = true, message = "fxContractReference1 格式不符合规则.")
    private String fxContractReference1;

    @DBSData(maxLength = 12, dateType = DataType.MONEY, canBeNull = true, message = "amounttobeUtilized1 格式不符合规则.")
    @ConditionalMandatory(associatedField = "fxContractReference1")
    private String amounttobeUtilized1;

    @DBSData(maxLength = 50, dateType = DataType.AND, canBeNull = true, message = "fxContractReference2 格式不符合规则.")
    private String fxContractReference2;

    @DBSData(maxLength = 12, dateType = DataType.MONEY, canBeNull = true, message = "amounttobeUtilized2 格式不符合规则.")
    @ConditionalMandatory(associatedField = "fxContractReference2")
    private String amounttobeUtilized2;

    @DBSData(maxLength = 140, dateType = DataType.S, canBeNull = true, message = "paymentDetails 格式不符合规则.")
    private String paymentDetails;

    @DBSData(maxLength = 1, dateType = DataType.A, canBeNull = true, enumValue = {"E", "F"}, message = "deliveryMode 格式不符合规则.")
    private String deliveryMode;

    @DBSData(maxLength = 75, dateType = DataType.EMAIL, canBeNull = true, message = "email1 格式不符合规则.")
    @ConditionalMandatory(associatedField = "deliveryMode", associatedConditionValue = "E")
    private String email1;

    @DBSData(maxLength = 75, dateType = DataType.EMAIL, canBeNull = true, message = "email2 格式不符合规则.")
    private String email2;

    @DBSData(maxLength = 75, dateType = DataType.EMAIL, canBeNull = true, message = "email3 格式不符合规则.")
    private String email3;

    @DBSData(maxLength = 75, dateType = DataType.EMAIL, canBeNull = true, message = "email4 格式不符合规则.")
    private String email4;

    @DBSData(maxLength = 75, dateType = DataType.EMAIL, canBeNull = true, message = "email5 格式不符合规则.")
    private String email5;

    @DBSData(maxLength = 35, dateType = DataType.N, canBeNull = true, message = "phoneNumber1 格式不符合规则.")
    @ConditionalMandatory(associatedField = "deliveryMode", associatedConditionValue = "F")
    private String phoneNumber1;

    @DBSData(maxLength = 35, dateType = DataType.N, canBeNull = true, message = "phoneNumber2 格式不符合规则.")
    private String phoneNumber2;

    @DBSData(maxLength = 35, dateType = DataType.N, canBeNull = true, message = "phoneNumber3 格式不符合规则.")
    private String phoneNumber3;

    @DBSData(maxLength = 35, dateType = DataType.N, canBeNull = true, message = "phoneNumber4 格式不符合规则.")
    private String phoneNumber4;

    @DBSData(maxLength = 35, dateType = DataType.N, canBeNull = true, message = "phoneNumber5 格式不符合规则.")
    private String phoneNumber5;

    @DBSData(maxLength = 70000, dateType = DataType.ANY, canBeNull = true, message = "invoiceDetails 格式不符合规则.")
    private String invoiceDetails;

    @DBSData(maxLength = 40, dateType = DataType.ANY, canBeNull = true, message = "clientReference1 格式不符合规则.")
    private String clientReference1;

    @DBSData(maxLength = 40, dateType = DataType.ANY, canBeNull = true, message = "clientReference2 格式不符合规则.")
    private String clientReference2;

    @DBSData(maxLength = 40, dateType = DataType.ANY, canBeNull = true, message = "clientReference3 格式不符合规则.")
    private String clientReference3;

    @DBSData(maxLength = 40, dateType = DataType.ANY, canBeNull = true, message = "clientReference4 格式不符合规则.")
    private String clientReference4;

}
