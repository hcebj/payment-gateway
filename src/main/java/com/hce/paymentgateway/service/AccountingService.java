package com.hce.paymentgateway.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public interface AccountingService {
	public void process(List<File> files) throws IOException, ParseException, InvalidFormatException;
}