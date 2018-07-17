package com.hce.paymentgateway.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HCEDBSVASetupVO {
	private String corp;
	private String masterAC;
	private String status;
	private String failureReason;
}
