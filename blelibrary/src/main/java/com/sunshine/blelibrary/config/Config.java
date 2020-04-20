package com.sunshine.blelibrary.config;

import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;
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

    public static Map<String, String> keyMap = new HashMap();

    static {
        keyMap.put("022102392757", "5651232146772A5762B11B5E777F2960");
        keyMap.put("022101154513", "55EF220528570F325C092558547F465A");
        keyMap.put("022102423842", "562B2300357A22427CA50778567B3A7A");
        keyMap.put("022102391965", "566D2319547D266554BF2F505F7F1B52");
        keyMap.put("022102392559", "56552321487D265960B31B5C777B275E");
        keyMap.put("022102392567", "5663232148671A6760811B5C777F275E");
        keyMap.put("022102423925", "560D2300343D46047D460679567F3B7B");
        keyMap.put("022102423883", "566C230035BBE3827CE40778567B3A7A");
        keyMap.put("022101154333", "551122012A7329325A692356547F4458");
        keyMap.put("022102424030", "561123402D702A108453FF80567F4282");
        keyMap.put("E6A715283910", "C77EBC28183DF733471B3FE5BFFF4E61");
        keyMap.put("E1F60C5239EA", "6AA7021013FF5CDA6C5E1D5E5EFF458B");
        keyMap.put("D5639EAC3EC1", "83E6012C02FFFF82BF0EDE2AF77BDCEA");
        keyMap.put("FB5D6F01FB75", "80D7CC016BFBEF70F73FEF367FD36AFC");
        keyMap.put("D71269D2A8AC", "A1167B809AAC6FBC517C6969F9FC117A");
        keyMap.put("C962DB63BF90", "B5333D2375FFFF90EB91DB61FABF9A22");
        keyMap.put("C7E628FB22BC", "7C800E2210BE293AE4D5BDE05EF74A1D");
        keyMap.put("FBA5A47E63A1", "6DE349620367BE80DC3EA563CFD907E1");
        keyMap.put("FEFD7A3FADD3", "EE23772DBCAFFA4BEA7BFBC6779327EC");
        keyMap.put("F30EFB1774DE", "53780914EAF4FFD47EFFFF76FFD76F8B");
        keyMap.put("CB2F2D360B8D", "3cb15c022babfd8b0ce87d4d2fd63841");
        keyMap.put("022101165243", "551222121B5249436A781366577E5368");     //02:21:01:16:52:43



        keyMap.put("26A715283910", "077EBC28583DF733871BFFE5FFFF4E61");
        keyMap.put("21F60C5239EA", "AAA7021053FF5CDAAC5EDD5E9EFF458B");
        keyMap.put("15639EAC3EC1", "C3E6012C42FFFF82FF0E9E2AB77BDCEA");
        keyMap.put("3B5D6F01FB75", "C0D7CC01ABFBEF70373F6F367FD36AFC");
        keyMap.put("171269D2A8AC", "E1167B80DAAC6FBC917CE969F9FC117A");
        keyMap.put("0962DB63BF90", "F5333D23B5FFFF902B91DB61FABF9A22");
        keyMap.put("07E628FB22BC", "BC800E2250BE293A24D57DE09EF74A1D");
        keyMap.put("3BA5A47E63A1", "ADE349624367BE801C3EE563CFD907E1");
        keyMap.put("3EFD7A3FADD3", "2E23772DFCAFFA4B2A7B7BC6F79327EC");
        keyMap.put("330EFB1774DE", "937809142AF4FFD4BEFFFF76FFD76F8B");

        keyMap.put("1F589CD575BA", "D69DF45515F7FEEA69C79C7FFB7D114A");

        keyMap.put("3691A92D557F", "C1BB3A054CFFEB7EB8ADE9F0BCF3FE82");
        keyMap.put("3918CA8BB784", "5EE5E283EDBFDA847B07CA37DD338142");
        keyMap.put("38F9F6138195", "A80DEF0122E5F625CCC1F76AF77B7794");
        keyMap.put("28D499CAA431", "60616D80EFA5B97196BBFF1FDF7B3D6E");
        keyMap.put("1372252284EC", "39DA9700FACC678CB96CE5F5357CA9A6");

        keyMap.put("285688E4C7A4", "A80DEF0122E5F625CCC1F76AF77B7794");
        keyMap.put("039263609B3C", "4733F500D3BF6F2CFE9EE76AE77EFEFB");

//        keyMap.put("285688E4C7A4", "1E552D5034493C462D4B3C560A5A282A");
    }

//    1106 022102423925    56 0D 23 00 34 3D 46 04 7D 46 06 79 56 7F 3B 7B
//    1105 022102423842    56 2B 23 00 35 7A 22 42 7C A5 07 78 56 7B 3A 7A
//    1104 022101154513    55 EF 22 05 28 57 0F 32 5C 09 25 58 54 7F 46 5A
//    1102 022102392567    56 63 23 21 48 67 1A 67 60 81 1B 5C 77 7F 27 5E
//    1108 022102391965    56 6D 23 19 54 7D 26 65 54 BF 2F 50 5F 7F 1B 52
//    1110 022102424030    56 11 23 40 2D 70 2A 10 84 53 FF 80 56 7F 42 82
//    1107 022102392757    56 51 23 21 46 77 2A 57 62 B1 1B 5E 77 7F 29 60
//    1101 022101154333    55 11 22 01 2A 73 29 32 5A 69 23 56 54 7F 44 58
//    1109 022102392559    56 55 23 21 48 7D 26 59 60 B3 1B 5C 77 7B 27 5E
//    1103 022102423883    56 6C 23 00 35 BB E3 82 7C E4 07 78 56 7B 3A 7A

    /**
     * 马蹄锁
     */
//    public static byte[] key = {(byte)0x96, (byte)0x9D, (byte)0xf4, 0x55, (byte)0xd5, (byte)0xf7, (byte)0xfe, (byte)0xea, 0x29, (byte)0xc7, (byte)0xdc, 0x7f, (byte)0xfb, 0x7d, 0x11, 0x4a}; //新锁原始密钥
//    public static byte[] key = {(byte)0x55, (byte)0x12, (byte)0x22, 0x12, (byte)0x1b, (byte)0x52, (byte)0x49, (byte)0x43, 0x6A, (byte)0x78, (byte)0x13, 0x66, (byte)0x57, 0x7e, 0x53, 0x68};
//    public static byte[] key = {(byte)0x56, (byte)0x0D, (byte)0x23, 0x00, (byte)0x34, (byte)0x3D, (byte)0x46, (byte)0x04, 0x7D, (byte)0x46, (byte)0x06, 0x79, (byte)0x56, 0x7F, 0x3B, 0x7B};
//    public static byte[] key = {(byte)0x56, (byte)0x2B, (byte)0x23, 0x00, (byte)0x35, (byte)0x7A, (byte)0x22, (byte)0x42, 0x7C, (byte)0xA5, (byte)0x07, 0x78, (byte)0x56, 0x7B, 0x3A, 0x7A};
//    public static byte[] key = {(byte)0x55, (byte)0xEF, (byte)0x22, 0x05, (byte)0x28, (byte)0x57, (byte)0x0F, (byte)0x32, 0x5C, (byte)0x09, (byte)0x25, 0x58, (byte)0x54, 0x7F, 0x46, 0x5A};
//    public static byte[] key = {(byte)0x56, (byte)0x63, (byte)0x23, 0x21, (byte)0x48, (byte)0x67, (byte)0x1A, (byte)0x67, 0x60, (byte)0x81, (byte)0x1B, 0x5C, (byte)0x77, 0x7F, 0x27, 0x5E};
//    public static byte[] key = {(byte)0x56, (byte)0x6D, (byte)0x23, 0x19, (byte)0x54, (byte)0x7D, (byte)0x26, (byte)0x65, 0x54, (byte)0xBF, (byte)0x2F, 0x50, (byte)0x5F, 0x7F, 0x1B, 0x52};
//    public static byte[] key = {(byte)0x56, (byte)0x11, (byte)0x23, 0x40, (byte)0x2D, (byte)0x70, (byte)0x2A, (byte)0x10, (byte)0x84, (byte)0x53, (byte)0xFF, (byte)0x80, (byte)0x56, 0x7F, 0x42, (byte)0x82};
//    public static byte[] key = {(byte)0x56, (byte)0x51, (byte)0x23, 0x21, (byte)0x46, (byte)0x77, (byte)0x2A, (byte)0x57, (byte)0x62, (byte)0xB1, (byte)0x1B, (byte)0x5E, (byte)0x77, 0x7F, 0x29, (byte)0x60};
//    public static byte[] key = {(byte)0x55, (byte)0x11, (byte)0x22, 0x01, (byte)0x2A, (byte)0x73, (byte)0x29, (byte)0x32, (byte)0x5A, (byte)0x69, (byte)0x23, (byte)0x56, (byte)0x54, 0x7F, 0x44, (byte)0x58};
//    public static byte[] key = {(byte)0x56, (byte)0x55, (byte)0x23, 0x21, (byte)0x48, (byte)0x7D, (byte)0x26, (byte)0x59, (byte)0x60, (byte)0xB3, (byte)0x1B, (byte)0x5C, (byte)0x77, 0x7B, 0x27, (byte)0x5E};
//    public static byte[] key = {(byte)0x56, (byte)0x6C, (byte)0x23, 0x00, (byte)0x35, (byte)0xBB, (byte)0xE3, (byte)0x82, (byte)0x7C, (byte)0xE4, (byte)0x07, (byte)0x78, (byte)0x56, 0x7B, 0x3A, (byte)0x7A};
//    public static byte[] key = {(byte)0x80, (byte)0xD7, (byte)0xCC, 0x01, (byte)0x6B, (byte)0xFB, (byte)0xEF, (byte)0x70, (byte)0xF7, (byte)0x3F, (byte)0xEF, (byte)0x36, (byte)0x7F, (byte)0xD3, 0x6A, (byte)0xFC};

//    public static byte[] key = {(byte)0xA8, (byte)0x0D, (byte)0xEF, (byte)0x01, (byte)0x22, (byte)0xE5, (byte)0xF6, (byte)0x25, (byte)0xCC, (byte)0xC1, (byte)0xF7, (byte)0x6A, (byte)0xF7, (byte)0x7B, (byte)0x77, (byte)0x94};


//    80 D7 CC 01 6B FB EF 70 F7 3F EF 36 7F D3 6A FC

    public static byte[] key = {32,87,47,82,54,75,63,71,48,80,65,88,17,99,45,43};   //物联锁原始密钥
//    十六进制：20 57 2f 52 36 4b 3f 47 30 50 41 58 11 63 2d 2b

//    public static byte[] key = {32,87,47,82,54,75,63,71,45,75,60,86,10,90,40,42};
//    public static byte[] newkey = {32,87,47,82,54,75,63,71,48,80,65,88,17,99,45,43};
//    public static byte[] newKey = {74,50,90,95,30,21,61,17,80,46,99,94,4,73,1,15};
//    public static byte[] newKey = {51,30,65,57,44,45,22,71,35,63,23,82,36,96,76,11};
    public static byte[] password = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};   //物联锁原始密码

//    public static byte[] password = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /**
     * 新的密钥
     * */
    public static byte[] newKey = {30,85,45,80,52,73,60,70,45,75,60,86,10,90,40,42};   //7MA密钥  1E552D5034493C462D4B3C560A5A282A

//    public static byte[] newKey = {(byte)0xA8, (byte)0x0D, (byte)0xEF, (byte)0x01, (byte)0x22, (byte)0xE5, (byte)0xF6, (byte)0x25, (byte)0xCC, (byte)0xC1, (byte)0xF7, (byte)0x6A, (byte)0xF7, (byte)0x7B, (byte)0x77, (byte)0x94}; //1124 锁厂密钥
//    A8 0D EF 01 22 E5 F6 25 CC C1 F7 6A F7 7B 77 94

    public static byte[] newKey2 = {30,85,45,80,52,73,60,70,45,75,60,86,10,90,40,42};
//    public static byte[] key = {36,87,48,82,54,75,26,71,48,80,65,88,12,99,45,23};
    /**
     * 圆形锁
     */
//    public static byte[] yx_key = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};


//    public static byte[] passwordnew = {0x4D, 0x47, 0x22, 0x3F, 0x38, 0x3B};
//    public static byte[] passwordnew = {0x28, 0x5, 0x55, 0x28, 0x58, 0x49};  //40,5,85,40,88,73
    public static byte[] passwordnew = {0x32, 0x30, 0x31, 0x37, 0x31, 0x35};    //7MA密码 201715
//    public static byte[] passwordnew = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};        //锁厂密码


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

