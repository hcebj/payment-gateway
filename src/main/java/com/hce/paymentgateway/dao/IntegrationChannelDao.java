package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.IntegrationChannelEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationChannelDao extends JpaRepository<IntegrationChannelEntity, Long> {
	public IntegrationChannelEntity findByShortName(String shortName);
}