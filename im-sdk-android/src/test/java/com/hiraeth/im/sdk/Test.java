package com.hiraeth.im.sdk;

import com.hiraeth.im.sdk.IMClient;

/**
 * @author leo
 * @ClassName Test
 * @description: TODO
 * @date 11/20/23 4:45 PM
 */
public class Test {
    public static void main(String[] args)throws Exception {
        // 首先请求 IM 系统的 IP 地址列表, 随机获取一台机器 IP 进行连接
        IMClient imClient = new IMClient();
        imClient.connectSync("127.0.0.1", 8080);
        imClient.authenticate("test001", "token");
        imClient.sendMessage("test001", "test005" , "this msg is from test001.");

        while (true){
            Thread.sleep(1000);
        }
    }
}
