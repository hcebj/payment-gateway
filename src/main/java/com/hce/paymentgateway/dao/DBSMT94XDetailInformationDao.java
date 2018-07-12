package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSMT94XInformationEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSMT94XDetailInformationDao extends JpaRepository<DBSMT94XInformationEntity, Long> {
	
}