package com.hce.paymentgateway.dao;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hce.paymentgateway.entity.BatchIdProEntity;

/**
 * @Author Heling.Yao
 * @Date 10:20 2018/5/25
 */
@Repository
@Transactional
public interface BatchIdProDao extends JpaRepository<BatchIdProEntity, Long> {

	BatchIdProEntity findByPaymentDate(String paymentDate);
	
	@Modifying
    @Query(value = "update BatchIdProEntity set batchIdSeq = :batchIdSeq where id = :id")
    int updatebatchIdSeqById(@Param("id") Long id, @Param("batchIdSeq") Long batchIdSeq);
    

}
