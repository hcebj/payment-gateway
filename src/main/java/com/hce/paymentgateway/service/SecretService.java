package com.hce.paymentgateway.service;

import java.io.IOException;

public interface SecretService {
	public String pgp(String encryption, String decryption) throws IOException, InterruptedException;
	public String getDBSPubKey();
}