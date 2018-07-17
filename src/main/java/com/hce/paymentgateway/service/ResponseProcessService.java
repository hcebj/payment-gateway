package com.hce.paymentgateway.service;

import java.io.File;
import java.util.List;

import com.hce.paymentgateway.vo.HCEHeader;

public interface ResponseProcessService {
	public void process(List<File> files);
	public String getMQName();
	public String getMsgTag();
	public HCEHeader getHeader(String today);
}