package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.dao.DBSMT940Dao;
import com.hce.paymentgateway.dao.DBSMT942Dao;
import com.hce.paymentgateway.dao.DBSMT94XDetailDao;
import com.hce.paymentgateway.dao.DBSMT94XHeaderDao;
import com.hce.paymentgateway.entity.DBSMT940Entity;
import com.hce.paymentgateway.entity.DBSMT942Entity;
import com.hce.paymentgateway.entity.DBSMT94XDetailEntity;
import com.hce.paymentgateway.entity.DBSMT94XHeaderEntity;
import com.hce.paymentgateway.entity.vo.DBSMT94XVO;
import com.prowidesoftware.swift.io.ConversionService;
import com.prowidesoftware.swift.io.IConversionService;
import com.prowidesoftware.swift.model.SwiftBlock1;
import com.prowidesoftware.swift.model.SwiftBlock2;
import com.prowidesoftware.swift.model.SwiftBlock4;
import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.field.Field20C;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("mt94xResponseProcessServiceImpl")
public class MT94XResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Autowired
	private DBSMT94XHeaderDao dbsMT94XHeaderDao;
	@Autowired
	private DBSMT940Dao dbsMT940Dao;
	@Autowired
	private DBSMT942Dao dbsMT942Dao;
	@Autowired
	private DBSMT94XDetailDao dbsMT94XDetailDao;

	private static final ThreadLocal<String> tagHolder = new ThreadLocal<String>();
	private static final ThreadLocal<String> corpHolder = new ThreadLocal<String>();

	private final String[] MT940_TAG_VALS_60 = {"60F", "60M"};

	@Override
	protected Object process(File file) throws IOException {
		String customerId = file.getName().substring(0, file.getName().indexOf("."));
		String corp = Constant.subsidiaryMap.get(customerId);
		InputStream in = null;
		byte[] buf = null;
		try {
			in = new FileInputStream(file);
			buf = new byte[in.available()];
			in.read(buf);
		} finally {
			if(in!=null)
				in.close();
		}
		IConversionService srv = new ConversionService();
		SwiftMessage msg = srv.getMessageFromFIN(new String(buf));
		SwiftBlock1 block1 = msg.getBlock1();
		SwiftBlock2 block2 = msg.getBlock2();
		DBSMT94XHeaderEntity mt94x = new DBSMT94XHeaderEntity();
		mt94x.setFileIn(file.getName());
		mt94x.setCustomerId(customerId);
		mt94x.setCorp(corp);
		mt94x.setApplicationId(block1.getApplicationId());
		mt94x.setServiceId(block1.getServiceId());
		mt94x.setSender(msg.getSender());
		mt94x.setSessionNumber(block1.getSessionNumber());
		mt94x.setSequenceNumber(block1.getSequenceNumber());
		mt94x.setMessageType(msg.getType());
		String s = block2.getValue();
		mt94x.setReceiver(s.substring(4, s.length()-1));
		mt94x.setMessagePriority(s.substring(s.length()-1, s.length()));
		SwiftBlock4 block4 = msg.getBlock4();
		String[] tagValues = block4.getTagValue("25").split("/");
		if(tagValues.length>1) {
			mt94x.setSubsidiarySwiftBic(tagValues[0]);
			mt94x.setAccountNumber(tagValues[1]);
		} else if(tagValues.length==1) {
			mt94x.setAccountNumber(tagValues[0]);
		}
		corpHolder.set(mt94x.getSubsidiarySwiftBic());
		tagValues = block4.getTagValue("28C").split("/");
		mt94x.setStatementNumber(tagValues[0]);
		mt94x.setDbsSequenceNumber(tagValues[1]);
		dbsMT94XHeaderDao.save(mt94x);
		String[] tagValues61 = block4.getTagValues("61");
		String[] tagValues86 = block4.getTagValues("86");
		if(msg.getTypeInt()==940) {
			tagHolder.set("35041");
			DBSMT940Entity mt940 = new DBSMT940Entity();
			mt940.setHeaderId(mt94x.getId());
			//首次期初余额/中间期初余额
			s = null;
			for(String tagVal:MT940_TAG_VALS_60) {
				s = block4.getTagValue(tagVal);
				if(s!=null) {
					s = s.trim();
					if(s.length()>0) {
						break;
					}
				}
			}
			char[] chars = s.toCharArray();
			mt940.setFirstOpeningBalanceIndicator(String.valueOf(chars[0]));
			mt940.setFirstOpeningBalanceDate(String.valueOf(chars, 1, 6));
			mt940.setFirstOpeningBalanceCurrency(String.valueOf(chars, 7, 3));
			mt940.setFirstOpeningBalanceAmount(new BigDecimal(String.valueOf(chars, 10, chars.length-10).replaceAll(",", ".")));
			//期末可用余额
			s = block4.getTagValue("64");
			if(s!=null) {
				s = s.trim();
				if(s.length()>0) {
					chars = s.toCharArray();
					mt940.setClosingAvailableBalanceIndicator(String.valueOf(chars[0]));
					mt940.setClosingAvailableBalanceDate(String.valueOf(chars, 1, 6));
					mt940.setClosingAvailableBalanceCurrency(String.valueOf(chars, 7, 3));
					mt940.setClosingAvailableBalanceAmount(new BigDecimal(String.valueOf(chars, 10, chars.length-10).replaceAll(",", ".")));
				}
			}
			dbsMT940Dao.save(mt940);
		} else if(msg.getTypeInt()==942) {
			tagHolder.set("35042");
			DBSMT942Entity mt942 = new DBSMT942Entity();
			mt942.setHeaderId(mt94x.getId());
			char[] chars = block4.getTagValue("34F").toCharArray();
			mt942.setFloorLimitIndicatorCurrency(String.valueOf(chars, 0, 3));
			log.info(mt942.getFloorLimitIndicatorCurrency());
			mt942.setFloorLimitIndicatorAmount(new BigDecimal(String.valueOf(chars, 3, chars.length-3).replaceAll(",", ".")));
			chars = block4.getTagValue("13D").toCharArray();
			mt942.setDateTimeIndicationDate(String.valueOf(chars, 0, 10));
			mt942.setDateTimeIndicationSign(String.valueOf(chars[10]));
			mt942.setDateTimeIndicationTimeZone(String.valueOf(chars, 11, 4));
			s = block4.getTagValue("90D");
			if(s!=null) {
				s = s.trim();
				if(s.length()>0) {
					chars = s.toCharArray();
					int lastDigitIndex = getLastDigitIndex(chars, 0);
					mt942.setNumberAndSumOfDebitEntriesNumber(new BigDecimal(String.valueOf(chars, 0, lastDigitIndex+1).replaceAll(",", ".")));
					mt942.setNumberAndSumOfDebitEntriesCurrency(String.valueOf(chars, lastDigitIndex+1, 3));
					mt942.setNumberAndSumOfDebitEntriesAmount(new BigDecimal(String.valueOf(chars, lastDigitIndex+4, chars.length-lastDigitIndex-4).replaceAll(",", ".")));
				}
			}
			s = block4.getTagValue("90C");
			if(s!=null) {
				s = s.trim();
				if(s.length()>0) {
					chars = s.toCharArray();
					int lastDigitIndex = getLastDigitIndex(chars, 0);
					mt942.setNumberAndSumOfCreditEntriesNumber(new BigDecimal(String.valueOf(chars, 0, lastDigitIndex+1).replaceAll(",", ".")));
					mt942.setNumberAndSumOfCreditEntriesCurrency(String.valueOf(chars, lastDigitIndex+1, 3));
					mt942.setNumberAndSumOfCreditEntriesAmount(new BigDecimal(String.valueOf(chars, lastDigitIndex+4, chars.length-lastDigitIndex-4).replaceAll(",", ".")));
				}
			}
			if(tagValues86!=null&&((tagValues61==null&&tagValues86.length>0)||(tagValues61!=null&&tagValues86.length>tagValues61.length))) {
				s = tagValues86[tagValues86.length-1].replaceAll("\r\n", "").replaceAll("\\s+", "");
				chars = s.toCharArray();
				int fieldFlagIndex = s.indexOf("OPBL");
				int lastDigitIndex = getLastDigitIndex(chars, fieldFlagIndex+8);
				mt942.setInformationToTheAccountOwnerOpeningBalanceIndicator(String.valueOf(chars, fieldFlagIndex+4, 1));
				mt942.setInformationToTheAccountOwnerOpeningBalanceCurrency(String.valueOf(chars, fieldFlagIndex+5, 3));
				mt942.setInformationToTheAccountOwnerOpeningBalanceAmount(new BigDecimal(String.valueOf(chars, fieldFlagIndex+8, lastDigitIndex-fieldFlagIndex-7).replaceAll(",", ".")));
				fieldFlagIndex = s.indexOf("CLBL");
				lastDigitIndex = getLastDigitIndex(chars, fieldFlagIndex+8);
				mt942.setInformationToTheAccountOwnerClosingBalanceIndicator(String.valueOf(chars, fieldFlagIndex+4, 1));
				mt942.setInformationToTheAccountOwnerClosingBalanceCurrency(String.valueOf(chars, fieldFlagIndex+5, 3));
				mt942.setInformationToTheAccountOwnerClosingBalanceAmount(new BigDecimal(String.valueOf(chars, fieldFlagIndex+8, lastDigitIndex-fieldFlagIndex-7).replaceAll(",", ".")));
				fieldFlagIndex = s.indexOf("CLAB");
				lastDigitIndex = getLastDigitIndex(chars, fieldFlagIndex+8);
				mt942.setInformationToTheAccountOwnerClosingAvailableIndicator(String.valueOf(chars, fieldFlagIndex+4, 1));
				mt942.setInformationToTheAccountOwnerClosingAvailableCurrency(String.valueOf(chars, fieldFlagIndex+5, 3));
				mt942.setInformationToTheAccountOwnerClosingAvailableAmount(new BigDecimal(String.valueOf(chars, fieldFlagIndex+8, lastDigitIndex-fieldFlagIndex-7).replaceAll(",", ".")));
			}
			dbsMT942Dao.save(mt942);
		}
		if(tagValues61!=null&&tagValues61.length>0) {
			List<DBSMT94XVO> list = new ArrayList<DBSMT94XVO>();
			for(int i=0;i<tagValues61.length;i++) {
				String tagVal61 = tagValues61[i];
				Field20C f20C = new Field20C(tagVal61);
				String component1 = f20C.getComponent1().replaceAll("\r\n", "");
				char[] chars = component1.toCharArray();
				int lastDigitIndex = getLastDigitIndex(chars, 12);
				DBSMT94XDetailEntity mt94xDetail = new DBSMT94XDetailEntity();
				mt94xDetail.setHeaderId(mt94x.getId());
				mt94xDetail.setValueDate(String.valueOf(chars, 0, 6));
				mt94xDetail.setEntryDate(String.valueOf(chars, 6, 4));
				mt94xDetail.setDebitCreditIndicator(String.valueOf(chars, 10, 1));
				mt94xDetail.setFundCode(String.valueOf(chars, 11, 1));
				mt94xDetail.setAmount(new BigDecimal(String.valueOf(chars, 12, lastDigitIndex-11).replaceAll(",", ".")));
				mt94xDetail.setTransactionTypeIdentificationCode(String.valueOf(chars, lastDigitIndex+2, 3));
				mt94xDetail.setReferenceToTheAccountOwner(String.valueOf(chars, lastDigitIndex+5, chars.length-lastDigitIndex-5));
				String[] component2 = f20C.getComponent2().replaceAll("\r\n", "").replaceAll("\\s+", "").split("\\?");
				mt94xDetail.setAccountServicingInstitutionsReference(component2[0]);
				mt94xDetail.setTradeTime(component2[1]);
				if(component2.length>2)
					mt94xDetail.setVaNumber(component2[2]);
				String[] tagVal86 = tagValues86[i].replaceAll("\r\n", "").split("\\?");
				Map<String, String> map = new HashMap<String, String>((tagVal86.length/2)+1);
				for(int j=0;j<tagVal86.length/2;j++) {
					int index = j*2;
					map.put(tagVal86[index], tagVal86[index+1]);
				}
				s = map.get("RA");
				if(s!=null) {
					s = s.trim();
					if(s.length()>0) {
						chars = map.get("RA").toCharArray();
						mt94xDetail.setRemittanceCurrency(String.valueOf(chars, 0, 3));
						mt94xDetail.setRemittanceAmount(new BigDecimal(String.valueOf(chars, 3, chars.length-3).replaceAll(",", ".")));
					}
				}
				mt94xDetail.setBeneficiaryName(map.get("BENM"));
				mt94xDetail.setBeneficiaryBankName(map.get("BB"));
				mt94xDetail.setBeneficiaryAccountNumber(map.get("BA"));
				mt94xDetail.setPayerName(map.get("ORDP"));
				mt94xDetail.setPayerBankName(map.get("OB"));
				mt94xDetail.setTransactionDescription(map.get("TD"));
				mt94xDetail.setPaymentDetails(map.get("REMI"));
				dbsMT94XDetailDao.save(mt94xDetail);
				DBSMT94XVO vo = new DBSMT94XVO();
				vo.setFileNm(mt94x.getFileIn());
				vo.setTrdDt(mt94xDetail.getValueDate());
				//Franco Chan说: 用交易日，銀行參考加3位交易代碼可以作唯一
				vo.setTlSnCd(mt94xDetail.getTransactionTypeIdentificationCode()+"-"+mt94xDetail.getAccountServicingInstitutionsReference()+"-"+mt94xDetail.getValueDate());
				vo.setBrrlndFlg(mt94xDetail.getDebitCreditIndicator());
				vo.setTrdCurr1(mt94xDetail.getRemittanceCurrency());
				vo.setTrdAmt(mt94xDetail.getRemittanceAmount().toString());
				String srcAccount = null;
				String srcName = null;
				String dstAccount = null;
				String dstName = null;
				if("C".equals(mt94xDetail.getDebitCreditIndicator())) {
					srcAccount = mt94xDetail.getBeneficiaryAccountNumber();
					srcName = mt94xDetail.getBeneficiaryName();
					dstAccount = "xxx";
					dstName = mt94xDetail.getPayerName();
				} else if("D".equals(mt94xDetail.getDebitCreditIndicator())) {
					srcAccount = mt94x.getAccountNumber();
					srcName = mt94xDetail.getPayerName();
					dstAccount = mt94xDetail.getBeneficiaryAccountNumber();
					dstName = mt94xDetail.getBeneficiaryName();
				}
				vo.setCustAcctno(srcAccount);
				vo.setAcctNm(srcName);
				vo.setOtherAcctno1(dstAccount);
				vo.setOtherAcctnm1(dstName);
				list.add(vo);
			}
			return list;
		}
		return null;
	}

	@Override
	public String getMsgTag() {
		String tag = tagHolder.get();
		tagHolder.remove();
		return tag;
	}

	@Override
	protected String getCorp() {
		String corp = Constant.subsidiaryMap.get(corpHolder.get());
		corpHolder.remove();
		return corp;
	}

	private final static int DIGIT_ASCII_RANGE_LOWER = 48;
	private final static int DIGIT_ASCII_RANGE_UPPER = 57;
	private final static int COMMA_SYMBOL_ASCII = 44;

	private int getLastDigitIndex(char[] chars, int startIndex) {
		for(int i=startIndex;i<chars.length;i++) {
			byte b = (byte)chars[i];
			if((b<DIGIT_ASCII_RANGE_LOWER||b>DIGIT_ASCII_RANGE_UPPER)&&b!=COMMA_SYMBOL_ASCII) {
				return i-1;
			}
		}
		return chars.length-1;
	}
}