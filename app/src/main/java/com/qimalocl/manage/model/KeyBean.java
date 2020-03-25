package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class KeyBean {

//    {"type":1,"lock_mac":"03:92:63:60:9B:3C","lock_password":"323031373135","lock_secretkey":"80D7CC016BFBEF70F73FEF367FD36AFC"}}


    private int encryptionKey;
    private String keys;
    private int serverTime;

    private int type;
    private String lock_mac;
    private String lock_password;
    private String lock_secretkey;

    public int getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(int encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public int getServerTime() {
        return serverTime;
    }

    public void setServerTime(int serverTime) {
        this.serverTime = serverTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLock_mac() {
        return lock_mac;
    }

    public void setLock_mac(String lock_mac) {
        this.lock_mac = lock_mac;
    }

    public String getLock_password() {
        return lock_password;
    }

    public void setLock_password(String lock_password) {
        this.lock_password = lock_password;
    }

    public String getLock_secretkey() {
        return lock_secretkey;
    }

    public void setLock_secretkey(String lock_secretkey) {
        this.lock_secretkey = lock_secretkey;
    }
}
