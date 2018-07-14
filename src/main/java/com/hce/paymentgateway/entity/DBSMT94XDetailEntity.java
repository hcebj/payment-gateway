package com.hce.paymentgateway.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dbs_mt94x_detail")
@EntityListeners(AuditingEntityListener.class)
public class DBSMT94XDetailEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="header_id")
	private Long headerId;
	@Column(name="value_date")
	private String valueDate;//价值日期YYMMDD
	@Column(name="entry_date")
	private String entryDate;//MMDD
	@Column(name="debit_credit_indicator")
	private String debitCreditIndicator;//借记/信贷指标, C：信用记录D：借记记录
	@Column(name="fund_code")
	private String fundCode;//基金代码
	@Column(name="amount")
	private BigDecimal amount;//量
	@Column(name="transaction_type_identification_code")
	private String transactionTypeIdentificationCode;//交易类型识别码
	@Column(name="reference_to_the_account_owner")
	private String referenceToTheAccountOwner;//引用帐户所有者
	@Column(name="account_servicing_institutions_reference")
	private String accountServicingInstitutionsReference;//账户服务机构的参考
	@Column(name="trade_time")
	private String tradeTime;//交易过账到账户时的时间戳 - HHMMSS
	@Column(name="va_number")
	private String vaNumber;//虚拟账户号，如果有的话
	@Column(name="beneficiary_name")
	private String beneficiaryBame;//收款人姓名
	@Column(name="beneficiary_bank_name")
	private String beneficiaryBankName;//收款银行名称
	@Column(name="beneficiary_account_number")
	private String beneficiaryAccountNumber;//收款人账号
	@Column(name="remittance_currency")
	private String remittanceCurrency;//币种
	@Column(name="remittance_amount")
	private BigDecimal remittanceAmount;//金额
	@Column(name="payer_name")
	private String payerName;//付款人姓名
	@Column(name="payer_bank_name")
	private String payerBankName;//付款人银行名称
	@Column(name="transaction_description")
	private String transactionDescription;//交易描述
	@Column(name="payment_details")
	private String paymentDetails;//付款信息（140个字符）
}