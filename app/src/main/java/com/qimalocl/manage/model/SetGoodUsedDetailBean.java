package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class SetGoodUsedDetailBean {
    private String number;    //车辆编号
    private String bad_reason; //坏车原因
    private String bad_time;    //坏车时间
    private String recycle_time; //回收时间
    private String setgood_time; //修好时间
    private String last_user_time; //使用时间
    private int status; //1已回收 2已修好 3报废 4投放使用(根据status值 显示数据)

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBad_reason() {
        return bad_reason;
    }

    public void setBad_reason(String bad_reason) {
        this.bad_reason = bad_reason;
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

    public String getSetgood_time() {
        return setgood_time;
    }

    public void setSetgood_time(String setgood_time) {
        this.setgood_time = setgood_time;
    }

    public String getLast_user_time() {
        return last_user_time;
    }

    public void setLast_user_time(String last_user_time) {
        this.last_user_time = last_user_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
