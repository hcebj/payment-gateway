package com.hce.paymentgateway.service.impl.hce;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.dao.AccountTransferDao;
import com.hce.paymentgateway.entity.AccountTransferEntity;
import com.hce.paymentgateway.entity.DBSMT94XDetailEntity;
import com.hce.paymentgateway.entity.DBSMT94XHeaderEntity;
import com.hce.paymentgateway.service.impl.MT94XResponseProcessServiceImpl;
import com.hce.paymentgateway.vo.HCEDBSMT94XVO;

@Service("HCEMT94XResponseServceImpl")
public class HCEMT94XResponseServceImpl extends MT94XResponseProcessServiceImpl {
	@Autowired
	private AccountTransferDao accountTransferDao;

	protected Object getResponseVO(DBSMT94XHeaderEntity mt94x, DBSMT94XDetailEntity mt94xDetail) {
		HCEDBSMT94XVO vo = new HCEDBSMT94XVO();
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
		List<AccountTransferEntity> tranfers = accountTransferDao.findByPaymentId(mt94xDetail.getReferenceToTheAccountOwner());
		String transId = null;
		String transTime = null;
		if(tranfers!=null) {
			if(tranfers.size()==0) {
				transId = "0";
				transTime = "0";
			} else if(tranfers.size()==1) {
				AccountTransferEntity transfer = tranfers.get(0);
				transId = transfer.getTransId();
				transTime = transfer.getTransTime();
			} else {
				transId = "多条记录";
				transTime = "多条记录";
			}
		} else {
			transId = "null";
			transTime = "null";
		}
		vo.setTransId(transId);
		vo.setTransTime(transTime);
		return vo;
	}

	@Override
	public String getMQName() {
		return Constant.MQ_NAME_IN_HCE;
	}

	@Override
	public String getMsgTag() {
		String tag = tagHolder.get();
		tagHolder.remove();
		return tag;
	}

	@Override
	protected String getCorp() {
		String corp = corpHolder.get();
		corpHolder.remove();
		return corp;
	}
}