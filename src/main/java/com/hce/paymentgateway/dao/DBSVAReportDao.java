package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSVAReportEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DBSVAReportDao extends JpaRepository<DBSVAReportEntity, Long> {
	
}