package com.hce.paymentgateway.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dbs_va_report")
@EntityListeners(AuditingEntityListener.class)
public class DBSVAReportEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="response_file")
	private String responseFile;//响应结果所在文件名
	@Column(name="parent_id")
	private String parentId;//PARENT ID, 海云汇香港、海云汇国际
	@Column(name="type")
	private int type;//1VA Report (End-Of-Day); 2VA Report (30-min interval)
	@CreatedDate
	@Column(name="creation_time")
	private Date creationTime;//创建创建时间
	@Column(name="sno")
	private String sno;//序号
	@Column(name="va_number")
	private String vaNumber;//VA编号
	@Column(name="bank_account_number")
	private String bankAccountNumber;//银行帐号
	@Column(name="beneficiary_name")
	private String beneficiaryName;//收款人姓名
	@Column(name="va_name")
	private String vaName;//VA名称
	@Column(name="remitter_name")
	private String remitterName;//汇款人姓名
	@Column(name="remitter_details")
	private String remitterDetails;//汇款人详情
	@Column(name="remitter_bank_code")
	private String remitterBankCode;//汇款银行代码
	@Column(name="remit_currency")
	private String remitCurrency;//汇款货币
	@Column(name="remit_amount")
	private BigDecimal remitAmount;//汇款金额
	@Column(name="credit_currency")
	private String creditCurrency;//入帐货币
	@Column(name="credit_amount")
	private BigDecimal creditAmount;//入帐金额
	@Column(name="sender_s_reference")
	private String sendersReference;//发件人的参考
	@Column(name="transaction_detail")
	private String transactionDetail;//交易明细
	@Column(name="bank_reference")
	private String bankReference;//银行参考
	@Column(name="transaction_date")
	private String transactionDate;//交易日期
	@Column(name="value_date")
	private String valueDate;//价值日期
	@Column(name="channel_id")
	private String channelId;//频道ID
}