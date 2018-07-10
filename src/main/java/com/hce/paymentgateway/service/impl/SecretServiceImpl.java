package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.service.SecretService;
import com.hce.paymentgateway.util.KeyBasedLargeFileProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class SecretServiceImpl implements SecretService {
	private final static int loops = 10;
	private final static int interval = 1000;
	@Value("${secret.pgp.pwd}")
	private String secretPwd;
	@Value("${secret.pubkey.dbs}")
	private String dbsPubKey;
	@Value("${cmd.decrypt}")
	private String decryptionCmd;

	public String pgp(String encryption, String decryption) throws IOException, InterruptedException {
		String cmd = decryptionCmd+" "+decryption+" "+encryption;
//		String cmd = "sudo gpg --passphrase "+secretPwd+" -o "+decryption+" -d "+encryption;
//		String cmd = "echo \""+secretPwd+"\"|sudo gpg --batch --passphrase-fd 0 -o "+decryption+" -d "+encryption;
		log.info("COMMAND_LINE: "+secretPwd+"----"+cmd);
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

	public String test(String filePathEncod, String filePathDecode) throws NoSuchProviderException, IOException {
		File f = new File(filePathDecode);
		if(f.exists()) {
			f.delete();
		}
		f.createNewFile();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyBasedLargeFileProcessor.decryptFile(filePathEncod, System.getProperty("user.home") + "/pgp/12_private.asc", secretPwd.toCharArray(), filePathDecode);
		InputStream in = null;
		try {
			in = new FileInputStream(filePathDecode);
			byte[] buf = new byte[in.available()];
			in.read(buf);
			return new String(buf);
		} finally {
			if(in!=null)
				in.close();
		}
	}
}