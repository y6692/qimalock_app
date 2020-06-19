package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class CarDispatchBean {
    private int id;    //调运单ID
    private String sn;  //调运单编号
    private String start_longitude;  //调运起始位置经度
    private String start_latitude;  //调运起始位置纬度
    private String end_longitude;  //调运终止位置经度
    private String end_latitude;  //调运终止位置纬度
    private String start_time;  //调运开始时间
    private String end_time;  //调运结束时间
    private String status;  //调运状态 0调运中 1调运完成
    private String cars;  //调运车辆信息
//    car_status	Int
//    调运车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废
//    car_state	Int
//    调运车辆启用禁用状态 1启用 0禁用
    private String photos;  //调运车辆照片信息
//    photo_url	String
//    调运车辆照片完整url
    private String survey_point;  //调运到的调配区域信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(String start_longitude) {
        this.start_longitude = start_longitude;
    }

    public String getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(String start_latitude) {
        this.start_latitude = start_latitude;
    }

    public String getEnd_longitude() {
        return end_longitude;
    }

    public void setEnd_longitude(String end_longitude) {
        this.end_longitude = end_longitude;
    }

    public String getEnd_latitude() {
        return end_latitude;
    }

    public void setEnd_latitude(String end_latitude) {
        this.end_latitude = end_latitude;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCars() {
        return cars;
    }

    public void setCars(String cars) {
        this.cars = cars;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getSurvey_point() {
        return survey_point;
    }

    public void setSurvey_point(String survey_point) {
        this.survey_point = survey_point;
    }
}
