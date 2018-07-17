package com.hce.paymentgateway.service.impl.hce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.dao.DBSVASetupDao;
import com.hce.paymentgateway.service.impl.VASetupResponseProcessServiceImpl;
import com.hce.paymentgateway.vo.HCEDBSVASetupVO;

@Service("HCEVASetupResponseProcessServiceImpl")
public class HCEVASetupResponseProcessServiceImpl extends VASetupResponseProcessServiceImpl {
	@Autowired
	private DBSVASetupDao dbsVASetupDao;

	@Override
	protected Object getExceptionVO(String vaNumber, String fileName, String status, String failureReason, String erpCode) {
		HCEDBSVASetupVO vo = new HCEDBSVASetupVO();
		vo.setCorp(dbsVASetupDao.findByMasterAC(vaNumber).getCorp());
		vo.setMasterAC(vaNumber);
		vo.setStatus(status);
		vo.setFailureReason(failureReason);
		return null;
	}

	@Override
	public String getMQName() {
		return Constant.MQ_NAME_IN_HCE;
	}
}