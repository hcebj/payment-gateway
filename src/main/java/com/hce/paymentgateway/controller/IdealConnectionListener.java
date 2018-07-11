package com.hce.paymentgateway.controller;

import com.hce.paymentgateway.service.DispatcherService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Heling.Yao
 * @Date 15:48 2018/6/6
 */
@Component
public class IdealConnectionListener {
    @Resource
    private DispatcherService dispatcherService;

    @JmsListener(destination = "pgw_ideal_connect")
    public void onListener(String message) {
        dispatcherService.dispatcher(message);
    }

    @JmsListener(destination = "pgw_va_setup")
    public void listenVASetup(String message) {
        dispatcherService.processVASetup(message);
    }
}