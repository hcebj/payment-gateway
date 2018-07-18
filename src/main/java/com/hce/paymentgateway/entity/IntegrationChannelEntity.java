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
@Table(name = "integration_channel")
@EntityListeners(AuditingEntityListener.class)
public class IntegrationChannelEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="cn_name")
	private String cnName;
	@Column(name="en_name")
	private String enName;
	@Column(name="short_name")
	private String shortName;
	@Column(name="va_setup_impl")
	private String vaSetupImpl;
	@Column(name="va_report_impl")
	private String vaReportImpl;
	@Column(name="mt94x_impl")
	private String mt94xImpl;
}