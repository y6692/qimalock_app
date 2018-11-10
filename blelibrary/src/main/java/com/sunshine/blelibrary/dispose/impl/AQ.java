package com.sunshine.blelibrary.dispose.impl;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 密钥
 * Created by sunshine on 2017/2/20.
 */

public class AQ extends BaseHandler {
    @Override
    protected void handler(String hexString, int state) {
//        byte[] mingwen = ConvertUtils.hexString2Bytes(hexString);
//        Logger.e("AQ","decrypt:"+ ConvertUtils.bytes2HexString(mingwen));
//        Intent intent = new Intent();
//        intent.setAction(Config.AQ_ACTION);
        if (hexString.startsWith("07030101")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.AQ_ACTION,"");
        }else {
//            intent.putExtra("data",hexString);
            GlobalParameterUtils.getInstance().sendBroadcast(Config.AQ_ACTION,hexString);
        }
//        GlobalParameterUtils.getInstance().sendBroadcast(Config.AQ_ACTION,hexString);
    }

    @Override
    protected String action() {
        return "0703";
    }
}
