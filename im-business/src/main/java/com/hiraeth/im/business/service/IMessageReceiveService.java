package com.hiraeth.im.business.service;

import com.hiraeth.im.business.entity.MessageReceive;

import java.util.List;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 15:21
 */
public interface IMessageReceiveService {
    boolean save(MessageReceive model);
    boolean batchSave(List<MessageReceive> models);
}
