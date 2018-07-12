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
@Table(name = "dbs_mt94x_detail_information")
@EntityListeners(AuditingEntityListener.class)
public class DBSMT94XInformationEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="detail_id")
	private Long detailId;
	@Column(name="key")
	private String key;
	@Column(name="value")
	private String value;
}