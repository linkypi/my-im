package com.hiraeth.im.dispatcher.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.cache.IRedisService;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.common.entity.Response;
import com.hiraeth.im.common.entity.mq.MQSenderMessage;
import com.hiraeth.im.common.snowflake.SnowFlakeIdUtil;
import com.hiraeth.im.common.util.CommonUtil;
import com.hiraeth.im.protocol.AuthenticateRequestProto.*;
import com.hiraeth.im.protocol.AuthenticateResponseProto.*;
import com.hiraeth.im.protocol.MessageSendRequestProto;
import com.hiraeth.im.protocol.MessageSendResponseProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.protocol.StatusCodeEnum;
import com.hiraeth.im.mq.MQProducer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/21 22:10
 */
@Slf4j
@Component
public class RequestHandler {

    @Autowired
    private IRedisService redisService;

    @Autowired
    private MQProducer mqProducer;

    public void handle(Request request, SocketChannel socketChannel) throws InvalidProtocolBufferException {
        if (RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()) {
            Response response = authenticate(request, socketChannel);
            socketChannel.writeAndFlush(response.getBuffer());
            return;
        }
        if (RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == request.getRequestType()) {
            saveMessage(request, socketChannel);
            return;
        }
        log.warn("unknown request: {}", JSON.toJSONString(request));
    }

    /**
     * 将消息保存到 MQ , 异步解耦后通过 im-business 业务系统来存储消息
     * 存储成功后, 业务系统再发MQ消息回复发送者处理结果
     * @param request
     * @param channel
     * @return
     * @throws InvalidProtocolBufferException
     */
    public void saveMessage(Request request, SocketChannel channel) throws InvalidProtocolBufferException {
        MessageSendRequestProto.MessageSendRequest msg = MessageSendRequestProto.MessageSendRequest.parseFrom(request.getBody());
        log.info("get message from gateway server: {}", JSON.toJSONString(msg));

        String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
        long messageId = SnowFlakeIdUtil.getNextId();
        MQSenderMessage mqSenderMessage = new MQSenderMessage(messageId, request, gatewayChannelId, msg);
        // 保存消息， 得到 messageId 并返回
        mqProducer.syncSend(MQConstant.Topic.CHAT_SENDER_MESSAGE, mqSenderMessage);
    }

    public Response authenticate(Request request, SocketChannel channel) throws InvalidProtocolBufferException {

        AuthenticateRequest authenticateRequest = AuthenticateRequest.parseFrom(request.getBody());
        String userId = authenticateRequest.getUid();
        String token = authenticateRequest.getToken();

        AuthenticateResponse.Builder builder = AuthenticateResponse.newBuilder();
        builder.setUid(userId);
        builder.setToken(token);
        builder.setTimestamp(System.currentTimeMillis());

        try {
            // 连接 SSO 单点登录系统验证 userId 及 token 是否有效
            if ("test001".equals(userId) && "token".equals(token)) {

                // 将 session 存储到 Redis 中
                // key = uid , value = {
                //    'isAuthenticated':true,
                //    'token': '...',
                //    'timestamp': 'xxx',
                //    'authenticatedTime': '...',
                //    'gatewayChannelId': 564
                //   }
                String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", token);
                jsonObject.put("timestamp", System.currentTimeMillis());
                jsonObject.put("authenticatedTime", System.currentTimeMillis());
                jsonObject.put("isAuthenticated", true);
                jsonObject.put("gatewayChannelId", gatewayChannelId);
                redisService.set("sessions:"+ userId, jsonObject.toJSONString());

                builder.setSuccess(true);
                builder.setCode(StatusCodeEnum.StatusCode.SUCCESS);
                builder.setMessage("");
                return new Response(request, builder.build().toByteArray());
            }
        }catch (Exception ex){
            log.error("authenticate occur error, request: {}", JSON.toJSONString(request), ex);
        }
        builder.setSuccess(false);
        builder.setCode(StatusCodeEnum.StatusCode.UNAUTHORIZED);
        builder.setMessage("user or password is invalid");
        return new Response(request, builder.build().toByteArray());
    }
}
