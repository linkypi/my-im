package com.hiraeth.im.common.entity.mq;

import com.hiraeth.im.protocol.RequestTypeProto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 转发消息给接收者
 * @author: linxueqi
 * @description:
 * @date: 2023/11/23 21:47
 */
@Getter
@Setter
@AllArgsConstructor
public class MQForwardMessage {
    public MQForwardMessage(MQSenderMessage message){
        this.senderId = message.getSenderId();
        this.receiveId = message.getReceiveId();
        this.chatType = message.getChatType();
        this.groupId = message.getGroupId();
        this.timeStamp = message.getTimeStamp();
        this.mediaType = message.getMediaType();
        this.filePath = message.getFilePath();
        this.content = message.getContent();

        this.messageId = message.getMessageId();
        this.requestType = RequestTypeProto.RequestType.FORWARD_MESSAGE_VALUE;
        this.gatewayChannelId = message.getGatewayChannelId();
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

}
