package com.hiraeth.im.business.consumer;

import com.hiraeth.im.business.service.IMessageReceiveService;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.mq.MQForwardSuccessMessage;
import com.hiraeth.im.mq.MQBaseListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/24 11:33
 */
@Slf4j
@Service
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        consumerGroup = MQConstant.Group.GROUP_FORWARD_SUCCESS_MSG,
        topic = MQConstant.Topic.FORWARD_SUCCESS_MESSAGE,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ForwardSuccessMessageConsumer extends MQBaseListener<MQForwardSuccessMessage> {

    private final IMessageReceiveService messageReceiveService;

    public ForwardSuccessMessageConsumer(IMessageReceiveService messageReceiveService) {
        this.messageReceiveService = messageReceiveService;
    }

    @Override
    public void onMsg(MQForwardSuccessMessage message) {
        if (message == null) {
            log.warn("unknown msg null");
            return;
        }
        log.info("message [{}] forward to receiver [{}] success.", message.getMessageId(), message.getReceiverId());
        boolean success = messageReceiveService.setDeliveredSuccess(message.getMessageId());
        if(success){
           log.info("update message [{}] to delivered success.", message.getMessageId());
           return;
        }
        log.error("update message [{}] to delivered failed.", message.getMessageId());
    }
}
