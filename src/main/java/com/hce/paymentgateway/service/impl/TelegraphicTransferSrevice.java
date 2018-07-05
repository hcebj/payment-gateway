package com.hce.paymentgateway.service.impl;

import static com.hce.paymentgateway.util.PaymentStatus.PROCESSING;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hce.paymentgateway.api.hce.ChatsPaymentRequest;
import com.hce.paymentgateway.api.hce.TelegraphicTransferRequest;
import com.hce.paymentgateway.api.hce.TradeResponse;
import com.hce.paymentgateway.dao.AccountTransferDao;
import com.hce.paymentgateway.entity.AccountTransferEntity;
import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.util.ServiceParameter;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author Heling.Yao
 * @Date 9:53 2018/5/25
 */
@Slf4j
@Service("telegraphicTransferSrevice")
@ServiceParameter(productType = Constant.TELEGRAPHIC_TRANSFER)
public class TelegraphicTransferSrevice extends AbstractTransactionService<TelegraphicTransferRequest> {

    @Resource(name = "accountTransferDao")
    private AccountTransferDao accountTransferDao;

    @Transactional
    @Override
    public TradeResponse handle(TelegraphicTransferRequest tradeRequest) {
    	//写业务逻辑
        AccountTransferEntity transfer = new AccountTransferEntity();
        BeanUtils.copyProperties(tradeRequest, transfer);
        transfer.setStatus(PROCESSING.getStatus());
        accountTransferDao.save(transfer);
        log.info("[网关支付]数据入库成功, id = {}, transId= {}", transfer.getId(), transfer.getTransId());
        ftpRequestDBS(tradeRequest, transfer);
        TradeResponse response = new TradeResponse();
        BeanUtils.copyProperties(tradeRequest, response);
        return response;
    }

}
