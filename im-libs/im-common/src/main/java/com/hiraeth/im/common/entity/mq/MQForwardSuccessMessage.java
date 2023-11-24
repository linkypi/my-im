package com.hiraeth.im.common.entity.mq;

import com.hiraeth.im.protocol.MessageForwardResponseProto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: linxueqi
 * @description:
 * @date: 2023/11/24 11:27
 */
@Getter
@Setter
@AllArgsConstructor
public class MQForwardSuccessMessage implements Serializable {

    public MQForwardSuccessMessage(String gatewayChannelId, MessageForwardResponseProto.MessageForwardResponse msg){
        this.senderId = msg.getSenderId();
        this.receiverId = msg.getReceiverId();
        this.messageId = msg.getMessageId();
        this.gatewayChannelId = gatewayChannelId;
    }

    private String gatewayChannelId;
    private String senderId;
    private String receiverId;
    private long messageId;
}
