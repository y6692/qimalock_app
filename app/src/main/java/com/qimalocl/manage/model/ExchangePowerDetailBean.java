package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class ExchangePowerDetailBean {
    private String number;    //车辆编号
    private String pre_electricity;    //更换前电量
    private String aft_electricity; //当前电量
    private String created_at; //更换时间
    private String finished_time; //完成时间

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPre_electricity() {
        return pre_electricity;
    }

    public void setPre_electricity(String pre_electricity) {
        this.pre_electricity = pre_electricity;
    }

    public String getAft_electricity() {
        return aft_electricity;
    }

    public void setAft_electricity(String aft_electricity) {
        this.aft_electricity = aft_electricity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFinished_time() {
        return finished_time;
    }

    public void setFinished_time(String finished_time) {
        this.finished_time = finished_time;
    }
}
