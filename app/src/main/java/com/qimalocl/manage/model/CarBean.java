package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class CarBean {
    private String number;    //车辆编号
    private int lock_id;  //锁型ID
    private String lock_name;    //车锁名称(英文)
    private String lock_title; //车锁名称(中文)
    private String vendor_lock_id; //厂商设备ID
    private int lock_status;  //0未知 3离线 非0非3 正常
    private String lock_no;    //lock_no
    private String lock_mac;    //mac地址
    private String lock_secretkey;    //车锁秘钥
    private String electricity;    //电量
    private int carmodel_id;  //车型ID
    private String carmodel_name;    //车型名称
    private int status;  //车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废
    private int can_finish_order;    //可否结束订单（有无进行中行程）1有 0无

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getLock_id() {
        return lock_id;
    }

    public void setLock_id(int lock_id) {
        this.lock_id = lock_id;
    }

    public String getLock_name() {
        return lock_name;
    }

    public void setLock_name(String lock_name) {
        this.lock_name = lock_name;
    }

    public String getLock_title() {
        return lock_title;
    }

    public void setLock_title(String lock_title) {
        this.lock_title = lock_title;
    }

    public String getVendor_lock_id() {
        return vendor_lock_id;
    }

    public void setVendor_lock_id(String vendor_lock_id) {
        this.vendor_lock_id = vendor_lock_id;
    }

    public int getLock_status() {
        return lock_status;
    }

    public void setLock_status(int lock_status) {
        this.lock_status = lock_status;
    }

    public String getLock_no() {
        return lock_no;
    }

    public void setLock_no(String lock_no) {
        this.lock_no = lock_no;
    }

    public String getLock_mac() {
        return lock_mac;
    }

    public void setLock_mac(String lock_mac) {
        this.lock_mac = lock_mac;
    }

    public String getLock_secretkey() {
        return lock_secretkey;
    }

    public void setLock_secretkey(String lock_secretkey) {
        this.lock_secretkey = lock_secretkey;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public int getCarmodel_id() {
        return carmodel_id;
    }

    public void setCarmodel_id(int carmodel_id) {
        this.carmodel_id = carmodel_id;
    }

    public String getCarmodel_name() {
        return carmodel_name;
    }

    public void setCarmodel_name(String carmodel_name) {
        this.carmodel_name = carmodel_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCan_finish_order() {
        return can_finish_order;
    }

    public void setCan_finish_order(int can_finish_order) {
        this.can_finish_order = can_finish_order;
    }
}
