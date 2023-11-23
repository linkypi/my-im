package com.hiraeth.im.business.service;

import com.hiraeth.im.business.entity.MessageSend;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 15:21
 */
public interface IMessageSendService {
    boolean save(MessageSend model);
}
