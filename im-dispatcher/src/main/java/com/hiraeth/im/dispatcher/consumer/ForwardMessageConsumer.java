package com.hiraeth.im.dispatcher.consumer;

import com.alibaba.fastjson.JSON;
import com.hiraeth.im.cache.IRedisService;
import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.common.entity.Session;
import com.hiraeth.im.common.entity.mq.MQForwardMessage;
import com.hiraeth.im.dispatcher.GatewayInstanceManager;
import com.hiraeth.im.mq.MQBaseListener;
import com.hiraeth.im.protocol.*;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;

import static com.hiraeth.im.common.Constant.SESSIONS_KEY_PREFIX;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 21:53
 */
@Slf4j
@Service
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        consumerGroup = MQConstant.Group.GROUP_FORWARD_MSG,
        topic = MQConstant.Topic.FORWARD_MESSAGE,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ForwardMessageConsumer extends MQBaseListener<MQForwardMessage> {

    private final GatewayInstanceManager gatewayInstanceManager;
    private final IRedisService redisService;

    public ForwardMessageConsumer(GatewayInstanceManager gatewayInstanceManager, IRedisService redisService) {
        this.gatewayInstanceManager = gatewayInstanceManager;
        this.redisService = redisService;
    }
    @Override
    public void onMsg(MQForwardMessage message) {

        if (message == null) {
            log.warn("unknown msg : null.");
            return;
        }

        Session session = redisService.getObj(SESSIONS_KEY_PREFIX + message.getReceiveId(), Session.class);
        if (session == null || session.getGatewayChannelId() == null || "".equals(session.getGatewayChannelId())) {
            log.error("forward message failed because receiver's session not found, may be disconnected.Receiver id: {}, msg: {}, session: {}",
                    message.getReceiveId(), JSON.toJSONString(message), JSON.toJSONString(session));
            return;
        }
        SocketChannel gatewayInstance = gatewayInstanceManager.getGatewayInstance(session.getGatewayChannelId());
        if (gatewayInstance == null) {
            log.error("forward msg to gateway server failed, because gateway instance not found, gateway channel id: {}, msg: {}",
                    message.getGatewayChannelId(), JSON.toJSONString(message));
            return;
        }

        int requestType = message.getRequestType();
        if (RequestTypeProto.RequestType.FORWARD_MESSAGE_VALUE == requestType) {

            MessageForwardRequestProto.MessageForwardRequest.Builder builder = MessageForwardRequestProto.MessageForwardRequest.newBuilder();
            builder.setTimestamp(System.currentTimeMillis());
            builder.setChatType(ChatTypeEnum.ChatType.valueOf(message.getChatType()));
            builder.setMediaType(MediaTypeEnum.MediaType.valueOf(message.getMediaType()));
            builder.setSenderId(message.getSenderId());
            builder.setReceiverId(message.getReceiveId());
            builder.setContent(message.getContent());
            builder.setMessageId(message.getMessageId());
            builder.setFilePath(message.getFilePath());
            builder.setGroupId(message.getGroupId());
            Request request = new Request(Constant.APP_SDK_VERSION,
                    RequestTypeProto.RequestType.FORWARD_MESSAGE, builder.build().toByteArray());
            gatewayInstance.writeAndFlush(request.getBuffer());
        }
    }
}
