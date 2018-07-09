package com.hce.paymentgateway.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dbs_va_report")
public class DBSVAReportEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="creation_time")
	private Date creationTime;
	@Column(name="transaction_date")
	private Date transactionDate;
	@Column(name="value_date")
	private Date valueDate;
	@Column(name="sno")
	private String sno;
	@Column(name="va_number")
	private String vaNumber;
	@Column(name="bank_account_number")
	private String bankAccountNumber;
	@Column(name="beneficiary_name")
	private String beneficiaryName;
	@Column(name="va_name")
	private String vaName;
	@Column(name="remitter_name")
	private String remitterName;
	@Column(name="remitter_details")
	private String remitterDetails;
	@Column(name="remitter_bank_code")
	private String remitterBankCode;
	@Column(name="remit_currency")
	private String remitCurrency;
	@Column(name="remit_amount")
	private String remitAmount;
	@Column(name="credit_currency")
	private String creditCurrency;
	@Column(name="credit_amount")
	private String creditAmount;
	@Column(name="sender_s_reference")
	private String sendersReference;
	@Column(name="transaction_detail")
	private String transactionDetail;
	@Column(name="bank_reference")
	private String bankReference;
	@Column(name="channel_id")
	private String channelId;
}