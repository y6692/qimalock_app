package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class CarsBean {
    private String number;    //车辆编号
    private int carmodel_id;  //车型ID 1单车 2助力车
    private int lock_id;  //锁型ID
    private String longitude; //经度
    private String latitude; //纬度
    private int level;  //图标级别 3红色 2黄色 1绿色 4换电中 5维护中

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCarmodel_id() {
        return carmodel_id;
    }

    public void setCarmodel_id(int carmodel_id) {
        this.carmodel_id = carmodel_id;
    }

    public int getLock_id() {
        return lock_id;
    }

    public void setLock_id(int lock_id) {
        this.lock_id = lock_id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
