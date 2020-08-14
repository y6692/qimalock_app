package com.qimalocl.manage.model;

import java.io.Serializable;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class BadCarBean implements Serializable {

    private String number;  //车辆编号
    private String last_user_time;  //最后使用时间
    private String last_user_phone;  //最后使用人手机号
    private String longitude;  //经度
    private String latitude;  //纬度
    private String bad_time;  //坏车时间
    private String bad_reason;  //坏车原因
    private String setgood_reason;  //维修内容
    private String recycle_time;  //回收时间
    private String setgood_time;  //修好时间
    private String last_user_name;  //最后使用人姓名

    private boolean isLoad; //是否加载过
    private boolean isShowDate; //是否显示日期


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLast_user_time() {
        return last_user_time;
    }

    public void setLast_user_time(String last_user_time) {
        this.last_user_time = last_user_time;
    }

    public String getLast_user_phone() {
        return last_user_phone;
    }

    public void setLast_user_phone(String last_user_phone) {
        this.last_user_phone = last_user_phone;
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

    public String getBad_time() {
        return bad_time;
    }

    public void setBad_time(String bad_time) {
        this.bad_time = bad_time;
    }

    public String getBad_reason() {
        return bad_reason;
    }

    public void setBad_reason(String bad_reason) {
        this.bad_reason = bad_reason;
    }

    public String getSetgood_reason() {
        return setgood_reason;
    }

    public void setSetgood_reason(String setgood_reason) {
        this.setgood_reason = setgood_reason;
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

    public String getLast_user_name() {
        return last_user_name;
    }

    public void setLast_user_name(String last_user_name) {
        this.last_user_name = last_user_name;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public boolean isShowDate() {
        return isShowDate;
    }

    public void setShowDate(boolean showDate) {
        isShowDate = showDate;
    }
}
