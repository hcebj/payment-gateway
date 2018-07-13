package com.hce.paymentgateway.dao;

import com.hce.paymentgateway.entity.AccountInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Heling.Yao
 * @Date 16:58 2018/6/06
 */
@Repository
public interface AccountInfoDao extends JpaRepository<AccountInfoEntity, Integer> {
    List<AccountInfoEntity> findByCorpAndEnabled(String corp, boolean enabled);
}
