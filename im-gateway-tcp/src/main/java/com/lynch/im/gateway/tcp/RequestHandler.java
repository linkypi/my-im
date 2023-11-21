package com.lynch.im.gateway.tcp;

import com.lynch.im.protocol.AuthenticateRequestProto.*;
import com.lynch.im.protocol.AuthenticateResponseProto.*;

/**
 * @author leo
 * @ClassName RequestHandler
 * @description: TODO
 * @date 11/21/23 2:07 PM
 */
public class RequestHandler {
    private RequestHandler(){
    }

    static class Singleton{
        static RequestHandler instance = new RequestHandler();
    }

    public static RequestHandler getInstance(){
        return Singleton.instance;
    }

    public AuthenticateResponse authenticate(AuthenticateRequest request){

    }
}
