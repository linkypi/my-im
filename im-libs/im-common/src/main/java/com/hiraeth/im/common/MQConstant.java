package com.hiraeth.im.common;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 13:16
 */
public class MQConstant {
    public static class Topic{
        public static final String CHAT_SENDER_MESSAGE = "CHAT_SENDER_MESSAGE";
        /**
         * 用于将发送者的消息转发给接收者
         */
        public static final String RESPONSE_SENDER_MESSAGE = "RESPONSE_SENDER_MESSAGE";
        public static final String FORWARD_MESSAGE = "FORWARD_MESSAGE";
        public static final String FORWARD_SUCCESS_MESSAGE = "FORWARD_SUCCESS_MESSAGE";
    }

    public static class Group{
        public static final String GROUP_SENDER_MSG = "GROUP_SENDER_MSG";
        public static final String GROUP_FORWARD_MSG = "GROUP_FORWARD_MSG";
        public static final String GROUP_FORWARD_SUCCESS_MSG = "GROUP_FORWARD_SUCCESS_MSG";
        public static final String GROUP_RESPONSE_SENDER_MSG = "GROUP_RESPONSE_SENDER_MSG";
    }

}
