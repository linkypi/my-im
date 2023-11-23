package com.hiraeth.im.common.entity.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: linxueqi
 * @description:
 * @date: 2023/11/23 17:35
 */
@Getter
@Setter
@AllArgsConstructor
public class MQSenderResponseMessage implements Serializable {

    public MQSenderResponseMessage(MQSenderMessage msg){
        this.timeStamp = msg.getTimeStamp();
        this.senderId = msg.getSenderId();
        this.receiverId = msg.getReceiveId();
        this.groupId = msg.getGroupId();
        this.requestType = msg.getRequestType();
        this.messageId = msg.getMessageId();
        this.gatewayChannelId = msg.getGatewayChannelId();
    }

    private String gatewayChannelId;
    private String senderId;
    private String receiverId;
    private int groupId;
    private long timeStamp;
    private long messageId;
    private int requestType;
}
