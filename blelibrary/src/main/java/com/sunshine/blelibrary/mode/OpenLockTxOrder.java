package com.sunshine.blelibrary.mode;

import android.util.Log;

import com.sunshine.blelibrary.config.Config;

/**
 * 开锁指令
 * Created by sunshine on 2017/2/24.
 */

public class OpenLockTxOrder extends TxOrder {

    public OpenLockTxOrder() {
        super(TYPE.OPEN_LOCK);

        byte[] bytes = {0x06, Config.passwordnew[0], Config.passwordnew[1], Config.passwordnew[2], Config.passwordnew[3], Config.passwordnew[4], Config.passwordnew[5]};

        add(bytes);

    }

    public OpenLockTxOrder(boolean isChangePsd) {
        super(TYPE.OPEN_LOCK);

//        byte[] bytes = new byte[7]{};

        if (isChangePsd){
            byte[] bytes = {0x06, Config.passwordnew[0], Config.passwordnew[1], Config.passwordnew[2], Config.passwordnew[3], Config.passwordnew[4], Config.passwordnew[5]};

            add(bytes);
        }else{
            byte[] bytes = {0x06, Config.password[0], Config.password[1], Config.password[2], Config.password[3], Config.password[4], Config.password[5]};

            add(bytes);
        }



//        }else {
//            add(bytes);
//        }
    }
}
