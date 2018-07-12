package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSMT94XDetailEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSMT94XDetailDao extends JpaRepository<DBSMT94XDetailEntity, Long> {
	
}