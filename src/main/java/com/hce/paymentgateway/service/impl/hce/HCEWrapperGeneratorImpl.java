package com.hce.paymentgateway.service.impl.hce;

import java.util.HashMap;
import java.util.Map;

import com.hce.paymentgateway.service.WrapperGenerator;
import com.hce.paymentgateway.vo.HCEHeader;
import com.hce.paymentgateway.vo.HCEMessageWrapper;

public class HCEWrapperGeneratorImpl implements WrapperGenerator {
	public Object getWapper(Object data, String... args) {
		HCEHeader header = new HCEHeader();
		header.setBIZBRCH("0101");
		header.setCHNL("00");
		header.setFRTSIDESN(String.valueOf(System.currentTimeMillis()));
		header.setTLCD("DBS001");
		String today = args[0];
		String corp = args[1];
		String tag = args[2];
		header.setFRTSIDEDT(today);
		header.setLGRPCD(corp);
		header.setTRDCD(tag);
		Map<String, Object> body = new HashMap<String, Object>(1);
		body.put("f"+tag+"1", data);
		HCEMessageWrapper msg = new HCEMessageWrapper(header, body);
		return msg;
	}
}