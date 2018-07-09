package com.hce.paymentgateway.controller;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hce.paymentgateway.service.SecretService;

@RestController
@RequestMapping(value = "/test")
public class TestController {
	@Autowired
	private SecretService secretService;

	@RequestMapping(value = "/commandline")
	@ResponseBody
	public String testCommandLine() throws IOException, InterruptedException {
		return secretService.pgp("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/home/wsh/decryption2.txt");
	}

	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() throws NoSuchProviderException, IOException {
		return "DECRYPTION: "+secretService.test("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1");
	}
}