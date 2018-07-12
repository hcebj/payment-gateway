package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSMT940Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSMT940Dao extends JpaRepository<DBSMT940Entity, Long> {
	
}