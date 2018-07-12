package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSMT942Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSMT942Dao extends JpaRepository<DBSMT942Entity, Long> {
	
}