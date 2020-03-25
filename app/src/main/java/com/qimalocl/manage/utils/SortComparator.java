package com.qimalocl.manage.utils;


import com.clj.fastble.data.BleDevice;

import java.util.Comparator;

/**
 * 排序
 * Created by sunshine on 2017/2/21.
 */

public class SortComparator implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        BleDevice a = (BleDevice) o;
        BleDevice b = (BleDevice) t1;

        return (b.getRssi()- a.getRssi());
    }
}
