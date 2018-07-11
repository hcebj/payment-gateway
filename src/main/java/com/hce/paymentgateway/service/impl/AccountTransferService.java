package com.hce.paymentgateway.service.impl;

import com.hce.paymentgateway.api.hce.TradeResponse;
import com.hce.paymentgateway.api.hce.AccountTransferRequest;
import com.hce.paymentgateway.dao.AccountTransferDao;
import com.hce.paymentgateway.entity.AccountTransferEntity;
import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.util.FileNameGenerator;
import com.hce.paymentgateway.util.ServiceParameter;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.hce.paymentgateway.util.PaymentStatus.PROCESSING;

import java.io.IOException;
import java.security.NoSuchProviderException;

/**
 * @Author Heling.Yao
 * @Date 9:53 2018/5/25
 */
@Slf4j
@Service("accountTransferService")
@ServiceParameter(productType = Constant.ACCOUNT_TRANSFER)
public class AccountTransferService extends AbstractTransactionService<AccountTransferRequest> {
    @Resource(name = "accountTransferDao")
    private AccountTransferDao accountTransferDao;

    @Transactional
    @Override
    public TradeResponse handle(AccountTransferRequest tradeRequest) throws NoSuchProviderException, JSchException, IOException, SftpException, PGPException {
        AccountTransferEntity transfer = new AccountTransferEntity();
        BeanUtils.copyProperties(tradeRequest, transfer);
        transfer.setStatus(PROCESSING.getStatus());
        transfer.setTransactionStatus("SEND");
        transfer.setFileName(FileNameGenerator.generateRequestFileName(tradeRequest));
        accountTransferDao.save(transfer);
        log.info("[网关支付]数据入库成功, id = {}, transId= {}", transfer.getId(), transfer.getTransId());
        ftpRequestDBS(tradeRequest, transfer);
        TradeResponse response = new TradeResponse();
        BeanUtils.copyProperties(tradeRequest, response);
        return response;
    }
}