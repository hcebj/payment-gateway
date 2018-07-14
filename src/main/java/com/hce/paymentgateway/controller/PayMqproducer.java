package com.hce.paymentgateway.controller;

import javax.annotation.Resource;

import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import io.github.rhwayfun.springboot.rocketmq.starter.common.DefaultRocketMqProducer;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class PayMqproducer {

    @Resource
    private DefaultRocketMqProducer producer;
    
    public PayMqproducer(){}
    
    private int tryCount=3;
    public void sendMsg(String tags,String msgInfo) {
    	
    	 boolean sendResult = false;
    	//String msgInfo="";
    	for(int i = 0;i<tryCount;i++ ){
    		    Message msg = new Message("CBSPAY", tags, msgInfo.getBytes());
    		    sendResult = producer.sendMsg(msg);
    	        if(!sendResult){
    	        	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    	        	continue;
    	        }else{
    	        	break;
    	        }
    	}
       if(sendResult){
    	   log.info("发送结果：success！");
       }else{
    	   log.info("发送结果：failed！");
       }
       // System.out.println("发送结果：" + sendResult);
        
    }

}
