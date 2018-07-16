package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hce.paymentgateway.controller.PayMqproducer;
import com.hce.paymentgateway.entity.vo.Header;
import com.hce.paymentgateway.entity.vo.MessageWrapper;
import com.hce.paymentgateway.service.ResponseProcessService;
import com.hce.paymentgateway.util.SCPFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class BaseResponseProcessServiceImpl implements ResponseProcessService {
	@Autowired
	private PayMqproducer payMqproducer;
	@Resource(name = "SCPFileUtils")
    private SCPFileUtils SCPFileUtils;

	protected abstract Object process(File file) throws IOException, ParseException;
	protected abstract String getMsgTag();
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
				String tag = getMsgTag();
				Header header = new Header();
				header.setBIZBRCH("0101");
				header.setCHNL("00");
				header.setFRTSIDEDT(today);
				header.setFRTSIDESN(String.valueOf(System.currentTimeMillis()));
				header.setLGRPCD(getCorp());
				header.setTLCD("DBS001");
				header.setTRDCD(tag);
				Map<String, Object> body = new HashMap<String, Object>(1);
				body.put("f"+tag+"1", obj);
				MessageWrapper msg = new MessageWrapper(header, body);
				String json = JSONObject.toJSONString(msg);
				log.info("\r\nJSON_TO_MQ: "+json);
				payMqproducer.sendMsg(tag, json);
			} catch (Exception e) {
				log.error("\r\nDBS_RESPONSE_PROCESS_ERROR: "+file.getName(), e);
			}
		}
	}
}