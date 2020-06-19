package com.sunshine.blelibrary.mode;

/**
 * 获取电量
 * Created by sunshine on 2017/2/24.
 */

public class Battery2TxOrder extends TxOrder {

    public Battery2TxOrder() {
        super(TYPE.GET_BATTERY);
        add(new byte[]{0x01,0x02});
    }
}
