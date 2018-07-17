package com.hce.paymentgateway.entity;

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
@Table(name = "dbs_mt94x_header")
@EntityListeners(AuditingEntityListener.class)
public class DBSMT94XHeaderEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="file_in")
	private String fileIn;//所在文件名
	@Column(name="customer_id")
	private String customerId;//CUSTOMER ID, 海云汇香港、海云汇国际
	@Column(name="corp")
	private String corp;//corp, 海云汇香港、海云汇国际
	@CreatedDate
	@Column(name="creation_time")
	private Date creationTime;//创建创建时间
	@Column(name="application_id")
	private String applicationId;
	@Column(name="service_id")
	private String serviceId;
	@Column(name="sender")
	private String sender;
	@Column(name="session_number")
	private String sessionNumber;
	@Column(name="sequence_number")
	private String sequenceNumber;
	@Column(name="message_type")
	private String messageType;
	@Column(name="receiver")
	private String receiver;
	@Column(name="message_priority")
	private String messagePriority;
	@Column(name="subsidiary_swift_bic")
	private String subsidiarySwiftBic;//SWIFT代码
	@Column(name="account_number")
	private String accountNumber;//帐号
	@Column(name="statement_number")
	private String statementNumber;//报表编号
	@Column(name="dbs_sequence_number")
	private String dbsSequenceNumber;//序列号
}