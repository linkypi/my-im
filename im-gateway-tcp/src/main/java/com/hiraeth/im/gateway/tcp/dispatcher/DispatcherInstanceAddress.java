package com.hiraeth.im.gateway.tcp.dispatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/20 23:10
 */
@Getter
@Setter
@AllArgsConstructor
public class DispatcherInstanceAddress {
    private String host;
    private String ip;
    private int port;
}
