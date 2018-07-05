package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.AccountTransferEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author Heling.Yao
 * @Date 10:20 2018/5/25
 */
@Repository
@Transactional
public interface AccountTransferDao extends JpaRepository<AccountTransferEntity, Long> {

    List<AccountTransferEntity> findByStatusAndQueryCountLessThan(Integer status, Integer queryCount, Pageable pageable);

    @Modifying
    @Query(value = "update AccountTransferEntity set queryCount = :newQueryCount, modifyTime = :modifyTime where id = :id and queryCount = :oldQueryCount")
    int updateCountByIdAndCount(@Param("id") Long id, @Param("oldQueryCount") Integer oldQueryCount,
                                 @Param("newQueryCount") Integer newQueryCount, @Param("modifyTime") Date modifyTime);

    @Modifying
    @Query(value = "update AccountTransferEntity set status = :status, modifyTime = :modifyTime where id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("modifyTime") Date modifyTime);

}
