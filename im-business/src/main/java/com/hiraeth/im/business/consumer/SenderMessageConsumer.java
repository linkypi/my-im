package com.hiraeth.im.business.consumer;

import com.alibaba.fastjson.JSON;
import com.hiraeth.im.business.entity.MessageReceive;
import com.hiraeth.im.business.entity.MessageSend;
import com.hiraeth.im.business.service.IMessageReceiveService;
import com.hiraeth.im.business.service.IMessageSendService;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.mq.MQForwardMessage;
import com.hiraeth.im.common.entity.mq.MQSenderMessage;
import com.hiraeth.im.common.entity.mq.MQSenderResponseMessage;
import com.hiraeth.im.common.util.CollectionUtil;
import com.hiraeth.im.mq.MQProducer;
import com.hiraeth.im.protocol.ChatTypeEnum;
import com.hiraeth.im.mq.MQBaseListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
public class SenderMessageConsumer extends MQBaseListener<MQSenderMessage> {

    private final IMessageSendService messageSendService;
    private final IMessageReceiveService messageReceiveService;
    private final TransactionTemplate transactionTemplate;
    private final MQProducer mqProducer;

    public SenderMessageConsumer(IMessageSendService messageSendService, IMessageReceiveService messageReceiveService,
                                 MQProducer mqProducer, TransactionTemplate transactionTemplate) {
        this.messageSendService = messageSendService;
        this.messageReceiveService = messageReceiveService;
        this.mqProducer = mqProducer;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void onMsg(MQSenderMessage msg) {
        if (msg == null) {
            log.warn("unknown msg null");
            return;
        }
        log.info("receive sender mq msg: {}", JSON.toJSONString(msg));

        // 存储消息
        List<String> receivers = storeMessage(msg);
        if(CollectionUtil.isNullOrEmpty(receivers)){
            return;
        }

        // 将消息放入 MQ 中, 以便转发系统将消息回复给发送者
        sendResponseMQMsg(msg);

        // 将消息通过 MQ 推送给接收者
        forwardMessageToReceiver(msg, receivers);
    }

    private void forwardMessageToReceiver(MQSenderMessage msg, List<String> receivers){
        MQForwardMessage mqForwardMessage = new MQForwardMessage(msg);
        for (String receiver: receivers) {
            mqForwardMessage.setReceiveId(receiver);
            mqProducer.syncSend(MQConstant.Topic.FORWARD_MESSAGE, mqForwardMessage);
        }
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
    public List<String> storeMessage(MQSenderMessage msg) {

        MessageSend model = buildMsgSend(msg);

        List<String> receivers = new ArrayList<>();
        List<MessageReceive> messages = new ArrayList<>();

        if (ChatTypeEnum.ChatType.SINGLE.name().equals(msg.getChatType())) {
            receivers.add(msg.getReceiveId());
            MessageReceive receive = buildMessageReceive(msg, msg.getReceiveId());
            messages.add(receive);
        } else {
            // 根据 groupId 找到群组其他成员, 为其他成员每人生成一条信息
            List<String> members = Arrays.asList("1", "2", "3");
            receivers.addAll(members);
            for (String item : members) {
                MessageReceive receive = buildMessageReceive(msg, item);
                messages.add(receive);
            }
        }

        Boolean success = transactionTemplate.execute((status) -> {
            messageReceiveService.batchSave(messages);
            messageSendService.save(model);
            return true;
        });
        if (success == null || !success) {
            log.error("store message occur error: {}", JSON.toJSONString(model));
            return null;
        }

        return receivers;
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
