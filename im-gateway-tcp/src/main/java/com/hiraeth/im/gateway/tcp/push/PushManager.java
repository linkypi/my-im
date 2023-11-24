package com.hiraeth.im.gateway.tcp.push;

import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.gateway.tcp.SessionManager;
import com.hiraeth.im.protocol.*;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * 根据当前存在的连接会话进行主动推送
 * @author linxueqi
 * @ClassName PushManager
 * @description: TODO
 * @date 11/20/23 4:16 PM
 */
@Slf4j
public class PushManager {

    public void start() {
        new PushThread().start();
    }

    static class PushThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String userId = "test001";
                    Thread.sleep(10 * 1000);
                    SocketChannel channel = SessionManager.getSession(userId);
                    if (channel != null) {
                        String msg = "push one msg from " + userId ;
                        MessagePushRequestProto.MessagePushRequest.Builder builder = MessagePushRequestProto.MessagePushRequest.newBuilder();
                        builder.setTimestamp(System.currentTimeMillis());
                        builder.setChatType(ChatTypeEnum.ChatType.SINGLE);
                        builder.setMediaType(MediaTypeEnum.MediaType.TEXT);
                        builder.setSenderId(userId);
                        builder.setReceiverId(userId);
                        builder.setContent(msg);
                        Request request = new Request(Constant.APP_SDK_VERSION,
                                RequestTypeProto.RequestType.PUSH_MESSAGE, builder.build().toByteArray());

                        channel.writeAndFlush(request.getBuffer());
                        log.info("push msg to client success: {}", userId);
                    } else {
                        log.info("user id [{}] be off line", userId);
                    }
                    Thread.sleep(10 * 1000);
                } catch (Exception ex) {
                    log.error("push manager occur error", ex);
                }
            }
        }
    }
}
