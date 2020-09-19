package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class LowPowerDetailBean {

    private int type;   //(1、超低电 2、低电)
    private int lock_id;    //车锁id
    private String lock_name;   //车锁名称(英文)
    private String lock_title;  //车锁描述(中文)
    private String number;  //车辆编号
    private String electricity; //当前电量
    private String low_start_time;  //低电开始时间
    private String total_time;  //缺电时长
    private String battery_name;  //电池类型

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getLow_start_time() {
        return low_start_time;
    }

    public void setLow_start_time(String low_start_time) {
        this.low_start_time = low_start_time;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getBattery_name() {
        return battery_name;
    }

    public void setBattery_name(String battery_name) {
        this.battery_name = battery_name;
    }
}
