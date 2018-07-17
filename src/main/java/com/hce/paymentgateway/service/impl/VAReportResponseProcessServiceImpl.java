package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;
import com.hce.paymentgateway.dao.DBSVAReportDao;
import com.hce.paymentgateway.entity.DBSVAReportEntity;

@Service("vaReportResponseProcessServiceImpl")
public class VAReportResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Autowired
	private DBSVAReportDao dbsVAReportDao;

	@Override
	protected Object process(File file) throws IOException, ParseException {
		String customerId = file.getName().substring(0, file.getName().indexOf("."));
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
			List<DBSVAReportEntity> list = new ArrayList<DBSVAReportEntity>();
			while(csvReader.readRecord()) {
				String vaNumber = normalize(csvReader.get(1));
				if(vaNumber!=null&&vaNumber.length()>0) {
					DBSVAReportEntity vareport = new DBSVAReportEntity();
					vareport.setResponseFile(file.getName());
					vareport.setCustomerId(customerId);
					vareport.setType(type);
//	                vareport.setSno(normalize(csvReader.get(0)));
					vareport.setVaNumber(vaNumber);
					vareport.setBankAccountNumber(normalize(csvReader.get(2)));
					vareport.setBeneficiaryName(normalize(csvReader.get(3)));
					vareport.setVaName(normalize(csvReader.get(4)));
					vareport.setRemitterName(normalize(csvReader.get(5)));
					vareport.setRemitterDetails(normalize(csvReader.get(6)));
					vareport.setRemitterBankCode(normalize(csvReader.get(7)));
					vareport.setRemitCurrency(normalize(csvReader.get(8)));
	                String remitAmount = normalize(csvReader.get(9));
	                String creditAmount = normalize(csvReader.get(11));
	                if(remitAmount.length()>0)
	                	vareport.setRemitAmount(new BigDecimal(remitAmount));
	                vareport.setCreditCurrency(normalize(csvReader.get(10)));
	                if(creditAmount.length()>0)
	                	vareport.setCreditAmount(new BigDecimal(creditAmount));
	                vareport.setSendersReference(normalize(csvReader.get(12)));
	                vareport.setTransactionDetail(normalize(csvReader.get(13)));
	                vareport.setBankReference(normalize(csvReader.get(14)));
	                String transactionDate = normalize(csvReader.get(15));
	                String valueDate = normalize(csvReader.get(16));
	                if(transactionDate.length()>0)
	                	vareport.setTransactionDate(transactionDate);
	                if(valueDate.length()>0)
	                	vareport.setValueDate(valueDate);
	                vareport.setChannelId(normalize(csvReader.get(17)));
					dbsVAReportDao.save(vareport);
					list.add(vareport);
				}
            }
			return list;
		} finally {
			if(csvReader!=null)
				csvReader.close();
		}
	}

	@Override
	public String getMsgTag() {
		return "35040";
	}

	@Override
	protected String getCorp() {
		return "9992";
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
			if(b!=0) {
				newBuf[index++] = b;
			}
		}
		return new String(newBuf);
	}
}