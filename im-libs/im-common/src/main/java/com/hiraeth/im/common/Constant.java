package com.hiraeth.im.common;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/21 20:56
 */
public class Constant {
    public static final int HEADER_LENGTH = 19;
    public static final int APP_SDK_VERSION = 1;
    public static final byte[] DELIMITER = "$_".getBytes();

    public static final String SESSIONS_KEY_PREFIX = "SESSIONS:";
    public static final String OFFLINE_MSG = "OFFLINE_MSG:";
}
