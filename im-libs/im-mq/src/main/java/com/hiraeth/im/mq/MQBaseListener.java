package com.hiraeth.im.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hiraeth.im.common.util.DateUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Value;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Description
 * Created by linxueqi on 2021/1/12 13:42
 */
@Slf4j
@Setter
public abstract class MQBaseListener<T> implements RocketMQPushConsumerLifecycleListener, RocketMQListener<MessageExt> {

    protected MessageExt messageExt;

    private int maxReconsumeTimes;

    @Value("${rocketmq.consumer.max-retry-times:5}")
    public void setMaxReconsumeTimes(String maxRetryTimes){
        maxReconsumeTimes = Integer.parseInt(maxRetryTimes);
    }

    @Override
    public void onMessage(MessageExt message) {
        messageExt = message;
        String body = new String(message.getBody());
        final Map<String, String> properties = message.getProperties();
        String tags = "";
        if(properties.containsKey("TAGS")){
            tags = properties.get("TAGS");
        }
        final long bornTimestamp = message.getBornTimestamp();
        final Date date = new Date(bornTimestamp);
        final String bornTime = DateUtil.format(date, DateUtil.DATETIME_FORMAT);

        // 超过最大消费重试次数则忽略
        if( message.getReconsumeTimes() >= maxReconsumeTimes){
            log.info(" exceed max reconsume times , ignoring consume this message. msgId: {}, topic: {}, born time: {}, tags: {}.",
                    message.getMsgId(), message.getTopic(), bornTime, tags);
            return;
        }

        final String readableSr = CommonUtil.getShortStr(message.getBody());
        log.info(" received message, msgId: {}, topic: {}, tags: {}, born time: {}, body: {}.",
                message.getMsgId(), message.getTopic(), tags, bornTime, readableSr);

        final Class<T> klass = getTClass();
        Object bean = null;
        if(Objects.isNull(klass)){
            // 泛型
            final Class<?> superClassGenericType = getSuperClassGenericType(this.getClass(), 0);
            bean = JSON.parseArray(body, superClassGenericType);
        }else {
            // 非泛型
            JSONObject jsonObject = JSON.parseObject(body);
            bean = JSON.toJavaObject (jsonObject, klass);
        }
        onMsg((T)bean);
    }

    public static Class<?> getSuperClassGenericType(Class<?> clazz, int index)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        final Type[] actualTypeArguments = ((ParameterizedTypeImpl) params[index]).getActualTypeArguments();
        if(actualTypeArguments[index] instanceof Class){
            return (Class<?>) actualTypeArguments[index];
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    public abstract void onMsg(T message);

    /**
     * 若 T 是泛型则抛出异常并返回null
     * @return
     */
    public Class<T> getTClass()
    {
        try {
            final Type genericSuperclass = getClass().getGenericSuperclass();
            return (Class<T>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        }catch (Exception ex){
            return null;
        }
    }

    // 若以下设置无法满足则可重写
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 2021-01-12 11:34:23,123
        consumer.setConsumeTimestamp(UtilAll.timeMillisToHumanString2(System.currentTimeMillis()));
    }
}
