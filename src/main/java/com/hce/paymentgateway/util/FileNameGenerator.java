package com.hce.paymentgateway.util;

import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.entity.BaseEntity;

import java.util.UUID;

/**
 * 文件名生成及解析工具类
 * @Author Heling.Yao
 * @Date 16:00 2018/6/1
 */
public class FileNameGenerator {

    /**
     * 生成文件名
     * UFF1.STP.HKHCEH.<CUSTOMER ID>.<CustomerFilename>.<Ext>.DHBKHKHH.<Encryption Extension>
     * e.g. UFF1.STP.HKHCEH.HKHCEH.23461.txt.DHBKHKHH.pgp
     * @return
     */
    public static String generateRequestFileName(TradeRequest request) {
        StringBuilder fileName = new StringBuilder();
        //需要参数化
        if ("9992".equals(request.getPaymentOrgId())){
        	fileName.append("UFF1.STP.HKHCEH.HKHCEH.");
        	fileName.append(request.getPaymentId()).append(".");
        	fileName.append("txt.DHBKHKHH.pgp");
        } else {
	        fileName.append("UFF1.STP.HKHCEH.HKBRHCEC.");
	        fileName.append(request.getPaymentId()).append(".");
	        fileName.append("txt.DBSSHKHH.pgp");
        }
        return fileName.toString();
    }

    /**
     * ACK1
     * UFF1.STP.<CUSTOMER ID>.<CUSTOMER ID>.<CustomerFilename>.<Ext>.DBSSHKHH.<Timestamp>.ACK1.<Encryption Extension>
     * e.g. UFF1.STP.HKGTSA.HKGTSA.23461.txt.DBSSHKHH.D20150703T160201.ACK1.pgp
     *
     * ACK2
     * UFF1.STP.<CUSTOMER ID>.<CUSTOMER ID>.<CustomerFilename>.<Ext>.DBSSHKHH.<Timestamp>.ACK2.<Encryption Extension>
     * e.g. UFF1.STP.HKGTSA.HKGTSA.23461.txt.DBSSHKHH.D20150703T160201.ACK2.pgp
     *
     * ACK3
     * UFF1.STP.<CUSTOMER ID>.<CUSTOMER ID>.<CustomerFilename>.<Ext>.DBSSHKHH.<Timestamp>.ACK3.<Encryption Extension>
     * e.g. UFF1.STP.HKGTSA.HKGTSA.23461.txt.DBSSHKHH.D20150703T160201121.ACK3.pgp
     */
    public static String generateAckFileName(BaseEntity baseEntity) {
        return baseEntity.getPaymentId();
    }


}
