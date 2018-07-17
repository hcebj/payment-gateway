package com.hce.paymentgateway.vo;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HCEHeader {
	@JSONField(name = "BIZBRCH")
	private String BIZBRCH;
	@JSONField(name = "CHNL")
	private String CHNL;
	@JSONField(name = "FRTSIDEDT")
	private String FRTSIDEDT;
	@JSONField(name = "FRTSIDESN")
	private String FRTSIDESN;
	@JSONField(name = "LGRPCD")
	private String LGRPCD;
	@JSONField(name = "TLCD")
	private String TLCD;
	@JSONField(name = "TRDCD")
	private String TRDCD;
}