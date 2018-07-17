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
@Table(name = "dbs_va_setup")
@EntityListeners(AuditingEntityListener.class)
public class DBSVASetupEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="corp")
	private String corp;
	@CreatedDate
	@Column(name="creation_time")
	private Date creationTime;//创建创建时间
	@Column(name="action")
	private String action;//A添加, M修改, D删除
	@Column(name="corp_code")
	private String corpCode;//公司代码
	@Column(name="remitter_payer_name")
	private String remitterPayerName;//VA名称
	@Column(name="master_a_c")
	private String masterAC;//VA编号
	@Column(name="erp_code")
	private String erpCode;//
	@Column(name="static_va_sequence_number")
	private String staticVASequenceNumber;//VA前缀
	@Column(name="request_file")
	private String requestFile;//请求果所在文件名
	@Column(name="response_file")
	private String responseFile;//响应结果所在文件名
	@Column(name="status")
	private String status;//状态
	@Column(name="failure_reason")
	private String failureReason;//失败原因
}