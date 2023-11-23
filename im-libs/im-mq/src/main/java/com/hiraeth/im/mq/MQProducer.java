package com.hiraeth.im.mq;

import com.alibaba.fastjson.JSON;
import com.hiraeth.im.cache.IRedisService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 * Created by linxueqi on 2021/1/13 16:20
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "rocketmq.producer", name = "group", matchIfMissing = false)
public class MQProducer {

    // 每条消息最大 15 M
    @ExtRocketMQTemplateConfiguration( nameServer = "${rocketmq.name-server}", maxMessageSize = 15728640)
    private static class IMRocketMQTemplate extends RocketMQTemplate {
    }

    @Autowired
    private IMRocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.send-message-timeout:5000}")
    private int sendMsgTimeout;

    @Autowired
    private IRedisService redisService;

    private static final String FAULT_MQ = "FAULT_MESSAGEQUEUE";

    /**
     * 异步发送消息
     * @param topic 主题，若需要加 Tag 标签则可以在 topic 后直接拼接，
     *              如Topic为 TopicA, Tag 为 TagA, TagB 则可拼接为: TopicA:TagA|TagB|...
     * @param msg  具体发送的消息，可以是 Integer, List<Integer>, VestingDto 及其他复杂数据结构
     * @param cacheOnFailed  发送失败时是否需要暂存到redis， 由定时任务的
     */
    public <T> void asyncSend(String topic, T msg, boolean cacheOnFailed) {
        rocketMQTemplate.getProducer().setVipChannelEnabled(false);
        final Message<T> message = MessageBuilder.withPayload(msg).build();
        rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                final String readableSr = CommonUtil.getShortStr(message.getPayload());
                log.info("send mq message successfully, topic: {}, body: {}.", topic, readableSr);
            }

            @SneakyThrows
            @Override
            public void onException(Throwable ex) {
                final String readableSr = CommonUtil.getShortStr(message.getPayload());
                log.error("send mq message take error, topic: {}, body: {}.", topic, readableSr, ex);
                if (!cacheOnFailed) {
                    return;
                }
                // 发送失败降级处理
                Map<String, String> json = new HashMap<>();
                json.put("topic", topic);
                json.put("msg", JSON.toJSONString(msg));
                redisService.lpush(FAULT_MQ, json);

                // 发送邮件告警
            }
        }, sendMsgTimeout);
    }



    public <T> void asyncSend(String topic, T msg){
        asyncSend(topic, msg, true);
    }

    public <T> void asyncSend(String topic, T msg, SendCallback sendCallback) {
        final Message<T> message = MessageBuilder.withPayload(msg).build();
        rocketMQTemplate.asyncSend(topic, message, sendCallback , sendMsgTimeout);
    }

    public <T> SendResult syncSend(String topic, T msg) {
        final Message<T> message = MessageBuilder.withPayload(msg).build();
        return rocketMQTemplate.syncSend(topic, message , sendMsgTimeout);
    }

}
