package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.DBSMT94XHeaderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSMT94XHeaderDao extends JpaRepository<DBSMT94XHeaderEntity, Long> {
	
}