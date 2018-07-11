package com.hce.paymentgateway.job;

import com.google.common.base.Charsets;
import com.hce.paymentgateway.api.dbs.*;
import com.hce.paymentgateway.entity.BaseEntity;
import com.hce.paymentgateway.service.AccountingService;
import com.hce.paymentgateway.util.DBSDataFormat;
import com.hce.paymentgateway.util.FileNameGenerator;
import com.hce.paymentgateway.util.PaymentStatus;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * @Author Heling.Yao
 * @Date 15:03 2018/6/1
 */
@Slf4j
public abstract class AbstractSchedulingService<T extends BaseEntity> {
    protected int interval = 1;

    @Resource(name = "SCPFileUtils")
    private com.hce.paymentgateway.util.SCPFileUtils SCPFileUtils;
    @Autowired
    private AccountingService accountingService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void queryTransactionStatus(){
    	int init = 0;
        while(true) {
            try {
                boolean isContinue = processTransaction(init, interval);
                if(!isContinue) break;
            } catch(Throwable t){
                log.error("Scheduling error", t);
            } finally {
                ++init;
            }
        }
    }

//    @Scheduled(cron = "0 0/5 * * * ?")
    public void process() throws JSchException, SftpException, IOException, ParseException {
    	List<File> files = SCPFileUtils.downloadFilesFromServer("HKHCEH");//海云汇香港
    	accountingService.process(files);
    	files = SCPFileUtils.downloadFilesFromServer("HKBRHCEC");//海云汇国际
    	accountingService.process(files);
    }

    /**
     * 0. 查询处理中数据
     * 1. 数据更新是否成功, 成功继续执行，失败则跳出
     * 2. 根据文件名查询FTP服务器数据
     * 3. 文件格式转换
     * 4. 成功更新数据库, 发送返回消息
     * 5. 处理中继续查询
     */
    private boolean processTransaction(int init, int interval) throws Exception {
        boolean isContinue = true;
        // 0. 查询处理中数据
        List<T> accounts = queryProcessingData(init, interval);
        if(CollectionUtils.isEmpty(accounts)) {
            isContinue = false;
            return isContinue;
        }
        for(T transfer : accounts) {
            // 1. 数据更新是否成功, 成功继续执行，失败则跳出
            boolean update = updateQueryCount(transfer);
            if(!update) continue;
            // 2. 根据文件名查询FTP服务器数据
            String fileName = FileNameGenerator.generateAckFileName(transfer);
            List<File> resultFiles = SCPFileUtils.downloadFilesFromServerAndDecrypt(fileName);
            if(fileName.equals("201807090012")) {
            	log.info("wwwwwwwwwww");
            	resultFiles  = SCPFileUtils.testack(resultFiles);
            }
            // 3. 文件格式转换
            AckResult ackResult = handleACK1(transfer, resultFiles);
            if(!ackResult.isNextHandler()) continue;
            ackResult = handleACK2(transfer, resultFiles);
            if(!ackResult.isNextHandler()) continue;
            handleACK3(transfer, resultFiles);
        };
        return isContinue;
    }

    protected abstract List<T> queryProcessingData(int init, int interval);

    protected abstract boolean updateQueryCount(T transfer);

    protected abstract void updatePaymentStatus(T transfer, PaymentStatus paymentStatus, String errorCode, String errorMsg);

    private <R>R parseFile(File file, Class headerClass, Class detailsClass, Class<R> responseClass) throws Exception {
        if(file == null) {
            return null;
        }
        InputStream in = new FileInputStream(file);
        List<String> ack = IOUtils.readLines(in, Charsets.UTF_8.name());
        log.info(String.valueOf(ack.size()));
        if(CollectionUtils.isEmpty(ack) || ack.size() > 3 || ack.size() < 2) {
        	if(!file.getName().contains("ACK1")){
        		log.error((!file.getName().contains("ACK1"))+"============"+file.getName());
        		log.error("[DBS服务]文件格式异常, fileName = " + file.getName());
                return null;
        	}
        }
        String headerValue = ack.get(0);
        String detailsValue = null, trailerValue = null;
        if(ack.size() == 2) {
            trailerValue = ack.get(1);
        } else if(file.getName().contains("ACK1")){
        	log.info("ACK1");
        } else {
            detailsValue = ack.get(1);
            trailerValue = ack.get(2);
        }
        Object header = DBSDataFormat.parse(headerValue, headerClass);
        Object details = StringUtils.isEmpty(detailsValue) ? null : DBSDataFormat.parse(detailsValue, detailsClass);
        Trailer trailer = DBSDataFormat.parse(trailerValue, Trailer.class);

        if(responseClass.equals(ACK1Response.class)) {
            ACK1Response response = new ACK1Response();
            response.setAck1Header((ACK1Header) header);
            //response.setTrailer(trailer);
            return (R) response;
        } else if(responseClass.equals(ACK2Response.class)) {
            ACK2Response response = new ACK2Response();
            response.setAck2Header((ACK2Header) header);
            response.setAck2Details((ACK2Details) details);
            response.setTrailer(trailer);
            return (R) response;
        } else if(responseClass.equals(ACK3Response.class)) {
            ACK3Response response = new ACK3Response();
            response.setAck3Header((ACK3Header) header);
            response.setAck3Details((ACK3Details) details);
            response.setTrailer(trailer);
            return (R) response;
        }
        return null;
    }

    private AckResult handleACK1(T transfer, List<File> resultFiles) throws Exception {
        AckResult ackResult = new AckResult();
        File ack1File = getACK(resultFiles, "ACK1");
        log.info("qqqqqqqqq");
        if(ack1File == null) return ackResult;
        log.info("sssssssss");
        ACK1Response ack1Response = parseFile(ack1File, ACK1Header.class, null, ACK1Response.class);
        log.info("aaaaaaaaaa");
        if(ack1Response == null || ack1Response.getAck1Header() == null ||
            StringUtils.isEmpty(ack1Response.getAck1Header().getGroupStatus())) {
        	log.info("vvvvvvv");
            return ackResult;
        }
        getPaymentStatus(transfer, ackResult, ack1Response.getAck1Header().getGroupStatus(), null,ack1Response.getAck1Header().getAdditionalInformation());
        return ackResult;
    }

    private AckResult handleACK2(T transfer, List<File> resultFiles) throws Exception {
        AckResult ackResult = new AckResult();
        File ack2File = getACK(resultFiles, "ACK2");
        if(ack2File == null) return ackResult;
        ACK2Response ack2Response = parseFile(ack2File, ACK2Header.class, ACK2Details.class, ACK2Response.class);
        if(ack2Response == null || ack2Response.getAck2Header() == null
            || StringUtils.isEmpty(ack2Response.getAck2Header().getGroupStatus())
            || StringUtils.isEmpty(ack2Response.getAck2Details().getTransactionStatus())) {
            return ackResult;
        }
        getPaymentStatus(transfer, ackResult, ack2Response.getAck2Header().getGroupStatus(), ack2Response.getAck2Details().getTransactionStatus(),ack2Response.getAck2Details().getAdditionalInformation());
        return ackResult;
    }

    private AckResult handleACK3(T transfer, List<File> resultFiles) throws Exception {
        AckResult ackResult = new AckResult();
        File ack3File = getACK(resultFiles, "ACK3");
        if(ack3File == null) return ackResult;
        log.info("there will be assignment!");
        ACK3Response ack3Response = parseFile(ack3File, ACK3Header.class, ACK3Details.class, ACK3Response.class);
        if(ack3Response == null
            || ack3Response.getAck3Header() == null || StringUtils.isEmpty(ack3Response.getAck3Header().getGroupStatus())
            || ack3Response.getAck3Details() == null || StringUtils.isEmpty(ack3Response.getAck3Details().getTransactionStatus())) {
        	log.info("Data is incomplete !");
            return ackResult;
        }
        log.info("there will be getting paymenStastus!");
        PaymentStatus paymentStatus = getPaymentStatus(transfer, ackResult, ack3Response.getAck3Header().getGroupStatus(), ack3Response.getAck3Details().getTransactionStatus(),ack3Response.getAck3Details().getAdditionalInformation());
        // 成功、失败、处理中 都需要更新数据库状态
        log.info("there will be updatting paymentStatus!");
        updatePaymentStatus(transfer, paymentStatus, ack3Response.getAck3Details().getTransactionStatus(),ack3Response.getAck3Details().getAdditionalInformation());
        return ackResult;
    }

    private static class AckResult {
        private boolean nextHandler = false;
        public boolean isNextHandler() {
            return nextHandler;
        }
        public void setNextHandler(boolean nextHandler) {
            this.nextHandler = nextHandler;
        }
    }

    private File getACK2(List<File> resultFiles) {
        return getACK(resultFiles, "ACK2");
    }

    private File getACK3(List<File> resultFiles) {
        return getACK(resultFiles, "ACK3");
    }

    private File getACK(List<File> resultFiles, String endWith) {
        for (File file  : resultFiles) {
            if(file.getName().toUpperCase().endsWith(endWith)) {
                return file;
            }
        }
        return null;
    }

    private PaymentStatus getPaymentStatus(T transfer, AckResult ackResult, String headerStatus, String transactionStatus, String additionalInformation) {
        PaymentStatus paymentStatus = null;
        if(StringUtils.isEmpty(headerStatus)) {
            paymentStatus = PaymentStatus.PROCESSING;
        } else {
            String headerStatusUpper = headerStatus.toUpperCase();
            if(headerStatusUpper.equals("ACTC") || headerStatusUpper.equals("ACCP")
                || headerStatusUpper.equals("ACWC") || headerStatusUpper.equals("PART")
                || headerStatusUpper.equals("ACSP")) {
            	log.info("paymentStatus:" + headerStatusUpper);
                paymentStatus = PaymentStatus.SUCCESS;
            } else if(headerStatusUpper.equals("RJCT")) {
            	log.info("paymentStatus:" + headerStatusUpper);
                paymentStatus = PaymentStatus.FAILED;
            } else {
                // 只有ACK3有该值
            	log.info("paymentStatus:" + headerStatusUpper);
                paymentStatus = PaymentStatus.PROCESSING;
            }
        }
        if(!StringUtils.isEmpty(transactionStatus)) {
            if(paymentStatus.equals(PaymentStatus.SUCCESS)) {
                String transactionStatusUpper = transactionStatus.toUpperCase();
                if(transactionStatusUpper.equals("ACCP") || transactionStatusUpper.equals("ACWC")) {
                	log.info("transactionStatusUpper:" + transactionStatusUpper);
                    paymentStatus = PaymentStatus.SUCCESS;
                } else if(transactionStatusUpper.equals("RJCT")) {
                	log.info("transactionStatusUpper:" + transactionStatusUpper);
                    paymentStatus = PaymentStatus.FAILED;
                } else {
                    // 只有ACK3有该值
                	log.info("transactionStatusUpper:" + transactionStatusUpper);
                    paymentStatus = PaymentStatus.PROCESSING;
                }
            }
        }

        if(paymentStatus.equals(PaymentStatus.FAILED)) {
        	log.info("zzzzzzzzzzzzzzzz");
            updatePaymentStatus(transfer, paymentStatus,"RJCT",additionalInformation);
            ackResult.setNextHandler(false);
        } else if(paymentStatus.equals(PaymentStatus.PROCESSING)) {
            ackResult.setNextHandler(false);
        } else if(paymentStatus.equals(PaymentStatus.SUCCESS)) {
            ackResult.setNextHandler(true);
        }
        return paymentStatus;
    }

    protected void setInterval(int interval) {
        this.interval = interval;
    }

}
