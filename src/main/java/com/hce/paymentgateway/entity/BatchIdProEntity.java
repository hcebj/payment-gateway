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
@Table(name = "batch_id_pro")
@EntityListeners(AuditingEntityListener.class)
public class BatchIdProEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="payment_date")
	private String paymentDate;//交易日期
	@Column(name="batch_id_seq")
	private Long batchIdSeq;//批次号序号
	
}