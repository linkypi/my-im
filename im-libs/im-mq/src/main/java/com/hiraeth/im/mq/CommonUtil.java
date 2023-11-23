package com.hiraeth.im.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hiraeth.im.common.util.StringUtil;

/**
 * Description
 * Created by linxueqi on 2021/4/29 9:41
 */
public class CommonUtil {

    private final static int STR_MAX_LENGTH = 1000;

    private CommonUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> String getShortStr(T msg) {
        if (msg == null) {
            return "";
        }
        String json = "";
        if (msg.getClass().isArray()) {
            json = JSON.toJSONString(new String((byte[]) msg), SerializerFeature.WriteDateUseDateFormat);
        } else {
            json = JSON.toJSONString(msg, SerializerFeature.WriteDateUseDateFormat);
        }
        return StringUtil.isNotEmpty(json) && json.length() > STR_MAX_LENGTH ? json.substring(0, STR_MAX_LENGTH - 3) + "..." : json;
    }
}
