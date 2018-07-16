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
@Table(name = "dbs_mt940")
@EntityListeners(AuditingEntityListener.class)
public class DBSMT940Entity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="header_id")
	private Long headerId;
	@Column(name="first_opening_balance_indicator")
	private String firstOpeningBalanceIndicator;//首次期初余额/中间期初余额, 借记/贷记余额指示, C：表示贷方余额; D：表示借方余额
	@Column(name="first_opening_balance_date")
	private String firstOpeningBalanceDate;//首次期初余额/中间期初余额, 日期, YYMMDD
	@Column(name="first_opening_balance_currency")
	private String firstOpeningBalanceCurrency;//首次期初余额/中间期初余额, 货币, 帐户的有效ISO货币代码
	@Column(name="first_opening_balance_amount")
	private BigDecimal firstOpeningBalanceAmount;//首次期初余额/中间期初余额, 量
	@Column(name="closing_available_balance_indicator")
	private String closingAvailableBalanceIndicator;//期末可用余额, 借记/贷记余额指示, C：表示贷方余额; D：表示借方余额
	@Column(name="closing_available_balance_date")
	private String closingAvailableBalanceDate;//期末可用余额, 日期, YYMMDD
	@Column(name="closing_available_balance_currency")
	private String closingAvailableBalanceCurrency;//期末可用余额, 货币, 帐户的有效ISO货币代码
	@Column(name="closing_available_balance_amount")
	private BigDecimal closingAvailableBalanceAmount;//期末可用余额, 量
}