package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/2/17.
 */

public class SchoolListBean {

    private int id;
    private String name;
    private String cert_method;

    private String longitude; //用户管辖学校经度
    private String latitude; //用户管辖学校纬度
    private int[] carmodel_ids; //用户管辖学校 对应车型ID数组 1、单车 2、助力车

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCert_method() {
        return cert_method;
    }

    public void setCert_method(String cert_method) {
        this.cert_method = cert_method;
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

    public int[] getCarmodel_ids() {
        return carmodel_ids;
    }

    public void setCarmodel_ids(int[] carmodel_ids) {
        this.carmodel_ids = carmodel_ids;
    }
}
