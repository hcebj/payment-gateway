package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.service.AccountingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountingServiceImpl implements AccountingService {
	@Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;

	public void process(List<File> files) {
		for(File file:files) {
			
			file.renameTo(new File(localTempDir+"/history/"+file.getName()));
		}
	}
}