package com.hce.paymentgateway.api.dbs;

import com.hce.paymentgateway.util.Order;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseDetails implements Instr {

    @Order(order = 1)
    private String recordType;
    @Order(order = 2)
    private String productType;
    @Order(order = 3)
    private String originatingAccountNumber;
    @Order(order = 4)
    private String customerOrBatchReference;
    @Order(order = 5)
    private String batchId;
    @Order(order = 6)
    private String paymentDate;
    @Order(order = 7)
    private String bankCharges;
    @Order(order = 8)
    private String debitAccountForBankCharges;
    @Order(order = 9)
    private String receivingPartyName;
    @Order(order = 10)
    private String payableTo;
    @Order(order = 11)
    private String receivingAccountNumberIBAN;
    @Order(order = 12)
    private String beneficiaryBankName;
    @Order(order = 13)
    private String beneficiaryBankCountry;
    @Order(order = 14)
    private String amountCurrency;
    @Order(order = 15)
    private String amount;
    @Order(order = 16)
    private String fxContractReference1;
    @Order(order = 17)
    private String fxContractReference2;
    @Order(order = 18)
    private String transactionCode;
    @Order(order = 19)
    private String particularsBeneficaryPayerReference;
    @Order(order = 20)
    private String dDAReferenceOrReference;
    @Order(order = 21)
    private String printAtLocationPickUpLocation;
    @Order(order = 22)
    private String payableLocation;
    @Order(order = 23)
    private String reservedField;
    @Order(order = 24)
    private String reservedField2;
    @Order(order = 25)
    private String idealInternalReference;
    @Order(order = 26)
    private String settlementDate;
    @Order(order = 27)
    private String valueDate;
    @Order(order = 28)
    private String settlementAmount;
    @Order(order = 29)
    private String remittanceAmount;
    @Order(order = 30)
    private String fxExchangeRate1;
    @Order(order = 31)
    private String fxExchangeRate2;
    @Order(order = 32)
    private String chargesCurrency;
    @Order(order = 33)
    private String chargesAmount;
    @Order(order = 34)
    private String receivingBankIdentifier;
    @Order(order = 35)
    private String beneficiaryBankAddress1;
    @Order(order = 36)
    private String beneficiaryBankAddress2;
    @Order(order = 37)
    private String bankReference;
    @Order(order = 38)
    private String processingStatus;
    @Order(order = 39)
    private String reasonCode;
    @Order(order = 40)
    private String backofficeRemarks;
    @Order(order = 41)
    private String chequeNumber;

}
