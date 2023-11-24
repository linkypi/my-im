package com.hiraeth.im.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiraeth.im.business.entity.MessageReceive;
import com.hiraeth.im.business.entity.MessageSend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 15:20
 */
@Mapper
public interface MessageReceiveMapper extends BaseMapper<MessageReceive> {

    @Update("update message_receive set is_delivered = 1 where deleted = 0 and message_id = #{messageId}")
    boolean updateDelivered(@Param("messageId")long messageId);
}
