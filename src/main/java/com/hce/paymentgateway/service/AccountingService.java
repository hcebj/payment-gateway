package com.hce.paymentgateway.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface AccountingService {
	public void process(List<File> files) throws IOException;
}
