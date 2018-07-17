package com.hce.paymentgateway.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hce.paymentgateway.entity.PayBaseInfoEntity;

@Repository
public interface PayBaseInfoDao extends JpaRepository<PayBaseInfoEntity, Long> {
	@Modifying
	@Query("UPDATE PayBaseInfoEntity t SET t.fieldValue=:fieldValue WHERE t.id=:id and t.fileStatus=:fileStatus")
	public  int updateById(@Param(value="id")Long id, @Param(value="fieldValue") String fieldValue, @Param(value="fieldStatus") String fieldStatus);
	
//	public PayBaseInfoEntity findByFieldName(String corp, String fieldName);
}