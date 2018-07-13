package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.service.ResponseProcessService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class BaseResponseProcessServiceImpl implements ResponseProcessService {
	@Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;

	protected abstract void process(File file) throws IOException, ParseException;

	public void process(List<File> files) {
		File historyDir = new File(localTempDir+"/history");
		if(!historyDir.exists()) {
			historyDir.mkdirs();
		}
		for(File file:files) {
			try {
				process(file);
				file.renameTo(new File(localTempDir+"/history/"+file.getName()));
			} catch (Exception e) {
				log.error("\r\nDBS_RESPONSE_PROCESS_ERROR: "+file.getName(), e);
			}
		}
	}
}