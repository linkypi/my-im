package com.hiraeth.im.dispatcher.handler;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.common.MQConstant;
import com.hiraeth.im.common.entity.mq.MQForwardSuccessMessage;
import com.hiraeth.im.common.util.CommonUtil;
import com.hiraeth.im.mq.MQProducer;
import com.hiraeth.im.protocol.AuthenticateResponseProto;
import com.hiraeth.im.protocol.MessageForwardResponseProto;
import com.hiraeth.im.protocol.MessageSendResponseProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.entity.Response;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: leo
 * @description:
 * @ClassName: com.lynch.im.sdk
 * @date: 2023/11/22 11:09
 */
@Slf4j
@Component
public class ResponseHandler {

    private final MQProducer mqProducer;

    public ResponseHandler(MQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    public void handle(Response response, SocketChannel channel) throws InvalidProtocolBufferException {
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == response.getRequestType()){
            AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(response.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }
        if(RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == response.getRequestType()){
            MessageSendResponseProto.MessageSendResponse msg = MessageSendResponseProto.MessageSendResponse.parseFrom(response.getBody());
            log.info("send message response: {}", JSON.toJSONString(msg));
        }

        if(RequestTypeProto.RequestType.FORWARD_MESSAGE_VALUE == response.getRequestType()){
            MessageForwardResponseProto.MessageForwardResponse forwardResponse = MessageForwardResponseProto.MessageForwardResponse.parseFrom(response.getBody());
            log.info("forward message response: {}", JSON.toJSONString(forwardResponse));

            // 发送 MQ , 异步更新数据表, 标记消息已投递
            String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
            MQForwardSuccessMessage msg = new MQForwardSuccessMessage(gatewayChannelId, forwardResponse);
            mqProducer.syncSend(MQConstant.Topic.FORWARD_SUCCESS_MESSAGE, msg);
        }
    }
}
