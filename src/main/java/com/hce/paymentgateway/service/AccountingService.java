package com.hce.paymentgateway.service;

import java.io.File;
import java.util.List;

public interface AccountingService {
	public void process(List<File> files);
}
