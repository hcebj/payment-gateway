package com.hce.paymentgateway.controller;

import com.hce.paymentgateway.service.DispatcherService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
/**
 * @Author Heling.Yao
 * @Date 15:48 2018/6/6
 */
@Slf4j
@Component
public class IdealConnectionListener {
    @Autowired
    private DispatcherService dispatcherService;

    @JmsListener(destination = "pgw_ideal_connect")
    public void onListener(String message) {
        dispatcherService.dispatcher(message);
    }

    @JmsListener(destination = "pgw_va_setup")
    public void listenVASetup(String message) {
        try {
			dispatcherService.processVASetup(message);
		} catch (Exception e) {
			log.error("VA_SETUP_ERROR: \r\n", e);
		}
    }
}