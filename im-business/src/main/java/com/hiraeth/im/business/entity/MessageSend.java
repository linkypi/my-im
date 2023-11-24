package com.hiraeth.im.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hiraeth.im.orm.entity.BaseModel;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author: linxueqi
 * @description:
 * @date: 2023/11/23 15:10
 */
@Getter
@Setter
@TableName("message_send")
public class MessageSend extends BaseModel {

    /**
     * 管理员ID
     */
    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    /**
     *
     */
    @TableField(value = "group_id")
    private long groupId;

    /**
     *
     */
    @TableField(value = "sender_id")
    private String senderId;

    /**
     *
     */
    @TableField(value = "receiver_id")
    private String receiverId;

    /**
     *
     */
    @TableField(value = "message_type")
    private int messageType;

    @TableField(value = "request_type")
    private int requestType;

    /**
     * GROUP / SINGLE
     */
    @TableField(value = "chat_type")
    private String chatType;

    /**
     * 'TEXT','IMAGE_AND_TEXT','IMAGE','VOICE','VIDEO','EMOJI
     */
    @TableField(value = "media_type")
    private String mediaType;

    /**
     *
     */
    @TableField(value = "sequence")
    private long sequence;

    /**
     *
     */
    @TableField(value = "content")
    private String content;

    /**
     *
     */
    @TableField(value = "file_path")
    private String filePath;

}
