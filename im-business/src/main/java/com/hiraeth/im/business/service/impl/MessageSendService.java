package com.hiraeth.im.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hiraeth.im.business.entity.MessageSend;
import com.hiraeth.im.business.mapper.MessageSendMapper;
import com.hiraeth.im.business.service.IMessageSendService;
import org.springframework.stereotype.Service;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 15:21
 */
@Service
public class MessageSendService extends ServiceImpl<MessageSendMapper, MessageSend> implements IMessageSendService {

    @Override
    public boolean save(MessageSend entity) {
        return super.save(entity);
    }
}
