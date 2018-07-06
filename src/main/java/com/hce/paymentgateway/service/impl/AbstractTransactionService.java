package com.hce.paymentgateway.service.impl;

import com.hce.paymentgateway.api.dbs.RequestHeader;
import com.hce.paymentgateway.api.dbs.RequestDetails;
import com.hce.paymentgateway.api.dbs.Trailer;
import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.dao.AccountInfoDao;
import com.hce.paymentgateway.entity.AccountInfoEntity;
import com.hce.paymentgateway.entity.BaseEntity;
import com.hce.paymentgateway.service.TransactionService;
import com.hce.paymentgateway.util.DBSDataFormat;
import com.hce.paymentgateway.util.FileNameGenerator;
import com.hce.paymentgateway.util.SCPFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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
     */
    protected void ftpRequestDBS(TradeRequest request, BaseEntity details) {
        String dbsData = assembleData(details);
        //String fileName = FileNameGenerator.generateRequestFileName(request);
        String fileName = "/home/wsh/in/test/UFF1.STP.HKHCEH.HKHCEH.201807060025.txt.DHBKHKHH.pgp";
        log.info("\n[网关支付]组装DBS请求数据, 文件名 {}, 消息体\n{}", fileName, dbsData);
        try {
            //ByteArrayInputStream inputStream = new ByteArrayInputStream(dbsData.getBytes());
            InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
            SCPFileUtils.uploadFileFromServer(fileName, inputStream);
        } catch (Throwable t) {
            log.error("[DBS服务]数据报文上送异常", t);
        }
    }

    /**
     * 组装DBS数据
     * @param details
     * @return
     */
    private String assembleData(BaseEntity details) {
        RequestHeader header = new RequestHeader();
        addAccountInfoToHeader(details, header);

        RequestDetails requestDetails = new RequestDetails();
        BeanUtils.copyProperties(details, requestDetails);

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
