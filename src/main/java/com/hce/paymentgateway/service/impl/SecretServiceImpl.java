package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.service.SecretService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SecretServiceImpl implements SecretService {
	private final static int loops = 10;
	private final static int interval = 1000;
	@Value("secret.pgp.pwd")
	private String secretPwd;
	@Value("secret.pubkey.dbs")
	private String dbsPubKey;

	public String pgp(String encryption, String decryption) throws IOException, InterruptedException {
		String cmd = "sudo gpg --passphrase "+secretPwd+" -o "+decryption+" -d "+encryption;
		File file = new File(decryption);
		if(file.exists()) {
			file.delete();
		}
		Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();//阻塞，等待脚本执行完
        InputStream fileIn = null;
        try {
        	boolean success = false;
        	for(int i=0;i<loops;i++) {
        		if(!file.exists()) {
        			Thread.sleep(interval);
        			log.info("WAITING_FOR_DECRYPTION----------"+i);
        		} else {
        			success = true;
        			break;
        		}
        	}
        	String content = null;
        	if(success) {
        		fileIn = new FileInputStream(file);
        		byte[] buf = new byte[fileIn.available()];
            	fileIn.read(buf);
            	content = new String(buf);
        	} else {
        		content = Constant.RESULT_FAILURE;
        	}
        	return content;
        } finally {
        	if(fileIn!=null)
        		fileIn.close();
        }
	}

	public String getDBSPubKey() {
		return this.dbsPubKey;
	}
}