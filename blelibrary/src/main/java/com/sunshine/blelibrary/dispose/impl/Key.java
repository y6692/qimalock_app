package com.sunshine.blelibrary.dispose.impl;

import android.util.Log;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 开锁指令
 * Created by sunshine on 2017/2/20.
 */

public class Key extends BaseHandler{
    @Override
    protected void handler(String hexString, int state) {
        if ("07030101".startsWith(hexString)){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.KEY_ACTION,"");
        }else {
            GlobalParameterUtils.getInstance().sendBroadcast(Config.KEY_ACTION,hexString);
        }
    }
    @Override
    protected String action() {
        return "0703";
    }
}
