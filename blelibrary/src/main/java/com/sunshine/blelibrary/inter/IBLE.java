package com.sunshine.blelibrary.inter;

import com.sunshine.blelibrary.mode.Order;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:29
 * 邮箱：44493547@qq.com
 * 备注：BLE操作接口
 */
public interface IBLE {

    /**
     * 开启日志
     * @param isOpen 是否开启
     */
    void setDebug(boolean isOpen);
    /**
     * 是否修改过密钥
     */
    void setChangKey(boolean isChange);
    /**
     * 是否修改过密码
     */
    void setChangPsd(boolean isChange);
    /**
     * 扫描设备
     */
    void startScan(OnDeviceSearchListener onDeviceSearchListener);

    /**
     * 停止扫描
     */
    void stopScan();

    /**
     * 链接设备
     * @param address 设备地址
     * @param onConnectionListener 链接状态回调
     */
    void connect(String address, OnConnectionListener onConnectionListener);

    /**
     * 写指令 指令byte
     * @param bytes
     */
    void writeByte(byte[] bytes);

    /**
     * 获取Token
     * @return
     */
    boolean getToken();

    boolean getBattery();

    boolean getBattery2();

    /**
     * 开锁
     * @return
     */
    boolean openLock();

    /**
     * 复位
     * @return
     */
    boolean resetLock();

    /**
     * 信标
     * @return
     */
    boolean xinbiao();


    /**
     * 获取锁状态
     * @return
     */
    boolean getLockStatus();

    /**
     * 修改密码
     * @return
     */
    boolean setPassword(Order.TYPE type, byte[] bytes);

    /**
     * 修改密钥
     * @return
     */
    boolean setKey(Order.TYPE type, byte[] bytes);
    /**
     * oad模式
     * @return
     */
    boolean updateVersion();
    /**
     * oad升级时写写入的特征值
     * @param bytes
     * @return
     */
    boolean writeWrite(byte[] bytes);

    /**
     * oad升级时写读取的特征值
     * @param bytes
     * @return
     */
    boolean writeRead(byte[] bytes);

    /**
     * 重置密钥和密码
     */
    void resetPasswordAndAQ();
    /**
     * 断开链接
     */
    void disconnect();

    void close();

    /**
     * 清除缓存
     * @return
     */
    boolean refreshCache();
}
