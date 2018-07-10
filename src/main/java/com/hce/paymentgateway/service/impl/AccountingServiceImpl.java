package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;
import com.hce.paymentgateway.dao.DBSVAReportDao;
import com.hce.paymentgateway.entity.DBSVAReportEntity;
import com.hce.paymentgateway.service.AccountingService;

@Service
public class AccountingServiceImpl implements AccountingService {
	@Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;
	@Autowired
	private DBSVAReportDao dbsVAReportDao;

	public void process(List<File> files) throws IOException, ParseException {
		File historyDir = new File(localTempDir+"/history");
		if(!historyDir.exists()) {
			historyDir.mkdirs();
		}
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		for(File file:files) {
			if(file.getName().endsWith(".csv")) {//VA Report
				CsvReader csvReader = new CsvReader(file.getAbsolutePath());
				int type = -1;
				int linesSkipped = -1;
				if(file.getName().indexOf(".VARPT.HK.")>0&&file.getName().indexOf(".TRAN.ENH.D")>0) {
					//VA Report (End-Of-Day)
					type = 1;
					linesSkipped = 1;
				} else if(file.getName().indexOf(".HK_")>0&&file.getName().indexOf("_HKD_EPAYCOL.ENH.001.D")>0) {
					//VA Report (30-min interval)
					type = 2;
					linesSkipped = 4;
				}
				for(int i=0;i<linesSkipped;i++) {//跳过表头
					csvReader.skipLine();
				}
				while (csvReader.readRecord()){
	                DBSVAReportEntity dbsVAReport = new DBSVAReportEntity();
	                String line = csvReader.get(0);
	                String[] fields = line.split(",");
	                dbsVAReport.setType(type);
	                dbsVAReport.setSno(fields[0].trim());
	                dbsVAReport.setVaNumber(fields[1].trim());
	                dbsVAReport.setBankAccountNumber(fields[2].trim());
	                dbsVAReport.setBeneficiaryName(fields[3].trim());
	                dbsVAReport.setVaName(fields[4].trim());
	                dbsVAReport.setRemitterName(fields[5].trim());
	                dbsVAReport.setRemitterDetails(fields[6].trim());
	                dbsVAReport.setRemitterBankCode(fields[7].trim());
	                dbsVAReport.setRemitCurrency(fields[8].trim());
	                String remitAmount = fields[9].trim();
	                String creditAmount = fields[11].trim();
	                if(remitAmount.length()>0)
	                	dbsVAReport.setRemitAmount(new BigDecimal(remitAmount));
	                dbsVAReport.setCreditCurrency(fields[10].trim());
	                if(creditAmount.length()>0)
	                	dbsVAReport.setCreditAmount(new BigDecimal(creditAmount));
	                dbsVAReport.setSendersReference(fields[12].trim());
	                dbsVAReport.setTransactionDetail(fields[13].trim());
	                dbsVAReport.setBankReference(fields[14].trim());
	                String transactionDate = fields[15].trim();
	                String valueDate = fields[16].trim();
	                if(transactionDate.length()>0)
	                	dbsVAReport.setTransactionDate(df.parse(transactionDate));
	                if(valueDate.length()>0)
	                	dbsVAReport.setValueDate(df.parse(valueDate));
	                dbsVAReport.setChannelId(fields[17].trim());
					dbsVAReportDao.save(dbsVAReport);
	            }
			} else if(file.getName().endsWith(".xls")||file.getName().endsWith(".xlsx")) {//VA Setup Instruction
				
			}
			file.renameTo(new File(localTempDir+"/history/"+file.getName()));
		}
	}
}