package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class OrdersBean {

    private int carmodel_id;    //车型id (1、单车 2、助力车)

    private String today_order_count;    //今日订单数

    private String yesterday_order_count;    //昨日订单数

    private String today_daily_car_count;    //今日车辆日活数

    private String yesterday_daily_car_count;    //昨日车辆日活数

    private String today_daily_rate;    //今日车辆日活率

    private String yesterday_daily_rate;    //昨日车辆日活率

    public int getCarmodel_id() {
        return carmodel_id;
    }

    public void setCarmodel_id(int carmodel_id) {
        this.carmodel_id = carmodel_id;
    }

    public String getToday_order_count() {
        return today_order_count;
    }

    public void setToday_order_count(String today_order_count) {
        this.today_order_count = today_order_count;
    }

    public String getYesterday_order_count() {
        return yesterday_order_count;
    }

    public void setYesterday_order_count(String yesterday_order_count) {
        this.yesterday_order_count = yesterday_order_count;
    }

    public String getToday_daily_car_count() {
        return today_daily_car_count;
    }

    public void setToday_daily_car_count(String today_daily_car_count) {
        this.today_daily_car_count = today_daily_car_count;
    }

    public String getYesterday_daily_car_count() {
        return yesterday_daily_car_count;
    }

    public void setYesterday_daily_car_count(String yesterday_daily_car_count) {
        this.yesterday_daily_car_count = yesterday_daily_car_count;
    }

    public String getToday_daily_rate() {
        return today_daily_rate;
    }

    public void setToday_daily_rate(String today_daily_rate) {
        this.today_daily_rate = today_daily_rate;
    }

    public String getYesterday_daily_rate() {
        return yesterday_daily_rate;
    }

    public void setYesterday_daily_rate(String yesterday_daily_rate) {
        this.yesterday_daily_rate = yesterday_daily_rate;
    }
}
