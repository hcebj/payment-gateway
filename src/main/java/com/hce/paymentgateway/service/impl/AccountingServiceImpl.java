package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;
import com.hce.paymentgateway.dao.DBSVAReportDao;
import com.hce.paymentgateway.entity.DBSVAReportEntity;
import com.hce.paymentgateway.service.AccountingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountingServiceImpl implements AccountingService {
	@Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;
	@Autowired
	private DBSVAReportDao dbsVAReportDao;

	public void process(List<File> files) throws IOException {
		for(File file:files) {
			if(file.getName().endsWith(".csv")) {
				CsvReader csvReader = new CsvReader(file.getAbsolutePath());
				while (csvReader.readRecord()){
	                DBSVAReportEntity dbsVAReport = new DBSVAReportEntity();
	                dbsVAReport.setSno(csvReader.get(0));
					dbsVAReportDao.save(dbsVAReport);
	            }
			} else {
				
			}
			file.renameTo(new File(localTempDir+"/history/"+file.getName()));
		}
	}
}