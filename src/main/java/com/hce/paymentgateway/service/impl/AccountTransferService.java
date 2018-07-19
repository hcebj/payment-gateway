package com.hce.paymentgateway.service.impl;

import static com.hce.paymentgateway.util.PaymentStatus.PROCESSING;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.text.ParseException;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.util.StringUtil;
import com.hce.paymentgateway.api.hce.AccountTransferRequest;
import com.hce.paymentgateway.api.hce.TradeResponse;
import com.hce.paymentgateway.dao.AccountTransferDao;
import com.hce.paymentgateway.dao.BatchIdProDao;
import com.hce.paymentgateway.entity.AccountTransferEntity;
import com.hce.paymentgateway.entity.BatchIdProEntity;
import com.hce.paymentgateway.util.CommonUtil;
import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.util.FileNameGenerator;
import com.hce.paymentgateway.util.ServiceParameter;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author Heling.Yao
 * @Date 9:53 2018/5/25
 */
@Slf4j
@Service("accountTransferService")
@ServiceParameter(productType = Constant.ACCOUNT_TRANSFER)
public class AccountTransferService extends AbstractTransactionService<AccountTransferRequest> {
	@Autowired
    private AccountTransferDao accountTransferDao;
	@Autowired
    private AccountTransferGetBatchId accountTransferGetBatchId;
    @Transactional
    @Override
    public TradeResponse handle(AccountTransferRequest tradeRequest) throws NoSuchProviderException, JSchException, IOException, SftpException, PGPException, ParseException {
        AccountTransferEntity transfer = new AccountTransferEntity();
        BeanUtils.copyProperties(tradeRequest, transfer);
        transfer.setStatus(PROCESSING.getStatus());
       // BatchIdProEntity batchIdProEntity = batchIdProDao.findBypaymentDate(transfer.getPaymentDate());
        
        String batchId = accountTransferGetBatchId.getBatcId(transfer.getPaymentDate());
        
        transfer.setPaymentId(CommonUtil.getNumberForPK()); //支付流水号
        transfer.setBatchId(batchId);//TODO 测试用,此处之后需变动
        //transfer.setBatchId(CommonUtil.getRandomString(5)); //支付批次号
        transfer.setTransactionStatus("SEND");
        tradeRequest.setPaymentId(transfer.getPaymentId());
        transfer.setCustomerOrBatchReference(transfer.getPaymentId());//域D05赋值自己产生的流水号
        transfer.setFileName(FileNameGenerator.generateRequestFileName(tradeRequest));
        accountTransferDao.save(transfer);
        log.info("[网关支付]数据入库成功, id = {},corp = {} transTime = {} transId = {}, paymentId = {}", transfer.getCorp(),transfer.getTransTime(), transfer.getId(), transfer.getTransId(), transfer.getPaymentId());
        ftpRequestDBS(tradeRequest, transfer);
        TradeResponse response = new TradeResponse();
        BeanUtils.copyProperties(tradeRequest, response);
        return response;
    }
    
    
}