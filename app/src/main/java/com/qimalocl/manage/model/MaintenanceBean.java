package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class MaintenanceBean {
    private String date;    //日期
    private String car_repair_used;    //投放使用数量
    private String car_recycle; //已回收数量
    private String car_repair; //已修好数量

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCar_repair_used() {
        return car_repair_used;
    }

    public void setCar_repair_used(String car_repair_used) {
        this.car_repair_used = car_repair_used;
    }

    public String getCar_recycle() {
        return car_recycle;
    }

    public void setCar_recycle(String car_recycle) {
        this.car_recycle = car_recycle;
    }

    public String getCar_repair() {
        return car_repair;
    }

    public void setCar_repair(String car_repair) {
        this.car_repair = car_repair;
    }
}
