package com.sunshine.blelibrary.dispose;

import android.util.Log;

/**
 * 处理指令基类
 * Created by sunshine on 2017/2/20.
 */

public abstract class BaseHandler {
    /**
     * 下一级处理器
     */
    public BaseHandler nextHandler;

    public final void handlerRequest(String hexString,int state){

        Log.e("handlerRequest===", hexString + "===" + action());

        if (hexString.startsWith(action())){
            Log.e("handlerRequest===1", hexString + "===" + action());

            handler(hexString, state);

        }else {
            Log.e("handlerRequest===2", hexString + "===" + action());

            nextHandler.handlerRequest(hexString, state);
        }
    }
    /**
     * 处理指令
     * @param hexString 接收到的指令
     */
    protected abstract void handler(String hexString, int state);
    /**
     * 自身的指令头
     * @return 指令头
     */
    protected abstract String action();

}
