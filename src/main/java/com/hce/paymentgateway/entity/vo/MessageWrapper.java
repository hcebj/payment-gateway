package com.hce.paymentgateway.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageWrapper {
	private Header head;
	private Object body;
}