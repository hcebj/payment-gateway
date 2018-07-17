package com.hce.paymentgateway.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hce.paymentgateway.Constant;
import com.hce.paymentgateway.service.DispatcherService;

import io.github.rhwayfun.springboot.rocketmq.starter.common.AbstractRocketMqConsumer;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqContent;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqTopic;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PayMqConsumer extends AbstractRocketMqConsumer<PayMqTopic, PayMqContent> {
	@Autowired
	private DispatcherService dispatcherService;

    @Override
    public boolean consumeMsg(PayMqContent content, MessageExt msg) {
		log.info(new Date() + ",########## " + new String(msg.getBody()));
		switch (msg.getTopic()) {
			case Constant.MQ_NAME_IN_HCE: {//核心支付消息
				if(msg.getBody()!=null) {
					if(msg.getTags().equals("35031")) {
						Map<String, Object> maps = (Map)JSON.parse(new String(msg.getBody()));
						log.info("接收到支付消息："+new String(msg.getBody()));
						log.info("接受到的body" + maps.get("body"));
						if(maps.get("body")!=null){
							dispatcherService.dispatcher(new String(maps.get("body").toString()));
						}
						log.info("here!");
						//payMqproducer.sendMsg("35303", "I'm WangShaohua!");
					} else if(msg.getTags().equals("17012")) {
						log.info("接收到VaSetup消息：" + new String(msg.getBody()));
						try {
							dispatcherService.processVASetup(new String(msg.getBody()));
						} catch (Exception e) {
							log.error("VA_SETUP_ERROR: \r\n", e);
							return false;
						}
					}
				}
				break;
			}
			default:
		}
		return true;
    }

    @Override
    public Map<String, Set<String>> subscribeTopicTags() {
        Map<String, Set<String>> map = new HashMap<>();
        Set<String> tags = new HashSet<>();
        //订阅的消息交易码
        tags.add("35031");
        tags.add("17012");
        //tags.add("17013");
        map.put(Constant.MQ_NAME_IN_HCE, tags);
        return map;
    }

    @Override
    public String getConsumerGroup() {
        return "pay_consumer-group";
    }
}

class PayMqTopic implements RocketMqTopic {
    @Override
    public String getTopic() {
        return "WSH";
    }
}

@Getter
@Setter
class PayMqContent extends RocketMqContent {
	private static final long serialVersionUID = 1L;
	private int id;
    private String desc;
}