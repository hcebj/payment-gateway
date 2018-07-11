package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSVASetupResponseEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DBSVASetupResponseDao extends JpaRepository<DBSVASetupResponseEntity, Long> {
	
}