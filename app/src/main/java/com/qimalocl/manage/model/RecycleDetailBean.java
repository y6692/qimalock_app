package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class RecycleDetailBean {
    private String number;    //车辆编号
    private String bad_time;    //坏车时间
    private String recycle_time; //回收时间
    private String scrapped_time; //报废时间


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBad_time() {
        return bad_time;
    }

    public void setBad_time(String bad_time) {
        this.bad_time = bad_time;
    }

    public String getRecycle_time() {
        return recycle_time;
    }

    public void setRecycle_time(String recycle_time) {
        this.recycle_time = recycle_time;
    }

    public String getScrapped_time() {
        return scrapped_time;
    }

    public void setScrapped_time(String scrapped_time) {
        this.scrapped_time = scrapped_time;
    }
}
