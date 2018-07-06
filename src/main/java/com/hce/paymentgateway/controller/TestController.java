package com.hce.paymentgateway.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {
	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() {
		return "wwwwwwwwww";
	}

	@RequestMapping(value = "/commandline")
	@ResponseBody
	public String testCommandLine() throws IOException, InterruptedException {
		String path = "/home/wsh/decryption2.txt";
		String cmd = "sudo gpg -o "+path+" -d /tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp";
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();//阻塞，等待脚本执行完
        InputStream in = null;
        InputStream fileIn = null;
        try {
        	in = process.getInputStream();
        	fileIn = new FileInputStream(path);
        	byte[] buf = new byte[in.available()];
        	in.read(buf);
        	String result = new String(buf);
        	System.out.println(in.available()+"---------------"+result);
        	buf = new byte[fileIn.available()];
        	fileIn.read(buf);
        	return result+"\r\n"+new String(buf);
        } finally {
        	if(in!=null)
        		in.close();
        	if(fileIn!=null)
        		fileIn.close();
        }
	}
}