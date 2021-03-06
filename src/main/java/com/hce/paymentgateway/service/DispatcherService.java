package com.hce.paymentgateway.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.api.hce.AccountTransferRequest;
import com.hce.paymentgateway.api.hce.PayRocketmqDto;
import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.api.hce.TradeResponse;
import com.hce.paymentgateway.controller.PayMqproducer;
import com.hce.paymentgateway.dao.AccountInfoDao;
import com.hce.paymentgateway.dao.DBSVASetupDao;
import com.hce.paymentgateway.entity.AccountInfoEntity;
import com.hce.paymentgateway.entity.DBSVASetupEntity;
import com.hce.paymentgateway.util.CommonUtil;
import com.hce.paymentgateway.util.JsonUtil;
import com.hce.paymentgateway.util.ResponseCode;
import com.hce.paymentgateway.util.SCPFileUtils;
import com.hce.paymentgateway.util.ServiceWrapper;
import com.hce.paymentgateway.validate.ConditionalMandatory;
import com.hce.paymentgateway.validate.ValidatorResult;
import com.hce.paymentgateway.validate.ValidatorUtil;
import com.hce.paymentgateway.vo.HCEDBSVASetupVO;
import com.hce.paymentgateway.vo.HCEHeader;
import com.hce.paymentgateway.vo.HCEMessageWrapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.NoSuchProviderException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Heling.Yao
 * @Date 11:42 2018/5/25
 */
@Slf4j
@Service
public class DispatcherService {
	@Autowired
    private ScanService scanService;
    @Autowired
    private AccountInfoDao accountInfoDao;

    public TradeResponse dispatcher(String json) {
        log.info("\n[网关支付]接收到交易请求 \n{}", json);
        TradeResponse response = new TradeResponse();
        // 付款类型校验
        TradeRequest tradeRequest = JsonUtil.parseObject(json, TradeRequest.class);
        ServiceWrapper serviceWrapper = scanService.getTransactionService(tradeRequest.getProductType());
        if(serviceWrapper == null) {
            response.setCode(ResponseCode.FAILED.name());
            response.setMessage("付款类型参数异常");
            return response;
        }
        // 静态参数格式校验
        TradeRequest actualRequest = JsonUtil.parseObject(json, serviceWrapper.getParameterType());
        ValidatorResult validatorResult = ValidatorUtil.validate(actualRequest);
        if(!validatorResult.isAvailable()) {
            addError(validatorResult, response);
            AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
            BeanUtils.copyProperties(actualRequest, accountTransferRequest);
            sendMqMsg(actualRequest, "文件格式错误", "RJCT", accountTransferRequest.getPaymentDate(), CommonUtil.getNumberForPK());
            return response;
        }
        // 动态数据校验
        validatorResult = dynamicValidate(actualRequest);
        if(!validatorResult.isAvailable()) {
            addError(validatorResult, response);
            AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
            BeanUtils.copyProperties(actualRequest, accountTransferRequest);
            sendMqMsg(actualRequest, "文件格式错误", "RJCT", accountTransferRequest.getPaymentDate(), CommonUtil.getNumberForPK());
            return response;
        }
        // 处理请求
        TransactionService transactionService = serviceWrapper.getTransactionService();
        try {
            response = transactionService.handle(actualRequest);
        } catch (Exception e) {
            response.setCode(ResponseCode.FAILED.name());
            log.error("[网关支付]失败, 异常信息如下\r\n"+json+"\r\n", e);
            AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
            BeanUtils.copyProperties(actualRequest, accountTransferRequest);
            sendMqMsg(actualRequest, "文件格式错误", "RJCT", accountTransferRequest.getPaymentDate(), CommonUtil.getNumberForPK());
        }
        return response;
    }

    private void addError(ValidatorResult validatorResult, TradeResponse response) {
        log.error("\n[网关支付]参数校验失败, {}", validatorResult.getMessage());
        response.setCode(ResponseCode.FAILED.name());
        response.setMessage(validatorResult.getMessage());
    }

    /**
     * 动态数据校验
     */
    private ValidatorResult dynamicValidate(TradeRequest actualRequest) {
        ValidatorResult result = checkCorp(actualRequest);
        if(!result.isAvailable()) {
            return result;
        }
        result = checkConditionalMandatory(actualRequest);
        if(!result.isAvailable()) {
            return result;
        }
        return result;
    }

    /**
     * 通过付款机构ID(Corp)查找数据库信息
     */
    private ValidatorResult checkCorp(TradeRequest actualRequest) {
        ValidatorResult result = new ValidatorResult();
        List<AccountInfoEntity> accountInfoList = accountInfoDao.findByCorpAndEnabled(actualRequest.getCorp(), true);
        if(accountInfoList.isEmpty()) {
            result.setAvailable(false);
            result.setMessage("付款机构ID不合法");
            return result;
        }
        return result;
    }

    private ValidatorResult checkConditionalMandatory(TradeRequest actualRequest) {
        ValidatorResult result = new ValidatorResult();
        try {
            Field[] fields = actualRequest.getClass().getDeclaredFields();
            for (Field field : fields) {
                ConditionalMandatory cm = field.getAnnotation(ConditionalMandatory.class);
                if(cm != null) {
                    String asf = cm.associatedField();
                    Field asField = actualRequest.getClass().getDeclaredField(asf);
                    asField.setAccessible(true);
                    Object asfValue = asField.get(actualRequest);

                    field.setAccessible(true);
                    Object currentValue = field.get(actualRequest);
                    // 当前字段的关联字段不为空
                    if(asfValue != null) {
                        String associatedConditionValue = cm.associatedConditionValue();
                        // 没有限制条件 下 当前字段为空
                        if(StringUtils.isBlank(associatedConditionValue) && currentValue == null) {
                            result.setAvailable(false);
                            result.setMessage(asField.getName() + "不为空, " + field.getName() + "为空");
                            return result;
                        }
                        // 有限制条件下
                        if(StringUtils.isNotBlank(associatedConditionValue)) {
                            String asfValueString = (String) asfValue;
                            // 符合限制条件 下 当前字段为空
                            if(asfValueString.equals(associatedConditionValue) && currentValue == null) {
                                result.setAvailable(false);
                                result.setMessage(asField.getName() + "值为" + asfValueString + ", " + field.getName() + "为空");
                                return result;
                            }
                        }
                    } else if (currentValue != null) {
                        result.setAvailable(false);
                        result.setMessage(asField.getName() + "值为空, " + field.getName() + "不应该有值");
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.error("参数校验异常", e);
        }
        return result;
    }

    @Autowired
    private SCPFileUtils SCPFileUtils;
    @Autowired
    private DBSVASetupDao dbsVASetupDao;
    @Autowired
	private PayMqproducer payMqproducer;
    @Resource(name = "HCEVASetupResponseProcessServiceImpl")
    private ResponseProcessService vasetupResponseProcessService;

    @Transactional
    public void processVASetup(String json) throws IOException, NoSuchProviderException, JSchException, SftpException, PGPException {
    	String fileName = "DSG_VAHKL."+UUID.randomUUID().toString().replace("-", "").toUpperCase()+".csv";
    	String path = SCPFileUtils.getTempFileDir();
    	path += fileName;//明文csv位置
    	File file = new File(path);
    	if(file.exists()) {
    		file.delete();
    	}
    	JSONObject jsonObj = JSONObject.parseObject(json, JSONObject.class);
    	List<JSONObject> vasetups = (List<JSONObject>)((JSONObject)jsonObj.get("body")).get("f350391");
    	List<HCEDBSVASetupVO> errorList = new ArrayList<HCEDBSVASetupVO>(vasetups.size());
    	CsvWriter csvWriter = null;
    	log.info("\r\nVA_SETUP_PROCESS: "+fileName);
    	try {
    		csvWriter = new CsvWriter(path);
        	for(JSONObject obj:vasetups) {
        		DBSVASetupEntity vasetup = new DBSVASetupEntity();
            	try {
            		vasetup.setRequestFile(fileName);
            		vasetup.setCorp(obj.getString("corp"));
            		vasetup.setAction(obj.getString("action"));
            		vasetup.setCorpCode(obj.getString("corpCode"));
            		vasetup.setRemitterPayerName(obj.getString("remitterPayerName"));
            		vasetup.setMasterAC(obj.getString("masterAC"));
            		vasetup.setStaticVASequenceNumber(obj.getString("staticVASequenceNumber"));
            		vasetup.setStatus(Constant.VA_SETUP_STATUS_DEFAULT);
            		dbsVASetupDao.save(vasetup);
            		String[] row = {vasetup.getAction(), vasetup.getStaticVASequenceNumber(), vasetup.getCorpCode(), vasetup.getMasterAC(), vasetup.getRemitterPayerName()};
            		csvWriter.writeRecord(row);
            	} catch(Exception e) {
            		HCEDBSVASetupVO vo = new HCEDBSVASetupVO();
            		vo.setMasterAC(vasetup.getMasterAC());
            		vo.setCorp(vasetup.getCorp());
            		vo.setStatus(Constant.VA_SETUP_STATUS_EXCEPTION);
            		vo.setFailureReason(e.getMessage());
            		errorList.add(vo);
            		log.error("", e);
            		break;
            	}
        	}
        	if(errorList==null||errorList.size()==0) {//成功
        		csvWriter.close();
            	SCPFileUtils.uploadFileFromServer(fileName+".pgp", null, path);
        	} else {//异常
        		DateFormat df = new SimpleDateFormat("yyyyMMdd");
        		String today = df.format(System.currentTimeMillis());
            	HCEHeader header = vasetupResponseProcessService.getHeader(today);
            	Map<String, Object> body = new HashMap<String, Object>(1);
    			body.put("f"+vasetupResponseProcessService.getMsgTag()+"1", errorList);
            	HCEMessageWrapper msg = new HCEMessageWrapper(header, body);
    			String rsJson = JSONObject.toJSONString(msg);
            	payMqproducer.sendMsg(Constant.MQ_NAME_OUT_HCE, vasetupResponseProcessService.getMsgTag(), rsJson);
        	}
    	} finally {
    		if(csvWriter!=null)
    			csvWriter.close();
    	}
    }
    
    private void sendMqMsg(TradeRequest transfer,String additionalInformation,String transactionStatus,String paymentDate,String customerReference){
    	PayRocketmqDto payRocketmqDto = new PayRocketmqDto();
    	if(additionalInformation!=null){
    		payRocketmqDto.getBody().setAdditionalInformation(additionalInformation);//附加信息
    	}
    	payRocketmqDto.getBody().setTransId(transfer.getTransId());
        payRocketmqDto.getBody().setCorp(transfer.getCorp());//实体代码-法人代码
        payRocketmqDto.getBody().setStatus(-1);//处理状态
        payRocketmqDto.getBody().setTransactionStatus(transactionStatus);//文件处理状态
        
        payRocketmqDto.getHead().setBIZBRCH("0101");
        payRocketmqDto.getHead().setFRTSIDEDT(paymentDate);//前台日期-付款日期
        payRocketmqDto.getHead().setFRTSIDESN(customerReference);//前台流水-支付号
        payRocketmqDto.getHead().setLGRPCD(transfer.getCorp());//法人代码
        payRocketmqDto.getHead().setTLCD("DBS002");//柜员号
        payRocketmqDto.getHead().setTRDCD("35033");
        payRocketmqDto.getHead().setTRDDT(paymentDate);//付款日期
        payRocketmqDto.getHead().setCHNL("00");
        
        
        String msgInfo = JSON.toJSONString(payRocketmqDto);
        log.info("will be sending");
        payMqproducer.sendMsg(Constant.MQ_NAME_OUT_HCE, "35033", msgInfo);
        log.info("send msg \"35033\" to hyh finish");
    }
}