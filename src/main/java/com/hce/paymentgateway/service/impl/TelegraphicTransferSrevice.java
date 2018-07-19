package com.hce.paymentgateway.service.impl;

import static com.hce.paymentgateway.util.PaymentStatus.PROCESSING;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hce.paymentgateway.api.hce.TelegraphicTransferRequest;
import com.hce.paymentgateway.api.hce.TradeResponse;
import com.hce.paymentgateway.dao.AccountTransferDao;
import com.hce.paymentgateway.entity.AccountTransferEntity;
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
@Service("telegraphicTransferSrevice")
@ServiceParameter(productType = Constant.TELEGRAPHIC_TRANSFER)
public class TelegraphicTransferSrevice extends AbstractTransactionService<TelegraphicTransferRequest> {
	@Autowired
    private AccountTransferDao accountTransferDao;
	@Autowired
    private AccountTransferGetBatchId accountTransferGetBatchId;
    @Transactional
    @Override
    public TradeResponse handle(TelegraphicTransferRequest tradeRequest) throws NoSuchProviderException, JSchException, IOException, SftpException, PGPException, ParseException {
    	//写业务逻辑
        AccountTransferEntity transfer = new AccountTransferEntity();
        BeanUtils.copyProperties(tradeRequest, transfer);
        transfer.setStatus(PROCESSING.getStatus());
        transfer.setPaymentId(this.getNumberForPK()); //支付流水号
        transfer.setTransactionStatus("SEND");
        tradeRequest.setPaymentId(transfer.getPaymentId());
        String batchId = accountTransferGetBatchId.getBatcId(transfer.getPaymentDate());
        transfer.setBatchId(batchId);//TODO 测试用,此处之后需变动
        transfer.setCustomerOrBatchReference(transfer.getPaymentId());//域D05赋值自己产生的流水号
        transfer.setFileName(FileNameGenerator.generateRequestFileName(tradeRequest));
        accountTransferDao.save(transfer);
        log.info("[网关支付]数据入库成功, id = {}, transId= {}", transfer.getId(), transfer.getTransId());
        ftpRequestDBS(tradeRequest, transfer);
        TradeResponse response = new TradeResponse();
        BeanUtils.copyProperties(tradeRequest, response);
        return response;
    }
    
    /**
	 * @描述 java生成流水号 
	 * 14位时间戳 + 6位随机数
	 * @作者 shaomy
	 * @时间:2015-1-29 上午10:57:41
	 * @参数:@return 
	 * @返回值：String
	 */
	public String getNumberForPK(){
		String id="";
    	SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
    	String temp = sf.format(new Date());
		//int random=(int) (Math.random()*10000);
		String random = String.format("%04d", (int) (Math.random()*10000));
		id=temp+random;
		log.info("[网关支付]支付流水号, id = {}", id ); 
		return id;
	}
}