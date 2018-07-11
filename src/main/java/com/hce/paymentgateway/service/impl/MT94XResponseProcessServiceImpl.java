package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.springframework.stereotype.Service;

@Service("mt94xResponseProcessServiceImpl")
public class MT94XResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Override
	protected void process(File file) throws IOException, ParseException {
//		AbstractMT.parse("")
//		MT103.parse("")
	}
}