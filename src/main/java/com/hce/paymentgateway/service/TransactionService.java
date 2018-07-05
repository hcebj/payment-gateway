package com.hce.paymentgateway.service;

import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.api.hce.TradeResponse;

/**
 * @Author Heling.Yao
 * @Date 9:55 2018/5/25
 */
public interface TransactionService<T extends TradeRequest> {

    /**
     * 处理海云汇内部单笔请求
     * @param tradeRequest
     * @return
     */
    TradeResponse handle(T tradeRequest);

}
