package com.qimalocl.manage.activity;

import com.qimalocl.manage.base.BaseApplication;
import com.sofi.blelocker.library.BluetoothClient;
import com.sofi.blelocker.library.IBluetoothClient;


/**
 * Created by heyong on 2017/5/19.
 */

public class ClientManager {

    private static IBluetoothClient mClient;

    public static IBluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(BaseApplication.getInstance());
                }
            }
        }
        return mClient;
    }

}
