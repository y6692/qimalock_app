package com.sunshine.blelibrary.mode;

import android.util.Log;

public class UpddateVersionTxOrder extends TxOrder {

    public UpddateVersionTxOrder() {
        super(TYPE.UPDATE_VERSION);
        add(new byte[]{0x01,0x01});
    }
}
