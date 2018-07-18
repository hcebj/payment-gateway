package com.hce.paymentgateway.service.impl.hce;

import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.entity.DBSVAReportEntity;
import com.hce.paymentgateway.service.impl.VAReportResponseProcessServiceImpl;

@Service("HCEVAReportResponseProcessServiceImpl")
public class HCEVAReportResponseProcessServiceImpl extends VAReportResponseProcessServiceImpl {
	@Override
	protected Object getResponseVO(DBSVAReportEntity vareport) {
		return vareport;
	}

	@Override
	public String getMQName() {
		return Constant.MQ_NAME_OUT_HCE;
	}

	@Override
	public String getMsgTag() {
		return "35040";
	}

	@Override
	protected String getCorp() {
		String corp = corpHolder.get();
		corpHolder.remove();
		return corp;
	}
}