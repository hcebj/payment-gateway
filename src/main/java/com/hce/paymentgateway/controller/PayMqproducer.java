package com.hce.paymentgateway.controller;

import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hce.paymentgateway.Constant;

import io.github.rhwayfun.springboot.rocketmq.starter.common.DefaultRocketMqProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PayMqproducer {
	@Autowired
    private DefaultRocketMqProducer producer;
    
    public PayMqproducer() {
    	
    }
    
    private int tries=3;

    public void sendMsg(String tags,String msgInfo) {
    	boolean sendResult = false;
    	for(int i = 0;i<tries;i++ ){
    		Message msg = new Message(Constant.MQ_NAME_HCE, tags, msgInfo.getBytes());
    		sendResult = producer.sendMsg(msg);
    		if(!sendResult) {
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    			continue;
    		} else {
    			break;
    		}
    	}
    	log.info("发送结果："+(sendResult?"success":"failed")+"！");
    }
}