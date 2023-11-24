package com.hiraeth.im.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 22:09
 */
@Setter
@Getter
@AllArgsConstructor
public class Session {
    private String token;
    private long timestamp;
    private Date authenticatedTime;
    private boolean isAuthenticated;
    private String gatewayChannelId;
}
