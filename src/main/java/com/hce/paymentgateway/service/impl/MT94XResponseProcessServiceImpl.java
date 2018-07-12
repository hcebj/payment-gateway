package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.prowidesoftware.swift.model.mt.AbstractMT;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;

@Service("mt94xResponseProcessServiceImpl")
public class MT94XResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Override
	protected void process(File file) throws IOException, ParseException {
		String path = "D:/docs/vareport/Sample_IDEAL Connect_Standardized MT940_HKBRGTS4XXXX.CBHK_MT940.D171025040616.txt";
		AbstractMT.parse(path);
//		MT103.parse("");
	}

	public static void main(String[] args) throws IOException {
		File file = new File("D:/docs/vareport/Sample_IDEAL_Connect_Standardized_MT940_HKBRGTS4XXXX.CBHK_MT940.D171025040616.txt");
		InputStream in = new FileInputStream(file);
		AbstractMT.parse(file);
		in.close();
	}
}