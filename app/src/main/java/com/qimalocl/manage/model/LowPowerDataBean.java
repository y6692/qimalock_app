package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class LowPowerDataBean {

    private int lock_id;    //车锁id

    private String lock_name;   //车锁名称(英文)

    private String lock_title;  //车锁描述(中文)

    private String remark;  //电量范围说明

    private int ultra_low_count;    //超低电数量

    private int low_count;  //低电数量

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUltra_low_count() {
        return ultra_low_count;
    }

    public void setUltra_low_count(int ultra_low_count) {
        this.ultra_low_count = ultra_low_count;
    }

    public int getLow_count() {
        return low_count;
    }

    public void setLow_count(int low_count) {
        this.low_count = low_count;
    }
}
