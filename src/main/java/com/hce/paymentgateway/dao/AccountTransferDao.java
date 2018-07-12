package com.hce.paymentgateway.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hce.paymentgateway.entity.AccountTransferEntity;

/**
 * @Author Heling.Yao
 * @Date 10:20 2018/5/25
 */
@Repository
@Transactional
public interface AccountTransferDao extends JpaRepository<AccountTransferEntity, Long> {

    List<AccountTransferEntity> findByStatusAndQueryCountLessThan(Integer status, Integer queryCount, Pageable pageable);
    
    Optional<AccountTransferEntity> findById(Long id);

    @Modifying
    @Query(value = "update AccountTransferEntity set queryCount = :newQueryCount, modifyTime = :modifyTime where id = :id and queryCount = :oldQueryCount")
    int updateCountByIdAndCount(@Param("id") Long id, @Param("oldQueryCount") Integer oldQueryCount,
                                 @Param("newQueryCount") Integer newQueryCount, @Param("modifyTime") Date modifyTime);

    @Modifying
    @Query(value = "update AccountTransferEntity set status = :status, modifyTime = :modifyTime, transactionStatus = :transactionStatus, additionalInformation = :additionalInformation where id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("modifyTime") Date modifyTime ,
    		@Param("transactionStatus") String transactionStatus, @Param("additionalInformation") String additionalInformation);
    
    @Modifying
    @Query(value = "update AccountTransferEntity set fileName1 = :fileName1, ackFileType = :ackFileType, modifyTime = :modifyTime where id = :id")
    int updateFileName1ById(@Param("id") Long id, @Param("fileName1") String fileName1, @Param("ackFileType") String ackFileType, @Param("modifyTime") Date modifyTime);
    
    @Modifying
    @Query(value = "update AccountTransferEntity set paymentDate = :paymentDate, modifyTime = :modifyTime where id = :id")
    int updatePaymentDateById(@Param("id") Long id, @Param("paymentDate") String paymentDate, @Param("modifyTime") Date modifyTime);

}
