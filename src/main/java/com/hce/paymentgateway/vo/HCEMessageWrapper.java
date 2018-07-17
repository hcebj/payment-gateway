package com.hce.paymentgateway.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HCEMessageWrapper {
	private HCEHeader head;
	private Object body;
}