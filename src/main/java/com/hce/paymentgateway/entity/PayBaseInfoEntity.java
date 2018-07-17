package com.hce.paymentgateway.entity;

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
@Table(name = "pay_base_info")
@EntityListeners(AuditingEntityListener.class)
public class PayBaseInfoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="corp")
	private String corp;
	@Column(name="field_name")
	private String fieldName;
	@Column(name="field_value")
	private String fieldValue;
	@Column(name="field_status")
	private String fieldStatus;
	@Column(name="remark")
	private String remark;
}
