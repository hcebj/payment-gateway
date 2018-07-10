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
				CsvReader csvReader = null;
				try {
					csvReader = new CsvReader(file.getAbsolutePath());
					int type = -1;
					int linesSkipped = -1;
					if(file.getName().indexOf(".VARPT.HK.")>0&&file.getName().indexOf(".TRAN.ENH.D")>0) {
						//VA Report (End-Of-Day)
						type = 1;
						linesSkipped = 2;
					} else if(file.getName().indexOf(".HK_")>0&&file.getName().indexOf("_HKD_EPAYCOL.ENH.001.D")>0) {
						//VA Report (30-min interval)
						type = 2;
						linesSkipped = 8;
					}
					for(int i=0;i<linesSkipped;i++) {//跳过表头
						csvReader.readRecord();
					}
					while(csvReader.readRecord()) {
						String vaNumber = normalize(csvReader.get(1));
						if(vaNumber!=null&&vaNumber.length()>0) {
							DBSVAReportEntity dbsVAReport = new DBSVAReportEntity();
			                dbsVAReport.setType(type);
//			                dbsVAReport.setSno(normalize(csvReader.get(0)));
			                dbsVAReport.setVaNumber(vaNumber);
			                dbsVAReport.setBankAccountNumber(normalize(csvReader.get(2)));
			                dbsVAReport.setBeneficiaryName(normalize(csvReader.get(3)));
			                dbsVAReport.setVaName(normalize(csvReader.get(4)));
			                dbsVAReport.setRemitterName(normalize(csvReader.get(5)));
			                dbsVAReport.setRemitterDetails(normalize(csvReader.get(6)));
			                dbsVAReport.setRemitterBankCode(normalize(csvReader.get(7)));
			                dbsVAReport.setRemitCurrency(normalize(csvReader.get(8)));
			                String remitAmount = normalize(csvReader.get(9));
			                String creditAmount = normalize(csvReader.get(11));
			                if(remitAmount.length()>0)
			                	dbsVAReport.setRemitAmount(new BigDecimal(remitAmount));
			                dbsVAReport.setCreditCurrency(normalize(csvReader.get(10)));
			                if(creditAmount.length()>0)
			                	dbsVAReport.setCreditAmount(new BigDecimal(creditAmount));
			                dbsVAReport.setSendersReference(normalize(csvReader.get(12)));
			                dbsVAReport.setTransactionDetail(normalize(csvReader.get(13)));
			                dbsVAReport.setBankReference(normalize(csvReader.get(14)));
			                String transactionDate = normalize(csvReader.get(15));
			                String valueDate = normalize(csvReader.get(16));
			                if(transactionDate.length()>0)
			                	dbsVAReport.setTransactionDate(df.parse(transactionDate));
			                if(valueDate.length()>0)
			                	dbsVAReport.setValueDate(df.parse(valueDate));
			                dbsVAReport.setChannelId(normalize(csvReader.get(17)));
							dbsVAReportDao.save(dbsVAReport);
						}
		            }
				} finally {
					if(csvReader!=null)
						csvReader.close();
				}
			} else if(file.getName().endsWith(".xls")||file.getName().endsWith(".xlsx")) {//VA Setup Instruction
				
			}
			file.renameTo(new File(localTempDir+"/history/"+file.getName()));
		}
	}

	private String normalize(String s) {
		if(s==null||s.trim().length()<1) {
			return "";
		}
		byte[] buf = s.getBytes();
		int first = -1;
		int last = -1;
		for(int i=0;i<buf.length;i++) {
			byte b = buf[i];
			if(b!=0&&b!=32&&b!=34) {
				first = i;
				break;
			}
		}
		for(int i=buf.length-1;i>=0;i--) {
			byte b = buf[i];
			if(b!=0&&b!=32&&b!=34) {
				last = i;
				break;
			}
		}
		int count = 0;
		for(int i=first+1;i<last;i++) {
			if(buf[i]>0)
				count++;
		}
		byte[] newBuf = new byte[last-first-count];
		int index = 0;
		for(int i=first;i<=last;i++) {
			byte b = buf[i];
			if(b>0) {
				newBuf[index++] = b;
			}
		}
		return new String(newBuf);
	}
}