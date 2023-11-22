package com.hiraeth.im.cache;

import org.springframework.data.redis.connection.MessageListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @Author leo
 * @Date 2023/11/22 17:31
 * @Version 1.0
 */
public interface  IRedisService {
    /**
     *  存入key
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, String value);
    /**
     *  存入key并设置存在时间
     * @param key
     * @param expire 单位为秒
     * @param value
     * @return
     */
    boolean set(String key, long expire, String value);

    boolean set(final String key, final String expire, final String value);

    long publish(final String channel, final String message);

    boolean subscribe(final MessageListener listener, final List<String> channels);

    List<String> lRang(final String key, long start, long end);

    void lTrim(final String key, long start, long end);

    long lRem(final String key, int count, Object obj);

    long llen(final String key);

    /**
     * 取出key
     * @param key
     * @return
     */
    String get(String key);
    /**
     * 设置key的存在时间
     * @param key
     * @param expire
     * @return
     */
    boolean expire(String key, long expire);

    boolean expire(String key, String expire);
    /**
     * 存在对象
     * @param key
     * @param obj
     * @return
     */
    boolean setObj(String key, Object obj);
    /**
     * 取出对象
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    <T>  T getObj(String key, Class<T> clz);
    /**
     * 存入list
     * @param key
     * @param list
     * @param <T>
     * @return
     */
    <T> boolean setList(String key, List<T> list);
    /**
     * 取出list
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    <T> List<T> getList(String key, Class<T> clz);
    /**
     * 将一个或多个值插入到列表头部
     * @param key
     * @param obj
     * @return
     */
    long lpush(String key, Object obj);
    /**
     * 将一个或多个值插入到列表的尾部
     * @param key
     * @param obj
     * @return
     */
    long rpush(String key, Object... obj);
    /**
     * 列表元素出栈
     * @param key
     * @return
     */
    String lpop(String key);
    /**
     * 列表元素出栈
     * @param key
     * @return
     */
    String rpop(String key);

    List<String> blPop(final String key, final int timeout);

    /**
     * 删除缓存<br>
     * 根据key精确匹配删除
     * @param key
     */
    void del(String... key);

    void del(List<String> keys);

    /**
     * 批量删除<br>
     * （该操作会执行模糊查询，请尽量不要使用，以免影响性能或误删）
     * @param pattern
     */
    void batchDel(String... pattern);

    /**
     * 查询存在
     * @param key
     */
    boolean exist(String key);

    boolean isMember(String key, String value);

    Set<String> keys(String key);

    Long incr(String key);

    Long decr(String key);

    /**
     * 将map写入缓存
     * @param key
     * @param map
     */
    <T> void setMap(String key, Map<String, T> map);
    /**
     * 获取map缓存
     * @param key
     * @param clazz
     * @return
     */
    <T> Map<String, T> mget(String key, Class<T> clazz);

//    void sadd(String key, String... value);

    void srem(String key, String... value);

    void srename(String oldkey, String newkey);

    <V> void zsadd(String key, V value, double w);

    Set<?> zsquery(String key, int sort);

    Set<?> zsqueryWithScores(String key, int sort);

    Set<?> zsqueryByScore(String key, int sort, Double min, Double max);

    Set<?> zsqueryByScoreWithScores(String key, int sort, Double min, Double max);

    void zsrem(String key, String... value);

    void zsrename(String oldkey, String newkey);

    Double score(String key, Object value);

    void removeZSetRangeByScore(String key, double s, double e);

    boolean getlock(String lockKey,long expireTime, String value) throws Exception;

    boolean getlock(String lockKey,String expireTime, String value) throws Exception;

    boolean releaseLock(String lockKey, String value) throws Exception;

    <T> T getByFunc(String key, Class<T> cls, Supplier<T> func);

    <T> T getByFunc(String key, Class<T> cls, long expire, Supplier<T> func);

    <T> List<T> getListByFunc(String key, Class<T> clz, Supplier<List<T>> func);

    <T> List<T> getListByFunc(String key, Class<T> clz, long expire, Supplier<List<T>> func);

    <T> List<T> getListByFunc(String key, Class<T> clz, String expire, Supplier<List<T>> func);

    <T> T getByFunc(String key, Class<T> cls,  String expire, Supplier<T> func);

    long getExpire(final String key);

}
