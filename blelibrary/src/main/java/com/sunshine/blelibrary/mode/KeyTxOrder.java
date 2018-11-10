package com.sunshine.blelibrary.mode;

import android.util.Log;

import java.util.Arrays;

/**
 * 修改密码
 * Created by sunshine on 2017/2/24.
 */

public class KeyTxOrder extends TxOrder {
    /**
     * @param type 指令类型
     */
    public KeyTxOrder(TYPE type, byte[] bytes) {
        super(type);
        add(0, (byte)0x08);
        add(bytes);
    }
}
