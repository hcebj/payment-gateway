package com.hce.paymentgateway.job;

import com.hce.paymentgateway.dao.AccountTransferDao;
import com.hce.paymentgateway.entity.AccountTransferEntity;
import com.hce.paymentgateway.util.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.hce.paymentgateway.util.Constant.MAX_QUERY_COUNT;

/**
 * @Author Heling.Yao
 * @Date 10:51 2018/5/28
 */
@Component
@Slf4j
public class AccountTransferScheduling extends AbstractSchedulingService<AccountTransferEntity> {

    @Resource(name = "accountTransferDao")
    private AccountTransferDao accountTransferDao;

    /**
     * 0. 查询处理中数据
     */
    protected List<AccountTransferEntity> queryProcessingData(int init, int interval) {
        List<AccountTransferEntity> accounts = accountTransferDao.findByStatusAndQueryCountLessThan(
            PaymentStatus.PROCESSING.getStatus(), MAX_QUERY_COUNT, new PageRequest(init, interval));
        return accounts;
    }

    /**
     * 1. 数据更新是否成功, 成功继续执行，失败则跳出
     */
    protected boolean updateQueryCount(AccountTransferEntity accountTransfer) {
        int updateSize = accountTransferDao.updateCountByIdAndCount(accountTransfer.getId(), accountTransfer.getQueryCount(),
            (accountTransfer.getQueryCount() + 1), new Date());
        if(updateSize == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void updatePaymentStatus(AccountTransferEntity transfer, PaymentStatus paymentStatus, String errorCode, String errorMsg) {
    	log.info(String.format("id:%s,pamentStatus:%s,errorcode:%s,errorMsg:%s", transfer.getId(),paymentStatus.getStatus(),errorCode, errorMsg));
        accountTransferDao.updateStatusById(transfer.getId(), paymentStatus.getStatus(), new Date(), errorCode, errorMsg);
    }
    
    @Override
    protected void updateFileName1(AccountTransferEntity transfer,String fileName,String ackFileType){
    	String filName1 = "";
    	if(ackFileType.equals("ACK1")){
    		filName1 = fileName;
    	}else{
    		filName1 = transfer.getFileName1() + "/" + fileName;
    	}
    	//= transfer.getFileName1() + "/" + fileName;
    	accountTransferDao.updateFileName1ById(transfer.getId(), filName1, ackFileType, new Date());
    }
    
    @Override
    protected void updatePaymentDateById(AccountTransferEntity transfer,String paymentDate){
    	accountTransferDao.updatePaymentDateById(transfer.getId(), paymentDate, new Date());
    }
}
