package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.dao.DBSVASetupDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("vaSetupResponseProcessServiceImpl")
public class VASetupResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Autowired
	private DBSVASetupDao dbsVASetupDao;

	@Transactional
	@Override
	protected void process(File file) throws IOException {
		Workbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
			Sheet sheet = workbook.getSheetAt(0);
			int cursor = 1;
			while(true) {
				Row row = sheet.getRow(cursor++);
				if(row==null)
					break;
				Cell actionCell = row.getCell(0);
				if(actionCell==null)
					break;
				String action = actionCell.getStringCellValue().trim();
				if(action.length()==0) {
					break;
				}
				row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
				String vaNumber = row.getCell(3).getStringCellValue();
				int effected = dbsVASetupDao.updateByVANumber(vaNumber, file.getName(), row.getCell(6).getStringCellValue(), row.getCell(7).getStringCellValue(), row.getCell(4).getStringCellValue());
				if(effected==0) {
					log.error("\r\nVA_SETUP_ERROR_RESPONSE_NOT_FOUND: "+vaNumber+"--------------"+file.getName());
				}
				/*DBSVASetupEntity vasetup = new DBSVASetupEntity();
				vasetup.setCorp("HKHCEH");
				vasetup.setAction(action);
				row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
				vasetup.setCorpCode(row.getCell(1).getStringCellValue());
				vasetup.setRemitterPayerName(row.getCell(2).getStringCellValue());
				row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
				vasetup.setMasterAC(row.getCell(3).getStringCellValue());
				row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
				vasetup.setErpCode(row.getCell(4).getStringCellValue());
				row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
				vasetup.setStaticVASequenceNumber(row.getCell(5).getStringCellValue());
				vasetup.setResponseFile(file.getName());
				vasetup.setStatus(row.getCell(6).getStringCellValue());
				vasetup.setFailureReason(row.getCell(7).getStringCellValue());
				dbsVASetupDao.save(vasetup);*/
			}
		} finally {
			if(workbook!=null)
				workbook.close();
		}
	}
}