package com.hce.paymentgateway.service.impl;

import com.hce.paymentgateway.api.dbs.RequestHeader;
import com.hce.paymentgateway.api.dbs.RequestDetails;
import com.hce.paymentgateway.api.dbs.Trailer;
import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.dao.AccountInfoDao;
import com.hce.paymentgateway.entity.AccountInfoEntity;
import com.hce.paymentgateway.entity.BaseEntity;
import com.hce.paymentgateway.service.TransactionService;
import com.hce.paymentgateway.util.CommonUtil;
import com.hce.paymentgateway.util.DBSDataFormat;
import com.hce.paymentgateway.util.FileNameGenerator;
import com.hce.paymentgateway.util.SCPFileUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.List;

import static com.hce.paymentgateway.util.Constant.LINUX_LINE_BREAK;

/**
 * @Author Heling.Yao
 * @Date 14:01 2018/5/28
 */
@Slf4j
public abstract class AbstractTransactionService<T extends TradeRequest> implements TransactionService<T>  {

    @Resource(name = "SCPFileUtils")
    private SCPFileUtils SCPFileUtils;

    @Autowired
    private AccountInfoDao accountInfoDao;

    /**
     * 请求报文以FTP形式发送到服务器
     * @param request
     * @throws PGPException 
     * @throws SftpException 
     * @throws IOException 
     * @throws JSchException 
     * @throws NoSuchProviderException 
     * @throws ParseException 
     */
    protected void ftpRequestDBS(TradeRequest request, BaseEntity details) throws NoSuchProviderException, JSchException, IOException, SftpException, PGPException, ParseException {
        String dbsData = assembleData(details);
        String fileName = FileNameGenerator.generateRequestFileName(request);
        log.info("\n[网关支付]组装DBS请求数据, 文件名 {}, 消息体\n{}", fileName, dbsData);
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(dbsData.getBytes());
            //InputStream inputStream = new BufferedInputStream(new FileInputStream("/home/wsh/in/test/UFF1.STP.HKHCEH.HKHCEH.201807060025.txt.DHBKHKHH.pgp"));
            SCPFileUtils.uploadFileFromServer(fileName, inputStream, null);
        } finally {
        	if(inputStream!=null)
        		inputStream.close();
        }
    }

    /**
     * 组装DBS数据
     * @param details
     * @return
     * @throws ParseException 
     */
    private String assembleData(BaseEntity details) throws ParseException {
        RequestHeader header = new RequestHeader();
        addAccountInfoToHeader(details, header);
        RequestDetails requestDetails = new RequestDetails();
        BeanUtils.copyProperties(details, requestDetails);
        log.info("PaymentDate-------------"+requestDetails.getPaymentDate());
        requestDetails.setPaymentDate(CommonUtil.getFormatDate(requestDetails.getPaymentDate(), "yyyyMMdd", "ddMMyyyy"));
        Trailer trailer = new Trailer();
        trailer.setTotalTransactionAmount(details.getAmount());
        String headerValue = DBSDataFormat.format(header);
        String detailsValue = DBSDataFormat.format(requestDetails);
        String trailerValue = DBSDataFormat.format(trailer);
        String dbsData = headerValue + LINUX_LINE_BREAK
            + detailsValue + LINUX_LINE_BREAK
            + trailerValue;
        return dbsData;
    }

    private void addAccountInfoToHeader(BaseEntity details, RequestHeader header) {
        List<AccountInfoEntity> accountInfoList = accountInfoDao.findByPaymentOrgIdAndEnabled(details.getPaymentOrgId(), true);
        header.setOrganizationId(accountInfoList.get(0).getOrganizationId());
        header.setSenderName(accountInfoList.get(0).getSenderName());
    }
}