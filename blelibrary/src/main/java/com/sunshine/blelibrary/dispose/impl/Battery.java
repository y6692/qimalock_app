package com.sunshine.blelibrary.dispose.impl;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 电量
 * Created by sunshine on 2017/2/20.
 */

public class Battery extends BaseHandler {
    @Override
    protected void handler(String hexString, int state) {
//        byte[] mingwen = ConvertUtils.hexString2Bytes(hexString);
//        Logger.e("battery","decrypt:"+ ConvertUtils.bytes2HexString(mingwen));
//        Intent intent = new Intent();
//        intent.setAction(Config.BATTERY_ACTION);
        if (hexString.startsWith("020201ff")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.BATTERY_ACTION,"");
        }else {
            String battery = hexString.substring(6, 8);
//            intent.putExtra("data",battery);
            GlobalParameterUtils.getInstance().sendBroadcast(Config.BATTERY_ACTION,battery);
        }
//        GlobalParameterUtils.getInstance().getContext().sendBroadcast(intent);
    }

    @Override
    protected String action() {
        return "0202";
    }
}
