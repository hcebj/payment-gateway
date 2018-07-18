package com.hce.paymentgateway.service;

public interface WrapperGenerator {
	public Object getWapper(Object data, String... args);
}