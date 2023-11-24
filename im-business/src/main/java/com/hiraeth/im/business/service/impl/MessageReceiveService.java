package com.hiraeth.im.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hiraeth.im.business.entity.MessageReceive;
import com.hiraeth.im.business.entity.MessageSend;
import com.hiraeth.im.business.mapper.MessageReceiveMapper;
import com.hiraeth.im.business.mapper.MessageSendMapper;
import com.hiraeth.im.business.service.IMessageReceiveService;
import com.hiraeth.im.business.service.IMessageSendService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 15:21
 */
@Service
public class MessageReceiveService extends ServiceImpl<MessageReceiveMapper, MessageReceive> implements IMessageReceiveService {

    @Override
    public boolean setDeliveredSuccess(long messageId) {
        return baseMapper.updateDelivered(messageId);
    }

    @Override
    public boolean save(MessageReceive entity) {
        return super.save(entity);
    }

    @Override
    public boolean batchSave(List<MessageReceive> models) {
        return super.saveBatch(models);
    }
}
