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
@Table(name = "dbs_mt942")
@EntityListeners(AuditingEntityListener.class)
public class DBSMT942Entity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name="header_id")
	private Long headerId;
	
	@Column(name="floor_limit_indicator_currency")
	private String floorLimitIndicatorCurrency;//限制指示货币
	@Column(name="floor_limit_indicator_amount")
	private BigDecimal floorLimitIndicatorAmount;//量
	@Column(name="date_time_indication_date")
	private String dateTimeIndicationDate;//日期YYMMDDHHMM
	@Column(name="date_time_indication_sign")
	private String dateTimeIndicationSign;//时区标志:: +-号
	@Column(name="date_time_indication_time_zone")
	private String dateTimeIndicationTimeZone;//时区
	@Column(name="number_and_sum_of_debit_entries_number")
	private BigDecimal numberAndSumOfDebitEntriesNumber;//借方分录的数量和总和, 数
	@Column(name="number_and_sum_of_debit_entries_currency")
	private String numberAndSumOfDebitEntriesCurrency;//货币
	@Column(name="number_and_sum_of_debit_entries_amount")
	private BigDecimal numberAndSumOfDebitEntriesAmount;//量
	@Column(name="number_and_sum_of_credit_entries_number")
	private BigDecimal numberAndSumOfCreditEntriesNumber;//贷方分录的数量和总和, 数
	@Column(name="number_and_sum_of_credit_entries_currency")
	private String numberAndSumOfCreditEntriesCurrency;//货币
	@Column(name="number_and_sum_of_credit_entries_amount")
	private BigDecimal numberAndSumOfCreditEntriesAmount;//量
	@Column(name="information_to_the_account_owner_opening_balance_indicator")
	private String informationToTheAccountOwnerOpeningBalanceIndicator;//期初余额,借方为“D”，贷方为“C”
	@Column(name="information_to_the_account_owner_opening_balance_currency")
	private String informationToTheAccountOwnerOpeningBalanceCurrency;//期初余额,期初余额, 货币, 帐户的有效ISO货币代码
	@Column(name="information_to_the_account_owner_opening_balance_amount")
	private BigDecimal informationToTheAccountOwnerOpeningBalanceAmount;//期初余额
	@Column(name="information_to_the_account_owner_closing_balance_indicator")
	private String informationToTheAccountOwnerClosingBalanceIndicator;//期末余额,借方为“D”，贷方为“C”
	@Column(name="information_to_the_account_owner_closing_balance_currency")
	private String informationToTheAccountOwnerClosingBalanceCurrency;//期末余额,货币, 帐户的有效ISO货币代码
	@Column(name="information_to_the_account_owner_closing_balance_amount")
	private BigDecimal informationToTheAccountOwnerClosingBalanceAmount;//期末余额
	@Column(name="information_to_the_account_owner_closing_available_indicator")
	private String informationToTheAccountOwnerClosingAvailableIndicator;//关闭可用余额,借方为“D”，贷方为“C”
	@Column(name="information_to_the_account_owner_closing_available_currency")
	private String informationToTheAccountOwnerClosingAvailableCurrency;//关闭可用余额,货币, 帐户的有效ISO货币代码
	@Column(name="information_to_the_account_owner_closing_available_amount")
	private BigDecimal informationToTheAccountOwnerClosingAvailableAmount;//关闭可用余额
}