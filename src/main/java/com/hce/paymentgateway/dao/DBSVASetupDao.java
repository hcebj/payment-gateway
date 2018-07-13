package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSVASetupEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSVASetupDao extends JpaRepository<DBSVASetupEntity, Long> {
	@Modifying
	@Query("UPDATE DBSVASetupEntity t SET t.responseFile=:responseFile,t.status=:status,t.failureReason=:failureReason,t.erpCode=:erpCode WHERE t.masterAC=:vaNumber")
	public int updateByVANumber(@Param(value="vaNumber")String vaNumber, @Param(value="responseFile")String responseFile, @Param(value="status")String status, @Param(value="failureReason")String failureReason, @Param(value="erpCode")String erpCode);
}