package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.service.ResponseProcessService;

@Service
public abstract class BaseResponseProcessServiceImpl implements ResponseProcessService {
	@Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;

	protected abstract void process(File file) throws IOException, ParseException;

	public void process(List<File> files) throws IOException, ParseException {
		File historyDir = new File(localTempDir+"/history");
		if(!historyDir.exists()) {
			historyDir.mkdirs();
		}
		for(File file:files) {
			process(file);
			file.renameTo(new File(localTempDir+"/history/"+file.getName()));
		}
	}
}