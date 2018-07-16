package com.hce.paymentgateway.api.hce;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zonga on 2018/5/24.
 */
@Getter
@Setter
public class PayRocketmqDto   {
	
	private body body;
	private head head;
	public PayRocketmqDto() {
		super();
		body = new body();
		head = new head();
	}
	
	@Getter
	@Setter
	public static class body{
		private String transId;
	    private Integer status;
	    private String transactionStatus;
	    private String additionalInformation;
	    private String corp;
	    public body() {
	    	transId = "";
	    	status = 0;
	    	transactionStatus = "";
	    	additionalInformation = "";
	    	corp = "";
		}
		
	}
	
	@Getter
	@Setter
	public static class head{
		private String TLCD;//柜员代号
	    private String TRDCD;//交易码
	    private String BIZBRCH;//机构
	    private String LGRPCD;//法人代码
	    private String FRTSIDESN;//前台流水
	    private String FRTSIDEDT;//前台日期
	    private String CHNL;//渠道
	    private String TRDDT;//交易日期
	    public head() {
	    	TLCD = "";
	    	TRDCD = "";
	    	BIZBRCH = "";
	    	LGRPCD = "";
	    	FRTSIDESN = "";
	    	FRTSIDEDT = "";
	    	CHNL = "";
	    	TRDDT = "";
		}
	}
}
