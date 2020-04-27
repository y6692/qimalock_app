package com.qimalocl.manage.model;

import com.amap.api.maps.model.Polygon;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

//"id":89,"name":"常大武进文正楼","school_id":2,"school_name":"常州大学"

public class ParkingBean {
    private int id;
    private String name;
    private int school_id;
    private String school_name;
    private Polygon polygon;

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

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
