package com.qimalocl.manage.model;

import java.io.Serializable;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class BatteryUnlockBean implements Serializable {

    private int code;   //状态码 1、电池锁打开成功 2、电池锁打开失败 3、换电次数超2次 4、电量超过换电标准 5、查询队列连接失败

    private int car_status;   //车辆状态 1、正常 2、坏车

    private String message;   //描述信息

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCar_status() {
        return car_status;
    }

    public void setCar_status(int car_status) {
        this.car_status = car_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
