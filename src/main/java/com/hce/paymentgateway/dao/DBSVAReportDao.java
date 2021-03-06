package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSVAReportEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSVAReportDao extends JpaRepository<DBSVAReportEntity, Long> {
	
}