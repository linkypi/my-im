//package com.hiraeth.im.dispatcher;
//
//import redis.clients.jedis.Jedis;
//
///**
// * @author: leo
// * @description:
// * @ClassName: com.hiraeth.im.dispatcher
// * @date: 2023/11/22 15:51
// */
//public class JedisManager {
//    private JedisManager(){
//    }
//    static class Singleton{
//        private static final JedisManager instance = new JedisManager();
//    }
//    public static JedisManager getInstance(){
//        return Singleton.instance;
//    }
//    private final Jedis jedis = new Jedis("localhost");
//
//    public Jedis getJedis(){
//        return this.jedis;
//    }
//
//}
