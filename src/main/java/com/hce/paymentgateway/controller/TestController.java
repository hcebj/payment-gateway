package com.hce.paymentgateway.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hce.paymentgateway.service.AccountingService;
import com.hce.paymentgateway.service.SecretService;

@RestController
@RequestMapping(value = "/test")
public class TestController {
	@Autowired
	private SecretService secretService;
	@Autowired
	private AccountingService accountingService;

	@RequestMapping(value = "/commandline")
	@ResponseBody
	public String testCommandLine() throws IOException, InterruptedException {
		return secretService.pgp("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/home/wsh/decryption2.txt");
	}

	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() throws NoSuchProviderException, IOException, ParseException, InvalidFormatException {
		List<File> files = new ArrayList<File>(2);
		files.add(new File("D:/docs/vareport/HKHCEHXXXXXX.HK_0248_HKD_EPAYCOL.ENH.001.D180709T203218.csv"));
		files.add(new File("D:/docs/vareport/HKHCEHXXXXXX.VARPT.HK.0248.HKD.TRAN.ENH.D180709T203218.csv"));
		files.add(new File("D:/docs/vasetup.xls"));
		accountingService.process(files);
		return "xxx";
//		return "DECRYPTION: "+secretService.test("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1");
//		return "DECRYPTION: "+secretService.test("D:/dbs/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "D:/dbs/decryption.txt");
	}
}