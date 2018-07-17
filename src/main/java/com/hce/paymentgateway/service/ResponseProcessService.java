package com.hce.paymentgateway.service;

import java.io.File;
import java.util.List;

import com.hce.paymentgateway.entity.vo.Header;

public interface ResponseProcessService {
	public void process(List<File> files);
	public String getMsgTag();
	public Header getHeader(String today);
}