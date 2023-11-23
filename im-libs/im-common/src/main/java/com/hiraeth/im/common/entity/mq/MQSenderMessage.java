package com.hiraeth.im.common.entity.mq;

import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.protocol.MessageSendRequestProto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: linxueqi
 * @description:
 * @date: 2023/11/23 15:15
 */
@Getter
@Setter
@AllArgsConstructor
public class MQSenderMessage implements Serializable {

    public MQSenderMessage(long messageId, Request request, String gatewayChannelId, MessageSendRequest message){
        this.senderId = message.getFromUid();
        this.receiveId = message.getToUid();
        this.chatType = message.getChatType().name();
        this.groupId = message.getGroupId();
        this.timeStamp = message.getTimestamp();
        this.mediaType = message.getMediaType().name();
        this.filePath = message.getFilePath();
        this.content = message.getContent();

        this.messageId = messageId;
        this.sequence = request.getSequence();
        this.requestType = request.getRequestType();
        this.messageType = request.getMessageType().getNumber();
        this.gatewayChannelId = gatewayChannelId;
    }

    private String gatewayChannelId;
    private String senderId;
    private String receiveId;      // 单聊存放用户id, 群聊存放群id
    private String chatType; // 单聊或群聊
    private int groupId;
    private long timeStamp;
    private String mediaType;
    private String content;   // 消息内容
    private String filePath; // 视频, 语音, 图片, 表情等文件地址

    private long messageId;
    private int requestType;
    /**
     * MessageType.REQUEST / MessageType.RESPONSE
     */
    private int messageType;
    private long sequence;
}
