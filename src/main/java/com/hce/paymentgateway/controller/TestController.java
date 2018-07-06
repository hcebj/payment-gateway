package com.hce.paymentgateway.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/test")
public class TestController {
	@RequestMapping(value = "/commandline")
	@ResponseBody
	public String testCommandLine() throws IOException, InterruptedException {
		String path = "/home/wsh/decryption2.txt";
		String cmd = "sudo gpg -o "+path+" -d /tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp";
		File file = new File(path);
		if(file.exists()) {
			file.delete();
		}
		Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();//阻塞，等待脚本执行完
        InputStream in = null;
        InputStream fileIn = null;
        try {
        	in = process.getInputStream();
        	byte[] buf = new byte[in.available()];
        	in.read(buf);
        	String result = new String(buf);
        	log.info(in.available()+"---------------"+result);
        	boolean success = false;
        	for(int i=0;i<10;i++) {
        		if(!file.exists()) {
        			Thread.sleep(1000);
        			log.info("================="+i);
        		} else {
        			success = true;
        			break;
        		}
        	}
        	String content = null;
        	if(success) {
        		fileIn = new FileInputStream(path);
            	buf = new byte[fileIn.available()];
            	fileIn.read(buf);
            	content = new String(buf);
        	} else {
        		content = "FAILURE";
        	}
        	return result+"\r\n"+content;
        } finally {
        	if(in!=null)
        		in.close();
        	if(fileIn!=null)
        		fileIn.close();
        }
	}

	public static void main(String[] args) throws InterruptedException {
		File f = new File("D:/dbs/commandline.txt");
		System.out.println(f.exists());
		f.delete();
		System.out.println(f.exists());
	}
}