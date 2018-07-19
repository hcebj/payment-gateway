package com.hce.paymentgateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hce.paymentgateway.dao.BatchIdProDao;
import com.hce.paymentgateway.entity.BatchIdProEntity;

@Component
public class AccountTransferGetBatchId {
	
	@Autowired
    private   BatchIdProDao batchIdProDao;
	
	public   String getBatcId(String paymentDate){
		
		BatchIdProEntity batchIdProEntity = batchIdProDao.findByPaymentDate(paymentDate);
		String batchId;
	    if(batchIdProEntity!=null){
	    	batchId = String.format("%05d", batchIdProEntity.getBatchIdSeq());
	    	batchIdProEntity.setBatchIdSeq(batchIdProEntity.getBatchIdSeq() +1);
	    	batchIdProDao.updatebatchIdSeqById(batchIdProEntity.getId(), batchIdProEntity.getBatchIdSeq());
	    }else{
	    	batchIdProEntity = new BatchIdProEntity();
	    	batchId = "00001";
	    	batchIdProEntity.setBatchIdSeq(1L);
	    	batchIdProEntity.setPaymentDate(paymentDate);
	    	batchIdProDao.save(batchIdProEntity);
	    	
	    }
		return batchId;
	}
	
}
