package com.hiraeth.im.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: leo
 * @description:
 * @ClassName: com.lynch.im.common
 * @date: 2023/11/22 12:29
 */
@Getter
@Setter
public class Message {

    private MediaType mediaType;
    private String text;
    /**
     * 语音/图片/视频相关链接
     */
    private String filePath;

    public Message(String text){
        this.mediaType = MediaType.Text;
        this.text = text;
    }

    public Message(MediaType mediaType, String filePath){
        this.mediaType = mediaType;
        this.filePath = filePath;
    }

    public enum MediaType{
        /**
         * 纯文本 / 文本 + 简单预装表情
         */
        Text,
        /**
         * 图文
         */
        ImageAndText,
        /**
         * 语音
         */
        Voice,
        /**
         * 图片
         */
        Image,
        /**
         * 视频
         */
        Video,
        /**
         * 表情
         */
        Emoji;
    }
}
