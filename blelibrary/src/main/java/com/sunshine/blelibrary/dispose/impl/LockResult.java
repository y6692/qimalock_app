package com.sunshine.blelibrary.dispose.impl;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 关锁
 * Created by sunshine on 2017/2/20.
 */

public class LockResult extends BaseHandler {
    @Override
    protected void handler(String hexString, int state) {
//        byte[] mingwen = ConvertUtils.hexString2Bytes(hexString);
//        Logger.e("close lock","decrypt:"+ ConvertUtils.bytes2HexString(mingwen));
//        Intent intent = new Intent();
//        intent.setAction(Config.LOCK_RESULT);
        if (hexString.startsWith("05080101")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.LOCK_RESULT,"");
        }else {
//            intent.putExtra("data",ConvertUtils.bytes2HexString(mingwen));
            GlobalParameterUtils.getInstance().sendBroadcast(Config.LOCK_RESULT,hexString);
        }

//        GlobalParameterUtils.getInstance().getContext().sendBroadcast(intent);
    }

    @Override
    protected String action() {
        return "0508";
    }
}
