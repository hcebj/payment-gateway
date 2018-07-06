package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SecretService {
	public String pgp(String encryption, String decryption) throws IOException, InterruptedException {
		String cmd = "sudo gpg -o "+decryption+" -d "+encryption;
		File file = new File(decryption);
		if(file.exists()) {
			file.delete();
		}
		Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();//阻塞，等待脚本执行完
        InputStream fileIn = null;
        try {
        	boolean success = false;
        	for(int i=0;i<10;i++) {
        		if(!file.exists()) {
        			Thread.sleep(1000);
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
}