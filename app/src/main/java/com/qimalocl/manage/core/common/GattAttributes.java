package com.qimalocl.manage.core.common;

import android.os.ParcelUuid;

import java.util.UUID;

/**
 * Created by Administrator on 2017/2/25 0025.
 */

public class GattAttributes {

    public static final ParcelUuid findServerUUID = ParcelUuid.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    public static final UUID OAD_SERVICE_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");

    public static final UUID bltServerUUID = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    public static final UUID readDataUUID = UUID.fromString("000036f6-0000-1000-8000-00805f9b34fb");

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID writeDataUUID = UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");
}
