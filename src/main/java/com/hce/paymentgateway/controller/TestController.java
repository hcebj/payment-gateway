package com.hce.paymentgateway.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hce.paymentgateway.service.ResponseProcessService;

@RestController
@RequestMapping(value = "/test")
public class TestController {
//	@Autowired
//	private SecretService secretService;
//	@Autowired
//	private ResponseProcessService accountingService;
	@Resource(name = "vaSetupResponseProcessServiceImpl")
    private ResponseProcessService vasetupResponseProcessService;
    @Resource(name = "vaReportResponseProcessServiceImpl")
    private ResponseProcessService vareportResponseProcessService;
	@Resource(name = "mt94xResponseProcessServiceImpl")
    private ResponseProcessService mt94xResponseProcessService;

	@RequestMapping(value = "/commandline")
	@ResponseBody
	public String testCommandLine() throws IOException, InterruptedException {
//		return secretService.pgp("/tmp/UFF1.STP.HKHCEH.HKHCEH.201807060012.txt.DHBKHKHH.D20180706T153341.ACK1.pgp", "/home/wsh/decryption2.txt");
		return null;
	}

	@RequestMapping(value = "/mt94x")
	@ResponseBody
	public String testMT94X() throws NoSuchProviderException, IOException, ParseException {
		List<File> files = new ArrayList<File>(2);
		files.add(new File("/home/wsh/tempFile/vasetup/mt940.txt"));
		files.add(new File("/home/wsh/tempFile/vasetup/mt942.txt"));
		mt94xResponseProcessService.process(files);
		return "SUCCESS";
	}

	@RequestMapping(value = "/vareport")
	@ResponseBody
	public String testVAReport() throws NoSuchProviderException, IOException, ParseException {
		List<File> files = new ArrayList<File>(2);
		files.add(new File("/home/wsh/tempFile/vasetup/HKHCEHXXXXXX.VARPT.HK.0248.HKD.TRAN.ENH.D180709T203218.csv"));
		vareportResponseProcessService.process(files);
		return "SUCCESS";
	}

	@RequestMapping(value = "/vasetup")
	@ResponseBody
	public String testVASetup() throws NoSuchProviderException, IOException, ParseException {
		List<File> files = new ArrayList<File>(2);
		files.add(new File("/home/wsh/tempFile/vasetup/HKHCEHXXXXXX_DSG_VAHKL_RESP_20180711123456.xls"));
		vasetupResponseProcessService.process(files);
		return "SUCCESS";
	}

	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() throws NoSuchProviderException, IOException, ParseException {
		List<File> files = new ArrayList<File>(2);
		files.add(new File("D:/dbs/HKHCEHXXXXXX.CBHL_MT940.D180707050003.txt"));
		mt94xResponseProcessService.process(files);
		return "SUCCESS";
	}
}