package com.hce.paymentgateway.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBSMT94XVO {
	private String fileNm;
	private String trdDt;
	private String tlSnCd;
	private String custAcctno;
	private String acctNm;
	private String otherAcctno1;
	private String otherAcctnm1;
	private String brrlndFlg;
	private String trdCurr1;
	private String trdAmt;
	private String transId;
	private String transTime;
}
