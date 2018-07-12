package com.hce.paymentgateway.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.prowidesoftware.swift.io.ConversionService;
import com.prowidesoftware.swift.io.IConversionService;
import com.prowidesoftware.swift.model.SwiftBlock4;
import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.field.Field20C;
import com.prowidesoftware.swift.model.mt.AbstractMT;

@Service("mt94xResponseProcessServiceImpl")
public class MT94XResponseProcessServiceImpl extends BaseResponseProcessServiceImpl {
	@Override
	protected void process(File file) throws IOException, ParseException {
		
	}

	public static void main(String[] args) throws IOException {
		String path = "D:/docs/vareport/mt94x1.txt";
		File file = new File(path);
		InputStream in = new FileInputStream(file);
		
		byte[] buf = new byte[in.available()];
		in.read(buf);
		IConversionService srv = new ConversionService();
		SwiftMessage msg = srv.getMessageFromFIN(new String(buf));
		SwiftBlock4 b4 = msg.getBlock4();
		
		String[] tags = {"20", "25", "28C", "34F", "13D", "61", "86", "90D", "90C"};
		for(String tag:tags) {
			String[] f20CValues = b4.getTagValues(tag);
			for(int i=0; i<f20CValues.length; i++) {
				String s = f20CValues[i].replaceAll("\r\n", "");
				Field20C f20C = new Field20C(s);
				System.out.println(tag+"---"+s+"---"+f20C.getComponent1()+"---"+f20C.getComponent2());
			}
		}
		AbstractMT mt = AbstractMT.parse(file);
		System.out.println("ApplicationId---------"+mt.getApplicationId());
		System.out.println("LogicalTerminal---------"+mt.getLogicalTerminal());
		System.out.println("MessagePriority---------"+mt.getMessagePriority());
		System.out.println("MessageType---------"+mt.getMessageType());
		System.out.println("Receiver---------"+mt.getReceiver());
		System.out.println("Sender---------"+mt.getSender());
		System.out.println("SequenceNumber---------"+mt.getSequenceNumber());
		System.out.println("ServiceId---------"+mt.getServiceId());
		System.out.println("SessionNumber---------"+mt.getSessionNumber());
		System.out.println("MtId.BusinessProcess---------"+mt.getMtId().getBusinessProcess());
		System.out.println("MtId.MessageType---------"+mt.getMtId().getMessageType());
		System.out.println("MtId.Variant---------"+mt.getMtId().getVariant());
		System.out.println("SwiftMessage.BlockCount---------"+mt.getSwiftMessage().getBlockCount());
		System.out.println("SwiftMessage.Receiver---------"+mt.getSwiftMessage().getReceiver());
		System.out.println("SwiftMessage.Sender---------"+mt.getSwiftMessage().getSender());
		System.out.println("SwiftMessage.MIR---------"+mt.getSwiftMessage().getMIR());
		System.out.println("SwiftMessage.MUR---------"+mt.getSwiftMessage().getMUR());
		System.out.println("SwiftMessage.PDE---------"+mt.getSwiftMessage().getPDE());
		System.out.println("SwiftMessage.PDM---------"+mt.getSwiftMessage().getPDM());
		System.out.println("SwiftMessage.Type---------"+mt.getSwiftMessage().getType());
		System.out.println("SwiftMessage.TypeInt---------"+mt.getSwiftMessage().getTypeInt());
		System.out.println("SwiftMessage.UUID---------"+mt.getSwiftMessage().getUUID());
		System.out.println("SwiftMessage.Id---------"+mt.getSwiftMessage().getId());
		System.out.println("SwiftMessage.fragmentCount---------"+mt.getSwiftMessage().fragmentCount());
		System.out.println("SwiftMessage.fragmentNumber---------"+mt.getSwiftMessage().fragmentNumber());
		System.out.println("SwiftMessage.toXml---------"+mt.getSwiftMessage().toXml());
//		System.out.println("json---------"+mt.json());
//		System.out.println("---------"+mt);
		in.close();
	}
}