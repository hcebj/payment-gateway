package com.hce.paymentgateway.service;

import java.io.IOException;
import java.security.NoSuchProviderException;

public interface SecretService {
	public String pgp(String encryption, String decryption) throws IOException, InterruptedException;
	public String getDBSPubKey();
	public String test(String filePathEncod, String filePathDecode) throws NoSuchProviderException, IOException;
}
