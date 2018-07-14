package com.hce.paymentgateway.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.api.dbs.ACK1Header;
import com.hce.paymentgateway.api.dbs.ACK1Response;
import com.hce.paymentgateway.api.dbs.ACK2Details;
import com.hce.paymentgateway.api.dbs.ACK2Header;
import com.hce.paymentgateway.api.dbs.ACK2Response;
import com.hce.paymentgateway.api.dbs.ACK3Details;
import com.hce.paymentgateway.api.dbs.ACK3Header;
import com.hce.paymentgateway.api.dbs.ACK3Response;
import com.hce.paymentgateway.api.dbs.Trailer;
import com.hce.paymentgateway.api.hce.PayRocketmqDto;
import com.hce.paymentgateway.controller.PayMqproducer;
import com.hce.paymentgateway.entity.BaseEntity;
import com.hce.paymentgateway.service.ResponseProcessService;
import com.hce.paymentgateway.util.CommonUtil;
import com.hce.paymentgateway.util.DBSDataFormat;
import com.hce.paymentgateway.util.FileNameGenerator;
import com.hce.paymentgateway.util.PaymentStatus;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author Heling.Yao
 * @Date 15:03 2018/6/1
 */
@Slf4j
public abstract class AbstractSchedulingService<T extends BaseEntity> {
    protected int interval = 1;

    @Autowired
	private PayMqproducer payMqproducer;
    @Resource(name = "SCPFileUtils")
    private com.hce.paymentgateway.util.SCPFileUtils SCPFileUtils;
    @Resource(name = "vaSetupResponseProcessServiceImpl")
    private ResponseProcessService vasetupResponseProcessService;
    @Resource(name = "vaReportResponseProcessServiceImpl")
    private ResponseProcessService vareportResponseProcessService;
    @Resource(name = "mt94xResponseProcessServiceImpl")
    private ResponseProcessService mt94xResponseProcessService;

    /**
     * 处理ACK1、ACK2、ACK3
     */
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

    @Scheduled(cron = "0 0/5 * * * ?")
    public void processResponse() throws JSchException, SftpException, IOException, InterruptedException {
    	List<File> files = SCPFileUtils.downloadFilesFromServerAndDecrypt(Constant.CUSTOMERID+"_DSG_VAHKL_RESP_*.xls");//海云汇香港VA Setup
    	vasetupResponseProcessService.process(files);
    	files = SCPFileUtils.downloadFilesFromServerAndDecrypt(Constant.PARENT+".HK_*_HKD_EPAYCOL.ENH.001.D*T*.csv");//海云汇香港VA Report (30-min interval)
    	vareportResponseProcessService.process(files);
    	files = SCPFileUtils.downloadFilesFromServerAndDecrypt(Constant.PARENT+".CBHK_MT942.D");//MT942
    	mt94xResponseProcessService.process(files);
    }

    @Scheduled(cron = "0 30 9 * * ?")
    public void processDaily() throws JSchException, SftpException, IOException, InterruptedException {
    	List<File> files = SCPFileUtils.downloadFilesFromServerAndDecrypt(Constant.PARENT+".VARPT.HK.*.TRAN.ENH.D*T*.csv");//海云汇香港VA Report (End-Of-Day)
    	vareportResponseProcessService.process(files);
    	files = SCPFileUtils.downloadFilesFromServerAndDecrypt(Constant.PARENT+".CBHK_MT940.D");//MT940
    	mt94xResponseProcessService.process(files);
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
            // 3. 文件格式转换
            AckResult ackResult = handleACK1(transfer, resultFiles);
            log.info(""+!ackResult.isNextHandler());
            //if(!ackResult.isNextHandler()) continue;
            ackResult = handleACK2(transfer, resultFiles);
            //if(!ackResult.isNextHandler()) continue;
            handleACK3(transfer, resultFiles);
        };
        return isContinue;
    }

    protected abstract List<T> queryProcessingData(int init, int interval);

    protected abstract boolean updateQueryCount(T transfer);

    protected abstract void updatePaymentStatus(T transfer, PaymentStatus paymentStatus, String errorCode, String errorMsg);
    
    protected abstract void updateFileName1(T transfer,String fileName ,String ackFileType);
    
    protected abstract void updatePaymentDateById(T transfer, String paymentDate);

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
    	log.info("handleACK1 begin");
        AckResult ackResult = new AckResult();
        File ack1File = getACK(resultFiles, "ACK1");
        if(ack1File == null) return ackResult;
        log.info(ack1File.getName());
        ACK1Response ack1Response = parseFile(ack1File, ACK1Header.class, null, ACK1Response.class);
        if(ack1Response == null || ack1Response.getAck1Header() == null ||
            StringUtils.isEmpty(ack1Response.getAck1Header().getGroupStatus())) {
            return ackResult;
        }
        PaymentStatus paymentStatus = getPaymentStatus(transfer, ackResult, ack1Response.getAck1Header().getGroupStatus(), null,ack1Response.getAck1Header().getAdditionalInformation());
        if(!paymentStatus.equals(PaymentStatus.FAILED)){
        	paymentStatus = PaymentStatus.PROCESSING;
        	sendMqMsg(transfer, ack1Response.getAck1Header().getAdditionalInformation(), ack1Response.getAck1Header().getGroupStatus(), 
        			(new SimpleDateFormat("yyyy-mm-dd")).format(Calendar.getInstance().getTime()), transfer.getPaymentId());
        }
        updatePaymentStatus(transfer, paymentStatus, ack1Response.getAck1Header().getGroupStatus(),ack1Response.getAck1Header().getAdditionalInformation());
        updateFileName1(transfer, ack1File.getName(),"ACK1");
        return ackResult;
    }

    private AckResult handleACK2(T transfer, List<File> resultFiles) throws Exception {
    	log.info("handleACK2 begin");
        AckResult ackResult = new AckResult();
        File ack2File = getACK(resultFiles, "ACK2");
        if(ack2File == null) return ackResult;
        log.info(ack2File.getName());
        ACK2Response ack2Response = parseFile(ack2File, ACK2Header.class, ACK2Details.class, ACK2Response.class);
        if(ack2Response == null || ack2Response.getAck2Header() == null
            || StringUtils.isEmpty(ack2Response.getAck2Header().getGroupStatus())
            || StringUtils.isEmpty(ack2Response.getAck2Details().getTransactionStatus())) {
            return ackResult;
        }
        PaymentStatus paymentStatus = getPaymentStatus(transfer, ackResult, ack2Response.getAck2Header().getGroupStatus(), ack2Response.getAck2Details().getTransactionStatus(),ack2Response.getAck2Details().getAdditionalInformation());
        if(!paymentStatus.equals(PaymentStatus.FAILED)){
        	paymentStatus = PaymentStatus.PROCESSING;
        	sendMqMsg(transfer, ack2Response.getAck2Details().getAdditionalInformation(), ack2Response.getAck2Details().getTransactionStatus(), 
            		ack2Response.getAck2Details().getPaymentDate(), ack2Response.getAck2Details().getCustomerReference());
        }
        updatePaymentStatus(transfer, paymentStatus, ack2Response.getAck2Details().getTransactionStatus(),ack2Response.getAck2Details().getAdditionalInformation());
        updateFileName1(transfer, ack2File.getName(),"ACK2");
        updatePaymentDateById(transfer,CommonUtil.getFormatDate(ack2Response.getAck2Details().getPaymentDate(),"yyyyMMdd","yyyy-MM-dd"));
        return ackResult;
    }

    private AckResult handleACK3(T transfer, List<File> resultFiles) throws Exception {
    	log.info("handleACK3 begin");
        AckResult ackResult = new AckResult();
        File ack3File = getACK(resultFiles, "ACK3");
        if(ack3File == null) return ackResult;
        log.info(ack3File.getName());
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
        updateFileName1(transfer, ack3File.getName(),"ACK3");
        
        sendMqMsg(transfer, ack3Response.getAck3Details().getAdditionalInformation(), ack3Response.getAck3Details().getTransactionStatus(), 
        		ack3Response.getAck3Details().getPaymentDate(), ack3Response.getAck3Details().getCustomerReference());
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
        	log.info("ACK call failed!");
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
    
    private void sendMqMsg(T transfer,String additionalInformation,String transactionStatus,String paymentDate,String customerReference){
    	PayRocketmqDto payRocketmqDto = new PayRocketmqDto();
        payRocketmqDto.getBody().setAdditionalInformation(additionalInformation);//附加信息
        payRocketmqDto.getBody().setCorp(transfer.getCorp());//实体代码-法人代码
        payRocketmqDto.getBody().setStatus(transfer.getStatus());//处理状态
        payRocketmqDto.getBody().setTransactionStatus(transactionStatus);//文件处理状态
        
        payRocketmqDto.getHeader().setBIZBRCH("0101");
        payRocketmqDto.getHeader().setFRTSIDEDT(paymentDate);//前台日期-付款日期
        payRocketmqDto.getHeader().setFRTSIDESN(customerReference);//前台流水-支付号
        payRocketmqDto.getHeader().setLGRPCD(transfer.getCorp());//法人代码
        payRocketmqDto.getHeader().setTLCD("DBS002");//柜员号
        payRocketmqDto.getHeader().setTRDCD("35033");
        payRocketmqDto.getHeader().setTRDDT(paymentDate);//付款日期
        
        
        String msgInfo = JSON.toJSONString(payRocketmqDto);
        payMqproducer.sendMsg("35033", msgInfo);
        log.info("send msg \"35033\" to hyh finish");
    }

}
