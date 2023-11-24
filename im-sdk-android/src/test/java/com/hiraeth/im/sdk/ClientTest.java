package com.hiraeth.im.sdk;

/**
 * @author leo
 * @ClassName Test
 * @description: TODO
 * @date 11/20/23 4:45 PM
 */
public class ClientTest {
    @org.junit.Test
    public void test1() throws InterruptedException {
        IMClient imClient = new IMClient();
        imClient.connectSync("127.0.0.1", 8080);
        imClient.authenticate("test001", "token");
        imClient.sendMessage("test001", "test005" , "宇宙第一.");

        while (true){
            Thread.sleep(1000);
        }
    }

    @org.junit.Test
    public void test2() throws InterruptedException {
        IMClient imClient = new IMClient();
        imClient.connectSync("127.0.0.1", 8080);
        imClient.authenticate("test005", "token");
//        imClient.sendMessage("test001", "test005" , "this msg is from test001.");

        while (true){
            Thread.sleep(1000);
        }
    }
}
