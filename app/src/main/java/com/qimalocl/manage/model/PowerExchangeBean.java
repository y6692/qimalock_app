package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class PowerExchangeBean {
    private String date;    //日期
    private String car_course;    //换电中数量
    private String car_valid; //有效换电数量
    private String car_invalid; //无效换电数量

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCar_course() {
        return car_course;
    }

    public void setCar_course(String car_course) {
        this.car_course = car_course;
    }

    public String getCar_valid() {
        return car_valid;
    }

    public void setCar_valid(String car_valid) {
        this.car_valid = car_valid;
    }

    public String getCar_invalid() {
        return car_invalid;
    }

    public void setCar_invalid(String car_invalid) {
        this.car_invalid = car_invalid;
    }
}
