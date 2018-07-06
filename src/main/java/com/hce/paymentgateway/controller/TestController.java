package com.hce.paymentgateway.controller;

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
		String cmd = "ps -ef|grep java|grep -v grep";
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();//阻塞，等待脚本执行完
        InputStream in = null;
        try {
        	in = process.getInputStream();
        	byte[] buf = new byte[in.available()];
        	in.read(buf);
        	String result = new String(buf);
        	System.out.println("---------------"+result);
        	return result;
        } finally {
        	if(in!=null)
        		in.close();
        }
	}
}