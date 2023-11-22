package com.hiraeth.im.gateway.tcp.push;

import com.hiraeth.im.gateway.tcp.SessionManager;
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
                        byte[] res = ("push one msg from " + userId + "$_").getBytes();
                        ByteBuf buffer = Unpooled.buffer(res.length);
                        buffer.writeBytes(res);
                        channel.writeAndFlush(buffer);
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
