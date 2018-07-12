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
	@Column(name="supplementary_details")
	private String supplementaryDetails;//补充细节
}