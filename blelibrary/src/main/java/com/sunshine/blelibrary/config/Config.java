package com.sunshine.blelibrary.config;

import android.content.IntentFilter;

import java.util.UUID;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:26
 * 邮箱：44493547@qq.com
 * 备注：
 */
public class Config {

    public static final String NOT_SUPPORTED = "com.sunshine.blelibrary.config.not_supported";
    public static final int OBJECT_EMPTY = -1;
    public static final int DISCONNECT = 0;

    public static final UUID bltServerUUID = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
//    public static final UUID bltServerUUID = UUID.fromString("0000fbca-0000-1000-8000-00805f9b34fb");
    public static final UUID readDataUUID = UUID.fromString("000036f6-0000-1000-8000-00805f9b34fb");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID writeDataUUID = UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");
    public static final UUID OAD_SERVICE_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
    public static final UUID OAD_READ_UUID = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    public static final UUID OAD_WRITE_UUID = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");



    /**
     * 马蹄锁
     */
//    public static byte[] key = {(byte)0x96, (byte)0x9D, (byte)0xf4, 0x55, (byte)0xd5, (byte)0xf7, (byte)0xfe, (byte)0xea, 0x29, (byte)0xc7, (byte)0xdc, 0x7f, (byte)0xfb, 0x7d, 0x11, 0x4a}; //新锁原始密钥

    public static byte[] key = {32,87,47,82,54,75,63,71,48,80,65,88,17,99,45,43};   //物联锁原始密钥
//  十六进制：20 57 2f 52 36 4b 3f 47 30 50 41 58 11 63 2d 2b

//    public static byte[] key = {32,87,47,82,54,75,63,71,45,75,60,86,10,90,40,42};
//    public static byte[] newkey = {32,87,47,82,54,75,63,71,48,80,65,88,17,99,45,43};
//    public static byte[] newKey = {74,50,90,95,30,21,61,17,80,46,99,94,4,73,1,15};
//    public static byte[] newKey = {51,30,65,57,44,45,22,71,35,63,23,82,36,96,76,11};
    public static byte[] password = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};   //物联锁原始密码

    /**
     * 新的密钥
     * */
    public static byte[] newKey = {30,85,45,80,52,73,60,70,45,75,60,86,10,90,40,42};   //7MA密钥

    public static byte[] newKey2 = {30,85,45,80,52,73,60,70,45,75,60,86,10,90,40,42};
//    public static byte[] key = {36,87,48,82,54,75,26,71,48,80,65,88,12,99,45,23};
    /**
     * 圆形锁
     */
//    public static byte[] yx_key = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};


//    public static byte[] passwordnew = {0x4D, 0x47, 0x22, 0x3F, 0x38, 0x3B};
//    public static byte[] passwordnew = {0x28, 0x5, 0x55, 0x28, 0x58, 0x49};  //40,5,85,40,88,73
    public static byte[] passwordnew = {0x32, 0x30, 0x31, 0x37, 0x31, 0x35};    //7MA密码
//    public static byte[] passwordnew = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};


//    public static byte[] passwordnew = {0x32, 0x30, 0x31, 0x37, 0x31, 0x35};

    public static final String TOKEN_ACTION = "com.sunshine.blelibrary.config.token_action";
    public static final String BATTERY_ACTION = "com.sunshine.blelibrary.config.battery_action";
    public static final String OPEN_ACTION = "com.sunshine.blelibrary.config.open_action";
    public static final String CLOSE_ACTION = "com.sunshine.blelibrary.config.close_action";
    public static final String LOCK_STATUS_ACTION = "com.sunshine.blelibrary.config.lock_status_action";
    public static final String PASSWORD_ACTION = "com.sunshine.blelibrary.config.password_action";
    public static final String AQ_ACTION = "com.sunshine.blelibrary.config.aq_action";
    public static final String SCAN_QR_ACTION = "com.sunshine.blelibrary.config.scan_qr_action";
    public static final String RESET_ACTION = "com.sunshine.blelibrary.config.reset_action";
    public static final String LOCK_RESULT = "com.sunshine.blelibrary.config.lock_result_action";
    public static final String SEND_AQ_ACTION = "com.sunshine.blelibrary.config.SEND_AQ_ACTION";
    public static final String UPDATE_VERSION_ACTION ="com.sunshine.blelibrary.config.UPDATE_VERSION_ACTION";
    public static final String KEY_ACTION ="com.sunshine.blelibrary.config.KEY_ACTION ";
    public static final String XINBIAO_ACTION ="com.sunshine.blelibrary.config.xinbiao_ACTION ";

    public static final String UPDATE_NEXT = "com.sunshine.blelibrary.config.UPDATE_NEXT";;
    public static IntentFilter initFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TOKEN_ACTION);
        intentFilter.addAction(BATTERY_ACTION);
        intentFilter.addAction(OPEN_ACTION);
        intentFilter.addAction(CLOSE_ACTION);
        intentFilter.addAction(LOCK_STATUS_ACTION);
        intentFilter.addAction(PASSWORD_ACTION);
        intentFilter.addAction(AQ_ACTION);
        intentFilter.addAction(SCAN_QR_ACTION);
        intentFilter.addAction(RESET_ACTION);
        intentFilter.addAction(LOCK_RESULT);
        intentFilter.addAction(SEND_AQ_ACTION);
        intentFilter.addAction(UPDATE_VERSION_ACTION);
        intentFilter.addAction(KEY_ACTION);
        intentFilter.addAction(UPDATE_NEXT);
        intentFilter.addAction(XINBIAO_ACTION);
        return intentFilter;
    }
}

