package com.hiraeth.im.gateway.tcp.push;

import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.Request;
import com.hiraeth.im.gateway.tcp.SessionManager;
import com.hiraeth.im.protocol.MessageProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author leo
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
                    SessionManager instance = SessionManager.getInstance();
                    SocketChannel channel = instance.getSession(userId);
                    if (channel != null) {
                        String msg = "push one msg from " + userId ;
                        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
                        builder.setTimestamp(System.currentTimeMillis());
                        builder.setFromUid(userId);
                        builder.setText(msg);
                        Request request = new Request(Constant.APP_SDK_VERSION,
                                RequestTypeProto.RequestType.SEND_MESSAGE, builder.build().toByteArray());

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
