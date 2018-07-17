package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.dao.DBSVASetupDao;
import com.hce.paymentgateway.entity.vo.DBSVASetupVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("vaSetupResponseProcessServiceImpl")
public class VASetupResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Autowired
	private DBSVASetupDao dbsVASetupDao;

	@Override
	protected Object process(File file) throws IOException {
		String customerId = file.getName().substring(0, file.getName().indexOf("."));
		Workbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
			Sheet sheet = workbook.getSheetAt(0);
			int cursor = 1;
			List<DBSVASetupVO> list = new ArrayList<DBSVASetupVO>();
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
				String status = row.getCell(6).getStringCellValue();
				String failureReason = row.getCell(7).getStringCellValue();
				int effected = dbsVASetupDao.updateByVANumber(vaNumber, file.getName(), status, failureReason, row.getCell(4).getStringCellValue(), customerId);
				if(effected==0) {
					log.error("\r\nVA_SETUP_ERROR_RESPONSE_NOT_FOUND: "+vaNumber+"--------------"+file.getName());
				} else {
					DBSVASetupVO vo = new DBSVASetupVO();
					vo.setCorp(dbsVASetupDao.findByMasterAC(vaNumber).getCorp());
					vo.setMasterAC(vaNumber);
					vo.setStatus(status);
					vo.setFailureReason(failureReason);
					list.add(vo);
				}
			}
			return list;
		} finally {
			if(workbook!=null)
				workbook.close();
		}
	}

	@Override
	public String getMsgTag() {
		return "35043";
	}

	@Override
	protected String getCorp() {
		return "9992";
	}
}