package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.dao.DBSMT940Dao;
import com.hce.paymentgateway.dao.DBSMT942Dao;
import com.hce.paymentgateway.dao.DBSMT94XDetailDao;
import com.hce.paymentgateway.dao.DBSMT94XDetailInformationDao;
import com.hce.paymentgateway.dao.DBSMT94XHeaderDao;
import com.hce.paymentgateway.entity.DBSMT940Entity;
import com.hce.paymentgateway.entity.DBSMT942Entity;
import com.hce.paymentgateway.entity.DBSMT94XDetailEntity;
import com.hce.paymentgateway.entity.DBSMT94XHeaderEntity;
import com.hce.paymentgateway.entity.DBSMT94XInformationEntity;
import com.prowidesoftware.swift.io.ConversionService;
import com.prowidesoftware.swift.io.IConversionService;
import com.prowidesoftware.swift.model.SwiftBlock1;
import com.prowidesoftware.swift.model.SwiftBlock2;
import com.prowidesoftware.swift.model.SwiftBlock4;
import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.field.Field20C;

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
	@Autowired
	private DBSMT94XDetailInformationDao dbsMT94XDetailInformationDao;

	@Override
	protected void process(File file) throws IOException {
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
		mt94x.setApplicationId(block1.getApplicationId());
		mt94x.setServiceId(block1.getServiceId());
		mt94x.setSender(msg.getSender());
		mt94x.setSessionNumber(block1.getSessionNumber());
		mt94x.setSequenceNumber(block1.getSequenceNumber());
		mt94x.setMessageType(msg.getType());
		mt94x.setReceiver(msg.getReceiver());
		mt94x.setMessagePriority(block2.getMessagePriority());
		SwiftBlock4 block4 = msg.getBlock4();
		String[] tagValues86 = null;
		String[] tagValues = block4.getTagValue("25").split("/");
		mt94x.setAccountNumber(tagValues[1]);
		mt94x.setSubsidiarySwiftBic(tagValues[0]);
		tagValues = block4.getTagValue("28C").split("/");
		mt94x.setStatementNumber(tagValues[0]);
		mt94x.setDbsSequenceNumber(tagValues[1]);
		dbsMT94XHeaderDao.save(mt94x);
		if(msg.getTypeInt()==940) {
			DBSMT940Entity mt940 = new DBSMT940Entity();
			mt940.setHeaderId(mt94x.getId());
			//首次期初余额/中间期初余额
			char[] chars = block4.getTagValue("60F").toCharArray();
			mt940.setFirstOpeningBalanceIndicator(String.valueOf(chars[0]));
			mt940.setFirstOpeningBalanceDate(String.valueOf(chars, 1, 6));
			mt940.setFirstOpeningBalanceCurrency(String.valueOf(chars, 7, 3));
			mt940.setFirstOpeningBalanceAmount(new BigDecimal(String.valueOf(chars, 10, chars.length-10).replaceAll(",", ".")));
			//期末可用余额
			chars = block4.getTagValue("64").toCharArray();
			mt940.setClosingAvailableBalanceIndicator(String.valueOf(chars[0]));
			mt940.setClosingAvailableBalanceDate(String.valueOf(chars, 1, 6));
			mt940.setClosingAvailableBalanceCurrency(String.valueOf(chars, 7, 3));
			mt940.setClosingAvailableBalanceAmount(new BigDecimal(String.valueOf(chars, 10, chars.length-10).replaceAll(",", ".")));
			dbsMT940Dao.save(mt940);
		} else if(msg.getTypeInt()==942) {
			DBSMT942Entity mt942 = new DBSMT942Entity();
			mt942.setHeaderId(mt94x.getId());
			char[] chars = block4.getTagValue("34F").toCharArray();
			mt942.setFloorLimitIndicatorCurrency(String.valueOf(chars, 0, 3));
			mt942.setFloorLimitIndicatorAmount(new BigDecimal(String.valueOf(chars, 3, chars.length-3).replaceAll(",", ".")));
			chars = block4.getTagValue("13D").toCharArray();
			mt942.setDateTimeIndicationDate(String.valueOf(chars, 0, 10));
			mt942.setDateTimeIndicationSign(String.valueOf(chars[10]));
			mt942.setDateTimeIndicationTimeZone(String.valueOf(chars, 11, 4));
			chars = block4.getTagValue("90D").toCharArray();
			int lastDigitIndex = getLastDigitIndex(chars, 0);
			mt942.setNumberAndSumOfDebitEntriesNumber(new BigDecimal(String.valueOf(chars, 0, lastDigitIndex+1).replaceAll(",", ".")));
			mt942.setNumberAndSumOfDebitEntriesCurrency(String.valueOf(chars, lastDigitIndex+1, 3));
			mt942.setNumberAndSumOfDebitEntriesAmount(new BigDecimal(String.valueOf(chars, lastDigitIndex+4, chars.length-lastDigitIndex-4).replaceAll(",", ".")));
			chars = block4.getTagValue("90C").toCharArray();
			lastDigitIndex = getLastDigitIndex(chars, 0);
			mt942.setNumberAndSumOfCreditEntriesNumber(new BigDecimal(String.valueOf(chars, 0, lastDigitIndex+1).replaceAll(",", ".")));
			mt942.setNumberAndSumOfCreditEntriesCurrency(String.valueOf(chars, lastDigitIndex+1, 3));
			mt942.setNumberAndSumOfCreditEntriesAmount(new BigDecimal(String.valueOf(chars, lastDigitIndex+4, chars.length-lastDigitIndex-4).replaceAll(",", ".")));
			tagValues86 = block4.getTagValues("86");
			String s = tagValues86[tagValues86.length-1].replaceAll("\r\n", "").replaceAll("\\s+", "");
			chars = s.toCharArray();
			int fieldFlagIndex = s.indexOf("OPBL");
			lastDigitIndex = getLastDigitIndex(chars, fieldFlagIndex+8);
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
			dbsMT942Dao.save(mt942);
		}
		String[] tagValues61 = block4.getTagValues("61");
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
			mt94xDetail.setAmount(new BigDecimal(String.valueOf(chars, 12, lastDigitIndex-11)));
			mt94xDetail.setTransactionTypeIdentificationCode(String.valueOf(chars, lastDigitIndex+2, 3));
			mt94xDetail.setReferenceToTheAccountOwner(String.valueOf(chars, lastDigitIndex+5, chars.length-lastDigitIndex-5));
			String[] component2 = f20C.getComponent2().replaceAll("\r\n", "").replaceAll("\\s+", "").split("\\?");
			mt94xDetail.setAccountServicingInstitutionsReference(component2[0]);
			mt94xDetail.setTradeTime(component2[1]);
			if(component2.length>2)
				mt94xDetail.setVaNumber(component2[2]);
			dbsMT94XDetailDao.save(mt94xDetail);
			
			String[] tagVal86 = tagValues86[i].replaceAll("\r\n", "").split("\\?");
			for(int j=0;j<tagVal86.length/2;j++) {
				int index = j*2;
				DBSMT94XInformationEntity dbsMT94XInformation = new DBSMT94XInformationEntity();
				dbsMT94XInformation.setDetailId(mt94xDetail.getId());
				dbsMT94XInformation.setKey(tagVal86[index]);
				dbsMT94XInformation.setValue(tagVal86[index]+1);
				dbsMT94XDetailInformationDao.save(dbsMT94XInformation);
			}
		}
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

	public static void main(String[] args) throws IOException {
//		System.out.println(",".getBytes("UTF-8")[0]);
//		String s = "C180108HKD148,20";
//		String s = "HKD0,";
//		String s = "1609211600+0800";
//		String s = "9HKD11502,5";
		/*String s = "4,7HKD0,278";
		char[] chars = s.toCharArray();
		int lastDigitIndex = getLastDigitIndex(chars, 0);
		System.out.println(String.valueOf(chars, 0, lastDigitIndex+1)+"---"+String.valueOf(chars, lastDigitIndex+4, chars.length-lastDigitIndex-4));
*/
		/*String s = "OPBL CHKD5965,70 CLBL\r\nCHKD2960,70 CLAB\r\nCHKD9508740,42";
		s = s.replaceAll("\r\n", "").replaceAll("\\s+", "");
		char[] chars = s.toCharArray();
		int index = s.indexOf("OPBL");
		int lastDigitIndex = getLastDigitIndex(chars, index+8);
		System.out.println(String.valueOf(chars, index+4, 1)+"---"+String.valueOf(chars, index+5, 3)+"---"+new BigDecimal(String.valueOf(chars, index+8, lastDigitIndex-index-7).replaceAll(",", ".")));
		index = s.indexOf("CLBL");
		lastDigitIndex = getLastDigitIndex(chars, index+8);
		System.out.println(String.valueOf(chars, index+4, 1)+"---"+String.valueOf(chars, index+5, 3)+"---"+new BigDecimal(String.valueOf(chars, index+8, lastDigitIndex-index-7).replaceAll(",", ".")));
		index = s.indexOf("CLAB");
		lastDigitIndex = getLastDigitIndex(chars, index+8);
		System.out.println(String.valueOf(chars, index+4, 1)+"---"+String.valueOf(chars, index+5, 3)+"---"+new BigDecimal(String.valueOf(chars, index+8, lastDigitIndex-index-7).replaceAll(",", ".")));
*/
		/*String s = "1601190121DD38,00NTRFICMTI3";
		char[] chars = s.toCharArray();
		int lastDigitIndex = getLastDigitIndex(chars, 12);
		System.out.println(String.valueOf(chars, 0, 6)+"---"+String.valueOf(chars, 6, 4)+"---"+String.valueOf(chars, 10, 1)+"---"+String.valueOf(chars, 11, 1)+"---"+String.valueOf(chars, 12, lastDigitIndex-11)+"---"+String.valueOf(chars, lastDigitIndex+2, 3)+"---"+String.valueOf(chars, lastDigitIndex+5, chars.length-lastDigitIndex-5));
*/
		/*String[] tagVal86 = "TD?OUTGOING CHATS?BENM?DBS-IDEAL CORP 1?RA?HKD0,05?REMI?MT942 live verification. please process and debit HKD 0.01 fees?BB? DBS BANK (HONG KONG) LIMITED".replaceAll("\r\n", "").split("\\?");
		for(int j=0;j<tagVal86.length/2;j++) {
			int index = j*2;
			System.out.println(tagVal86[index]+"---"+tagVal86[index+1]);
		}*/
		/*String path = "D:/docs/vareport/mt94x1.txt";
		File file = new File(path);
		InputStream in = new FileInputStream(file);
		byte[] buf = new byte[in.available()];
		in.read(buf);
		IConversionService srv = new ConversionService();
		SwiftMessage msg = srv.getMessageFromFIN(new String(buf));*/
//		System.out.println(msg.getMIR());
//		System.out.println(msg.getMUR());
//		System.out.println(msg.getPDE());
//		System.out.println(msg.getPDM());
//		System.out.println(msg.getTypeInt());
//		System.out.println(msg.getUUID());
//		System.out.println(msg.getId());
//		
//		SwiftBlock2 block2 = msg.getBlock2();
//		System.out.println(block2.getMessagePriority()+"---"+block2.getMessagePriorityType());
//		System.out.println("BlockType---"+block2.getBlockType());
//		System.out.println("BlockValue---"+block2.getBlockValue());
//		System.out.println("Name---"+block2.getName());
//		System.out.println("Value---"+block2.getValue());
//		System.out.println("Id---"+block2.getId());
//		System.out.println("Number---"+block2.getNumber());
//		System.out.println(msg);
		/*SwiftBlock4 b4 = msg.getBlock4();
		String[] tags = {"20", "25", "28C", "34F", "13D", "61", "86", "90D", "90C"};
		for(String tag:tags) {
			String[] f20CValues = b4.getTagValues(tag);
			for(int i=0; i<f20CValues.length; i++) {
				String s = f20CValues[i].replaceAll("\r\n", "");
				Field20C f20C = new Field20C(s);
				System.out.println(tag+"---"+s+"---"+f20C.getComponent1()+"---"+f20C.getComponent2());
			}
		}*/
//		AbstractMT mt = AbstractMT.parse(file);
//		System.out.println("ApplicationId---------"+mt.getApplicationId());
//		System.out.println("LogicalTerminal---------"+mt.getLogicalTerminal());
//		System.out.println("MessagePriority---------"+mt.getMessagePriority());
//		System.out.println("MessageType---------"+mt.getMessageType());
//		System.out.println("Receiver---------"+mt.getReceiver());
//		System.out.println("Sender---------"+mt.getSender());
//		System.out.println("SequenceNumber---------"+mt.getSequenceNumber());
//		System.out.println("ServiceId---------"+mt.getServiceId());
//		System.out.println("SessionNumber---------"+mt.getSessionNumber());
//		System.out.println("MtId.BusinessProcess---------"+mt.getMtId().getBusinessProcess());
//		System.out.println("MtId.MessageType---------"+mt.getMtId().getMessageType());
//		System.out.println("MtId.Variant---------"+mt.getMtId().getVariant());
//		System.out.println("SwiftMessage.BlockCount---------"+mt.getSwiftMessage().getBlockCount());
//		System.out.println("SwiftMessage.Receiver---------"+mt.getSwiftMessage().getReceiver());
//		System.out.println("SwiftMessage.Sender---------"+mt.getSwiftMessage().getSender());
//		System.out.println("SwiftMessage.MIR---------"+mt.getSwiftMessage().getMIR());
//		System.out.println("SwiftMessage.MUR---------"+mt.getSwiftMessage().getMUR());
//		System.out.println("SwiftMessage.PDE---------"+mt.getSwiftMessage().getPDE());
//		System.out.println("SwiftMessage.PDM---------"+mt.getSwiftMessage().getPDM());
//		System.out.println("SwiftMessage.Type---------"+mt.getSwiftMessage().getType());
//		System.out.println("SwiftMessage.TypeInt---------"+mt.getSwiftMessage().getTypeInt());
//		System.out.println("SwiftMessage.UUID---------"+mt.getSwiftMessage().getUUID());
//		System.out.println("SwiftMessage.Id---------"+mt.getSwiftMessage().getId());
//		System.out.println("SwiftMessage.fragmentCount---------"+mt.getSwiftMessage().fragmentCount());
//		System.out.println("SwiftMessage.fragmentNumber---------"+mt.getSwiftMessage().fragmentNumber());
//		System.out.println("SwiftMessage.toXml---------"+mt.getSwiftMessage().toXml());
//		System.out.println("json---------"+mt.json());
//		System.out.println("---------"+mt);
//		in.close();
	}
}