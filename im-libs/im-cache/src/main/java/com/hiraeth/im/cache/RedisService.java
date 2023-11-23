package com.hiraeth.im.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hiraeth.im.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author leo
 * @Date 2023/11/22 17:31
 * @Version 1.0
 */
@Service
public class RedisService implements IRedisService {

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    private long defaultExpireTime;

    @Value("${default.cache-expire:30m}")
    public void setDefaultExpireTime(String defaultExpireTimeStr) {
        this.defaultExpireTime = getExpireTimeByTimeStr(defaultExpireTimeStr);
    }

    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * \d+(\.\d+)? 匹配整数或小数
     * TIME_PATTERN正则表达式过长，由于不是检测重复出现的单词故不可使用反向引用
     */
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "((?<day>\\d+(\\.\\d+)?)(?<dayUnit>d))?" +       // 匹配天数 day
            "((?<hour>\\d+(\\.\\d+)?)(?<hourUnit>h))?" +     // 匹配小时 hour
            "((?<minute>\\d+(\\.\\d+)?)(?<minuteUnit>m))?" + // 匹配分钟 minute
            "((?<second>\\d+(\\.\\d+)?)(?<secondUnit>s?))?"  // 匹配秒数 second
    );

    private static final HashMap<String,Long> TIME_UNIT_CACHE = new HashMap<String,Long>(){
        {
            put("d", ONE_DAY);
            put("h", ONE_HOUR);
            put("m", ONE_MINUTE);
            put("s", 1L);
        }
    };

    public static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 存入key
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean set(final String key, final String value) {

        boolean result = execute((connection,serializer)-> {
            return connection.set(serializer.serialize(key), serializer.serialize(value));
        });
        return result;
    }

    /**
     * 存入key并设置存在时间
     *
     * @param key
     * @param expire seconds
     * @param value
     * @return
     */
    @Override
    public boolean set(final String key, final long expire, final String value) {
        boolean result = execute((connection,serializer)-> {
            return connection.setEx(serializer.serialize(key), expire, serializer.serialize(value));
        });

        return result;
    }

    /**
     * 发布消息
     * @param channel
     * @param message
     * @return
     */
    @Override
    public long publish(final String channel, final String message) {
        long result = execute((connection,serializer)-> {
            return connection.publish(serializer.serialize(channel), serializer.serialize(message));
        });

        return result;
    }

    /**
     * 发布消息
     * @param listener
     * @param channels
     * @return
     */
    @Override
    public boolean subscribe(final MessageListener listener, final List<String> channels) {

        if (CollectionUtils.isEmpty(channels)) {
            return false;
        }
        byte[][] buffer = new byte[channels.size()][];
        for (String item : channels) {
            buffer[0] = item.getBytes();
        }

        execute((connection) -> {
            connection.subscribe(listener, buffer);
        });
        return true;
    }

    @Override
    public boolean set(final String key, final String expire, final String value) {
        long timeSpan = getExpireTimeByTimeStr(expire);
        boolean result = execute((connection,serializer)-> {
            return connection.setEx(serializer.serialize(key), timeSpan, serializer.serialize(value));
        });

        return result;
    }

    /**
     * 取出key
     *
     * @param key
     * @return
     */
    @Override
    public String get(final String key) {
        String result = execute((connection,serializer)-> {
            byte[] value = connection.get(serializer.serialize(key));
            return serializer.deserialize(value);
        });
        return result;
    }

    /**
     * 设置key的存在时间
     *
     * @param key
     * @param expire
     * @return
     */
    @Override
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean expire(final String key, String expire) {
        long timeSpan = getExpireTimeByTimeStr(expire);
        return redisTemplate.expire(key, timeSpan, TimeUnit.SECONDS);
    }

    /**
     * 存在对象
     *
     * @param key
     * @param obj
     * @return
     */
    @Override
    public boolean setObj(String key, Object obj) {
        String value = JSONObject.toJSONString(obj);
        return set(key, value);
    }

    /**
     * 取出对象
     *
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getObj(String key, Class<T> clz) {
        String json = get(key);
        if (json != null) {
            T object = JSONObject.parseObject(json, clz);
            return object;
        }
        return null;
    }

    /**
     * 存入list
     *
     * @param key
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> boolean setList(String key, List<T> list) {
        String value = JSONObject.toJSONString(list);
        return set(key, value);
    }

    /**
     * 取出list
     *
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> getList(String key, Class<T> clz) {
        String json = get(key);
        if (json != null) {
            List<T> list = JSONObject.parseArray(json, clz);
            return list;
        }
        return null;
    }

    /**
     * 获取队列指定区间的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<String> lRang(final String key, long start, long end) {
        List<String> list = execute((connection, serializer) -> {
            final List<byte[]> bytes = connection.lRange(serializer.serialize(key), start, end);
            List<String> result = new ArrayList<>();
            for (byte[] item : bytes) {
                result.add(serializer.deserialize(item));
            }

            return result;
        });
        return list;
    }

    /**
     * 保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * @param key
     * @param start
     * @param end
     */
    @Override
    public void lTrim(final String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    @Override
    public long llen(final String key) {
        long result = execute((connection,serializer)-> connection.lLen(serializer.serialize(key)));
        return result;
    }

    @Override
    public long lRem(final String key, int count, Object obj) {
        final String value = JSONObject.toJSONString(obj);
        long result = execute((connection,serializer)->
                connection.lRem(serializer.serialize(key), count, serializer.serialize(value)));
        return result;
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key
     * @param obj
     * @return
     */
    @Override
    public long lpush(final String key, Object obj) {
        final String value = JSONObject.toJSONString(obj);
        long result = execute((connection,serializer)-> connection.lPush(serializer.serialize(key), serializer.serialize(value)));
        return result;
    }

    /**
     * 将一个或多个值插入到列表的尾部
     *
     * @param key
     * @param obj
     * @return
     */
    @Override
    public long rpush(final String key, Object... obj) {
        final byte[][] rawValues = new byte[obj.length][];
        int i = 0;
        for (Object value : obj) {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            rawValues[i++] = serializer.serialize(JSONObject.toJSONString(value));
        }
        long result = execute((connection,serializer)-> connection.rPush(serializer.serialize(key), rawValues));
        return result;
    }

    /**
     * 列表元素出栈
     *
     * @param key
     * @return
     */
    @Override
    public String lpop(final String key) {
        String result = execute((connection,serializer)-> {
            byte[] res = connection.lPop(serializer.serialize(key));
            return serializer.deserialize(res);
        });
        return result;
    }

    /**
     * 列表元素出栈
     *
     * @param key
     * @return
     */
    @Override
    public String rpop(final String key) {
        String result = execute((connection,serializer)-> {
            byte[] res = connection.rPop(serializer.serialize(key));
            return serializer.deserialize(res);
        });
        return result;
    }

    /**
     * 列表元素出栈，队列为空时堵塞直到队列有元素
     * @param key
     * @param timeout
     * @return
     */
    @Override
    public List<String> blPop(final String key, final int timeout) {
        List<String> result = execute((connection, serializer) -> {
            List<byte[]> res = connection.bLPop(timeout, serializer.serialize(key));
            List<String> list = new ArrayList<>();
            for (byte[] item : res) {
                list.add(serializer.deserialize(item));
            }
            return list;
        });
        return result;
    }

    /**
     * 删除缓存<br>
     * 根据key精确匹配删除
     *
     * @param key
     */
    @Override
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                List<?> list = CollectionUtils.arrayToList(key);
                redisTemplate.delete((Collection<String>) list);
            }
        }
    }

    @Override
    public void del(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    /**
     * 批量删除<br>
     * （该操作会执行模糊查询，请尽量不要使用，以免影响性能或误删）
     *
     * @param pattern
     */
    @Override
    public void batchDel(String... pattern) {
        for (String kp : pattern) {
            redisTemplate.delete(redisTemplate.keys(kp + "*"));
        }
    }

    @Override
    public boolean exist(final String key) {
        boolean result = execute((connection,serializer)-> connection.exists(serializer.serialize(key)));
        return result;
    }

    @Override
    public boolean isMember(final String key, final String value) {
        boolean result = execute((connection,serializer)-> connection.sIsMember(serializer.serialize(key), serializer.serialize(value)));
        return result;
    }

    private <T> T execute(final BiFunction<RedisConnection,RedisSerializer<String>,T> callback){
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        return redisTemplate.execute((RedisCallback<T>) connection -> {
            return callback.apply(connection, serializer);
        });
    }

    private void execute(final Consumer<RedisConnection> callback){
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.execute((RedisCallback) connection -> {
            callback.accept(connection);
            return null;
        });
    }

    @Override
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    @Override
    public Long incr(final String key) {
        Long result = execute((connection,serializer)-> connection.incr(serializer.serialize(key)));
        return result;
    }

    @Override
    public Long decr(final String key) {
        Long result = execute((connection,serializer)-> connection.decr(serializer.serialize(key)));
        return result;
    }

    /**
     * 将map写入缓存
     *
     * @param key
     * @param map
     */
    @Override
    public <T> void setMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取map缓存
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> Map<String, T> mget(String key, Class<T> clazz) {
        BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(key);
        return boundHashOperations.entries();
    }


    /**
     * 添加set
     * @param key
     * @param value
     */
//    @Override
//    public void sadd(String key, String... value) {
//        redisTemplate.boundSetOps(key).add(value);
//    }

    /**
     * 删除set集合中的对象
     *
     * @param key
     * @param value
     */
    @Override
    public void srem(String key, String... value) {
        redisTemplate.boundSetOps(key).remove(value);
    }

    /**
     * set重命名
     *
     * @param oldkey
     * @param newkey
     */
    @Override
    public void srename(String oldkey, String newkey) {
        redisTemplate.boundSetOps(oldkey).rename(newkey);
    }

    /**
     * 添加set
     *
     * @param key
     * @param value
     */
    @Override
    public <V> void zsadd(String key, V value, double w) {
        BoundZSetOperations ops = redisTemplate.boundZSetOps(key);
        ops.add(value, w);
    }

    /**
     * 查询set集合中的对象
     *
     * @param key
     * @param sort 0:正序 1倒叙
     */
    @Override
    public Set<?> zsquery(String key, int sort) {
        if (sort == 0) {
            return redisTemplate.boundZSetOps(key).range(0, -1);
        } else if (sort == 1) {
            return redisTemplate.boundZSetOps(key).reverseRange(0, -1);
        } else {
            return null;
        }
    }

    /**
     * 查询set集合中的对象
     *
     * @param key
     * @param sort 0:正序 1倒叙
     */
    @Override
    public Set<?> zsqueryWithScores(String key, int sort) {
        if (sort == 0) {
            return redisTemplate.boundZSetOps(key).rangeWithScores(0, -1);
        } else if (sort == 1) {
            return redisTemplate.boundZSetOps(key).reverseRangeWithScores(0, -1);
        } else {
            return null;
        }
    }

    @Override
    public Set<?> zsqueryByScore(String key, int sort, Double min, Double max) {
        if (sort == 0) {
            return redisTemplate.boundZSetOps(key).rangeByScore(min, max);
        } else if (sort == 1) {
            return redisTemplate.boundZSetOps(key).reverseRangeByScore(min, max);
        } else {
            return null;
        }
    }

    @Override
    public Set<?> zsqueryByScoreWithScores(String key, int sort, Double min, Double max) {
        if (sort == 0) {
            return redisTemplate.boundZSetOps(key).rangeByScoreWithScores(min, max);
        } else if (sort == 1) {
            return redisTemplate.boundZSetOps(key).reverseRangeByScoreWithScores(min, max);
        } else {
            return null;
        }
    }

    /**
     * 删除set集合中的对象
     *
     * @param key
     * @param value
     */
    @Override
    public void zsrem(String key, String... value) {
        redisTemplate.boundZSetOps(key).remove(value);
    }

    /**
     * zset重命名
     *
     * @param oldkey
     * @param newkey
     */
    @Override
    public void zsrename(String oldkey, String newkey) {
        redisTemplate.boundZSetOps(oldkey).rename(newkey);
    }

    /**
     * 获取对应分数
     */
    @Override
    public Double score(String key, Object value) {
        return redisTemplate.boundZSetOps(key).score(value);
    }

    /**
     * 通过分数删除ZSet中的值
     */
    @Override
    public void removeZSetRangeByScore(String key, double s, double e) {
        redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }


    /**
     * 加锁
     *
     * @param lockKey
     * @param expireTime
     * @return
     */
    @Override
    public boolean getlock(String lockKey, long expireTime, String value) throws Exception {
        return getLockInternal(lockKey,expireTime,value);
    }

    @Override
    public boolean getlock(String lockKey, String expireTime, String value) throws Exception {
        long timeSpan = getExpireTimeByTimeStr(expireTime);
        return getLockInternal(lockKey, timeSpan, value);
    }

    private boolean getLockInternal(String lockKey, long expireTime, String value) throws Exception {
        try {
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                //String uuid = UUID.randomUUID().toString();
                SetParams setParams = new SetParams();
                setParams.px(expireTime);
                setParams.nx();
                return commands.set(lockKey, value,setParams);
            };
            String result = redisTemplate.execute(callback);
            return !StringUtils.isEmpty(result);
        }catch (Exception e){
            throw new Exception("redis获取锁失败，失败原因："+e.getMessage());
        }
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param value 请求标识
     * @return 是否释放成功
     */
    @Override
    public boolean releaseLock(String lockKey, String value) throws Exception {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(lockKey);
            List<String> args = new ArrayList<>();
            args.add(value);

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            };
            Long result = redisTemplate.execute(callback);

            return result != null && result > 0;
        } catch (Exception e) {
            //logger.error("release lock occured an exception", e);
            throw new Exception("redis释放锁失败，失败原因："+e.getMessage());
        }
    }

    /**
     * 通过缓存获取数据，获取不到则通过函数获取
     * 缓存默认保存 defaultExpireTime 秒，该时间可通过配置项 default.defaultExpireTime 配置
     * 若通过函数获取不到缓存则不会对结果缓存
     * @param key
     * @param func
     * @return
     */
    @Override
    public  <T> T getByFunc(String key, Class<T> cls,  Supplier<T> func){
        return getByFuncInternal(key, cls, 0, func);
    }

    @Override
    public <T> T getByFunc(String key, Class<T> cls,  long expire, Supplier<T> func){
        return getByFuncInternal(key, cls, expire, func);
    }

    @Override
    public <T> T getByFunc(String key, Class<T> cls,  String expire, Supplier<T> func){
        long expireTime = getExpireTimeByTimeStr(expire);
        return getByFuncInternal(key, cls, expireTime, func);
    }

    private  <T> T getByFuncInternal(String key, Class<T> cls, long expire, Supplier<T> func) {
        T result = getObj(key, cls);
        if (StringUtils.isEmpty(result) || (result instanceof BigDecimal && ((BigDecimal) result).doubleValue() == 0)) {
            T temp = func.get();
            if (!StringUtils.isEmpty(temp)) {
                boolean needCache = true;
                if (result instanceof BigDecimal && ((BigDecimal) result).doubleValue() == 0) {
                    needCache = false;
                }

                if (needCache) {
                    if (expire <= 0) {
                        expire = this.defaultExpireTime;
                    }
                    set(key, expire, JSON.toJSONString(temp));
                }
            }

            return temp;
        }
        return result;
    }

    /**
     * 通过缓存获取数据，获取不到则通过函数获取
     * 缓存默认保存 defaultExpireTime 秒，该时间可通过配置项 default.defaultExpireTime 配置
     * 若通过函数获取不到缓存则不会对结果缓存
     * @param key
     * @param clz
     * @param func
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> getListByFunc(String key, Class<T> clz, Supplier<List<T>> func) {
        return getListByFuncInternal(key,clz,0, func);
    }

    /**
     *
     * @param key
     * @param clz
     * @param expire  过期时间，单位秒
     * @param func
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> getListByFunc(String key, Class<T> clz, long expire, Supplier<List<T>> func) {
        return getListByFuncInternal(key, clz, expire, func);
    }

    @Override
    public <T> List<T> getListByFunc(String key, Class<T> clz, String expire, Supplier<List<T>> func) {
        long expireTime = getExpireTimeByTimeStr(expire);
        return getListByFuncInternal(key, clz, expireTime, func);
    }

    /**
     * 获取key剩余存活时间
     *
     * @param key
     * @return
     */
    @Override
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    private <T> List<T> getListByFuncInternal(String key, Class<T> clz, long expire, Supplier<List<T>> func){
        List<T> result = getList(key, clz);
        if (null == result || result.size() == 0) {
            List<T> temp = func.get();
            if(null == temp || temp.size() == 0){
                return null;
            }
            setObj(key, temp);
            if(expire <= 0){
                expire = this.defaultExpireTime;
            }
            expire(key, expire);
            return temp;
        }
        return result;
    }

    /**
     * 根据时间字符串获取指定秒数   // d 天 h 时 m 分 s 秒
     * 如 ： 2h3m、3m10s、1.5h、2.5m、30s
     * @param time
     * @return
     */
    private long getExpireTimeByTimeStr(String time) {
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            throw new RuntimeException("redis parse expire time take error: " + time);
        }

        // parse day
        long expireTime = parseTime(matcher, "day");
        // parse hour
        expireTime += parseTime(matcher, "hour");
        // parse minute
        expireTime += parseTime(matcher, "minute");
        // parse second
        expireTime += parseTime(matcher, "second");

        return expireTime;
    }

    private long parseTime(Matcher matcher, String digitGroup) {
        long expireTime = 0;
        String digit = matcher.group(digitGroup);
        String unit = matcher.group(digitGroup + "Unit");
        if (!StringUtil.isNullOrEmpty(digit)) {
            if (StringUtil.isNullOrEmpty(unit)) {
                unit = "s";
            }
            if (!TIME_UNIT_CACHE.containsKey(unit)) {
                throw new RuntimeException("redis expire time syntax not support unit: " + unit);
            }
            expireTime += TIME_UNIT_CACHE.get(unit) * Double.parseDouble(digit);
        }
        return expireTime;
    }
}
