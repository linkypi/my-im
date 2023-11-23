package com.hiraeth.im.business.consumer;

import com.alibaba.fastjson.JSON;
import com.hiraeth.im.business.entity.MessageReceive;
import com.hiraeth.im.business.entity.MessageSend;
import com.hiraeth.im.business.service.IMessageReceiveService;
import com.hiraeth.im.business.service.IMessageSendService;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.mq.MQSenderMessage;
import com.hiraeth.im.common.entity.mq.MQSenderResponseMessage;
import com.hiraeth.im.mq.MQProducer;
import com.hiraeth.im.protocol.ChatTypeEnum;
import com.hiraeth.im.mq.MQBaseListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 处理IM消息，业务系统接收到MQ单聊或群聊消息后将消息入库
 * @author: linxueqi
 * @description:
 * @date: 2023/11/23 12:42
 */
@Slf4j
@Service
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        consumerGroup = MQConstant.Group.GROUP_SENDER_MSG,
        topic = MQConstant.Topic.CHAT_SENDER_MESSAGE,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MessageConsumer extends MQBaseListener<MQSenderMessage> {

    private final IMessageSendService messageSendService;

    private final IMessageReceiveService messageReceiveService;
    private final MQProducer mqProducer;

    public MessageConsumer(IMessageSendService messageSendService, IMessageReceiveService messageReceiveService, MQProducer mqProducer) {
        this.messageSendService = messageSendService;
        this.messageReceiveService = messageReceiveService;
        this.mqProducer = mqProducer;
    }

    @Override
    public void onMsg(MQSenderMessage msg) {
        if (msg == null) {
            log.warn("unknown msg null");
            return;
        }
        log.info("receive msg: {}", JSON.toJSONString(msg));

        // 存储消息
        saveMessage(msg);

        // 转发消息, 将消息放入 MQ 中, 以便转发系统将消息回复给发送者
        sendResponseMQMsg(msg);
    }

    private void sendResponseMQMsg(MQSenderMessage msg){
        MQSenderResponseMessage mqSenderResponseMessage = new MQSenderResponseMessage(msg);
        mqProducer.syncSend(MQConstant.Topic.RESPONSE_SENDER_MESSAGE, mqSenderResponseMessage);
    }

    /**
     * 保存发送者信息, 同时为接收方保存同样的信息, 若是群聊消息则需要为群组其他成员各自存储一条信息
     *
     * @param msg
     */
    @Transactional
    public void saveMessage(MQSenderMessage msg) {

        MessageSend model = buildMsgSend(msg);

        List<MessageReceive> receives = new ArrayList<>();
        if (ChatTypeEnum.ChatType.SINGLE.name().equals(msg.getChatType())) {
            MessageReceive receive = buildMessageReceive(msg, msg.getReceiveId());
            receives.add(receive);
        } else {
            // 根据 groupId 找到群组其他成员, 为其他成员每人生成一条信息
            List<String> members = Arrays.asList("1", "2", "3");
            for (String item : members) {
                MessageReceive receive = buildMessageReceive(msg, item);
                receives.add(receive);
            }
        }

        messageSendService.save(model);
        messageReceiveService.batchSave(receives);
    }

    private static MessageSend buildMsgSend(MQSenderMessage msg) {
        MessageSend model = new MessageSend();
        model.setMessageType(msg.getMessageType());
        model.setRequestType(msg.getRequestType());
        model.setMessageId(msg.getMessageId());
        model.setSequence(msg.getSequence());

        model.setReceiverId(msg.getReceiveId());
        model.setContent(msg.getContent());
        model.setFilePath(msg.getFilePath());
        model.setGroupId(msg.getGroupId());
        model.setSenderId(msg.getSenderId());
        model.setGroupId(msg.getGroupId());
        model.setMediaType(msg.getMediaType());
        model.setChatType(msg.getChatType());
        model.setInitBy(0);
        return model;
    }

    private static MessageReceive buildMessageReceive(MQSenderMessage msg, String receiveId) {
        MessageReceive receive = new MessageReceive();
        receive.setMessageType(msg.getMessageType());
        receive.setRequestType(msg.getRequestType());
        receive.setMessageId(msg.getMessageId());
        receive.setSequence(msg.getSequence());

        receive.setReceiverId(receiveId);
        receive.setContent(msg.getContent());
        receive.setFilePath(msg.getFilePath());
        receive.setGroupId(msg.getGroupId());
        receive.setSenderId(msg.getSenderId());
        receive.setGroupId(msg.getGroupId());
        receive.setMediaType(msg.getMediaType());
        receive.setChatType(msg.getChatType());
        receive.setInitBy(0);
        return receive;
    }
}
