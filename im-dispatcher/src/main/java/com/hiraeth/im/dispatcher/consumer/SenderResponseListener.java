package com.hiraeth.im.dispatcher.consumer;

import com.alibaba.fastjson.JSON;
import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.Response;
import com.hiraeth.im.common.entity.mq.MQSenderResponseMessage;
import com.hiraeth.im.common.snowflake.SnowFlakeIdUtil;
import com.hiraeth.im.dispatcher.GatewayInstanceManager;
import com.hiraeth.im.mq.MQBaseListener;
import com.hiraeth.im.protocol.MessageSendResponseProto;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.protocol.StatusCodeEnum;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 将
 * @author: lynch
 * @description:
 * @date: 2023/11/23 17:47
 */
@Slf4j
@Service
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        consumerGroup = MQConstant.Group.GROUP_RESPONSE_SENDER_MSG,
        topic = MQConstant.Topic.RESPONSE_SENDER_MESSAGE,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class SenderResponseListener extends MQBaseListener<MQSenderResponseMessage> {

    @Autowired
    private GatewayInstanceManager gatewayInstanceManager;

    @Override
    public void onMsg(MQSenderResponseMessage message) {

        if (message == null) {
            log.warn("unknown msg : null.");
            return;
        }

        // 通过messageId查询到消息后, 通过 GatewayInstanceManager 将消息转发到相关 gateway server 实例
        SocketChannel gatewayInstance = gatewayInstanceManager.getGatewayInstance(message.getGatewayChannelId());
        if (gatewayInstance == null) {
            log.error("send response to gateway server failed, because gateway instance not found, gateway channel id: {}, msg: {}",
                    message.getGatewayChannelId(), JSON.toJSONString(message));
            return;
        }

        int requestType = message.getRequestType();
        if (RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == requestType) {
            MessageSendResponseProto.MessageSendResponse.Builder builder = MessageSendResponseProto.MessageSendResponse.newBuilder();
            builder.setCode(StatusCodeEnum.StatusCode.SUCCESS);
            builder.setSuccess(true);
            builder.setSenderId(message.getSenderId());
            builder.setReceiverId(message.getReceiverId());
            builder.setTimestamp(System.currentTimeMillis());
            builder.setMessageId(message.getMessageId());
            byte[] body = builder.build().toByteArray();

            Response response = new Response(Constant.APP_SDK_VERSION,
                    MessageTypeEnum.MessageType.RESPONSE,
                    (byte) message.getRequestType()
                    , SnowFlakeIdUtil.getNextId(), body.length, body);
            gatewayInstance.writeAndFlush(response.getBuffer());
        }
    }
}
