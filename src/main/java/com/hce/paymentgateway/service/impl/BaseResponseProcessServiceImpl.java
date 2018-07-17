package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hce.paymentgateway.controller.PayMqproducer;
import com.hce.paymentgateway.service.ResponseProcessService;
import com.hce.paymentgateway.util.SCPFileUtils;
import com.hce.paymentgateway.vo.HCEHeader;
import com.hce.paymentgateway.vo.HCEMessageWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class BaseResponseProcessServiceImpl implements ResponseProcessService {
	@Autowired
	private PayMqproducer payMqproducer;
	@Autowired
    private SCPFileUtils SCPFileUtils;

	protected abstract Object process(File file) throws IOException, ParseException;
	protected abstract String getCorp();

	@Transactional
	public void process(List<File> files) {
		String localTempDir = SCPFileUtils.getTempFileDir();
		File historyDir = new File(localTempDir+"/history");
		if(!historyDir.exists()) {
			historyDir.mkdirs();
		}
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String today = df.format(System.currentTimeMillis());
		for(File file:files) {
			try {
				Object obj = process(file);
//				file.renameTo(new File(localTempDir+"/history/"+file.getName()));
				String corp = getCorp();
				String tag = getMsgTag();
				HCEHeader header = getHeader(today);
				header.setLGRPCD(corp);
				header.setTRDCD(tag);
				Map<String, Object> body = new HashMap<String, Object>(1);
				body.put("f"+tag+"1", obj);
				HCEMessageWrapper msg = new HCEMessageWrapper(header, body);
				String json = JSONObject.toJSONString(msg);
				log.info("\r\nJSON_TO_MQ: "+json);
				payMqproducer.sendMsg(this.getMQName(), tag, json);
			} catch (Exception e) {
				log.error("\r\nDBS_RESPONSE_PROCESS_ERROR: "+file.getName(), e);
			}
		}
	}

	public HCEHeader getHeader(String today) {
		HCEHeader header = new HCEHeader();
		header.setBIZBRCH("0101");
		header.setCHNL("00");
		header.setFRTSIDEDT(today);
		header.setFRTSIDESN(String.valueOf(System.currentTimeMillis()));
		header.setTLCD("DBS001");
		return header;
	}
}