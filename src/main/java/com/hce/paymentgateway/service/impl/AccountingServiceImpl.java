package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;
import com.hce.paymentgateway.dao.DBSVAReportDao;
import com.hce.paymentgateway.dao.DBSVASetupDao;
import com.hce.paymentgateway.entity.DBSVAReportEntity;
import com.hce.paymentgateway.entity.DBSVASetupEntity;
import com.hce.paymentgateway.service.AccountingService;
import com.prowidesoftware.swift.model.mt.AbstractMT;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;

@Service
public class AccountingServiceImpl implements AccountingService {
	@Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;
	@Autowired
	private DBSVAReportDao dbsVAReportDao;
	@Autowired
	private DBSVASetupDao dbsVASetupDao;

	public void process(List<File> files) throws IOException, ParseException, InvalidFormatException {
		File historyDir = new File(localTempDir+"/history");
		if(!historyDir.exists()) {
			historyDir.mkdirs();
		}
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		for(File file:files) {
			if(file.getName().endsWith(".csv")) {//VA Report
				String parentId = file.getName().substring(0, file.getName().indexOf("."));
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
							DBSVAReportEntity vareport = new DBSVAReportEntity();
							vareport.setCorp(parentId);
							vareport.setType(type);
//			                vareport.setSno(normalize(csvReader.get(0)));
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
			                	vareport.setTransactionDate(df.parse(transactionDate));
			                if(valueDate.length()>0)
			                	vareport.setValueDate(df.parse(valueDate));
			                vareport.setChannelId(normalize(csvReader.get(17)));
							dbsVAReportDao.save(vareport);
						}
		            }
				} finally {
					if(csvReader!=null)
						csvReader.close();
				}
			} else if(file.getName().indexOf("_DSG_VAHKL_RESP_")>0&&(file.getName().endsWith(".xls")||file.getName().endsWith(".xlsx"))) {//VA Setup Instruction
				XSSFWorkbook workbook = null;
				try {
					workbook = new XSSFWorkbook(file);
					XSSFSheet sheet = workbook.getSheetAt(0);
					int cursor = 1;
					while(true) {
						XSSFRow row = sheet.getRow(cursor++);
						XSSFCell actionCell = row.getCell(0);
						if(actionCell==null)
							break;
						String action = actionCell.getStringCellValue().trim();
						if(action.length()==0) {
							break;
						}
						DBSVASetupEntity vasetup = new DBSVASetupEntity();
						vasetup.setCorp("HKHCEH");
						vasetup.setAction(action);
						vasetup.setCorp(row.getCell(1).getStringCellValue());
						vasetup.setRemitterPayerName(row.getCell(2).getStringCellValue());
						vasetup.setMasterAC(row.getCell(3).getStringCellValue());
						vasetup.setErpCode(row.getCell(4).getStringCellValue());
						vasetup.setStaticVASequenceNumber(row.getCell(5).getStringCellValue());
						vasetup.setStatus(row.getCell(6).getStringCellValue());
						vasetup.setFailureReason(row.getCell(7).getStringCellValue());
						dbsVASetupDao.save(vasetup);
					}
					workbook.close();
				} finally {
					if(workbook!=null)
						workbook.close();
				}
			} else if(file.getName().endsWith(".txt")) {//MT94*
//				AbstractMT.parse("")
//				MT103.parse("")
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
			if(b!=0) {
				newBuf[index++] = b;
			}
		}
		return new String(newBuf);
	}

	public static void main(String[] args) {
//		AbstractMT.parse("").
	}
}