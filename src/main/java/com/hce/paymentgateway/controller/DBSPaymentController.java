package com.hce.paymentgateway.controller;

import com.hce.paymentgateway.api.hce.TradeResponse;
import com.hce.paymentgateway.service.DispatcherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 * Created by zonga on 2018/5/24.
 */
@RestController
@RequestMapping(value = "/hce-payment/dbs/transaction")
public class DBSPaymentController {
    @Autowired
    private DispatcherService dispatcherService;

    @RequestMapping(value = "/accountTransfer", method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public TradeResponse accountTransfer(@RequestBody AccountTransferRequest request) {
    public TradeResponse accountTransfer(@RequestBody String request) {
        TradeResponse response = dispatcherService.dispatcher(request);
        return response;
    }
}