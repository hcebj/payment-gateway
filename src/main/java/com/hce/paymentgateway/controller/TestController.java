package com.hce.paymentgateway.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {
//	@Autowired
//	private SecretService secretService;
//	@Autowired
//	private ResponseProcessService accountingService;

	@RequestMapping(value = "/commandline")
	@ResponseBody
	public String testCommandLine() throws IOException, InterruptedException {
//		return secretService.pgp("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/home/wsh/decryption2.txt");
		return null;
	}

	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() throws NoSuchProviderException, IOException, ParseException {
		List<File> files = new ArrayList<File>(2);
		files.add(new File("D:/docs/vareport/HKHCEHXXXXXX.HK_0248_HKD_EPAYCOL.ENH.001.D180709T203218.csv"));
		files.add(new File("D:/docs/vareport/HKHCEHXXXXXX.VARPT.HK.0248.HKD.TRAN.ENH.D180709T203218.csv"));
		files.add(new File("D:/docs/vareport/HKHCEHXXXXXX_DSG_VAHKL_RESP_09072018203232.xls"));
//		accountingService.process(files);
		return "xxx";
//		return "DECRYPTION: "+secretService.test("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1");
//		return "DECRYPTION: "+secretService.test("D:/dbs/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "D:/dbs/decryption.txt");
	}
}