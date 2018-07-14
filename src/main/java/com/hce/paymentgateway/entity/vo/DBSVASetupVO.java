package com.hce.paymentgateway.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBSVASetupVO {
	private String corp;
	private String masterAC;
	private String status;
	private String failureReason;
}
