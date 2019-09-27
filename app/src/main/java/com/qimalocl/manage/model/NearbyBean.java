package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/7/20.
 */

public class NearbyBean {

    private String latitude;
    private String longitude;
    private String codenum;
    private String quantity;
    private String quantity_level;
    private String type;
    private String quantity_level_2_count;
    private String quantity_level_3_count;
//    "quantity_level_3_count":33,"quantity_level_2_count":32

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCodenum() {
        return codenum;
    }

    public void setCodenum(String codenum) {
        this.codenum = codenum;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity_level() {
        return quantity_level;
    }

    public void setQuantity_level(String quantity_level) {
        this.quantity_level = quantity_level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuantity_level_2_count() {
        return quantity_level_2_count;
    }

    public void setQuantity_level_2_count(String quantity_level_2_count) {
        this.quantity_level_2_count = quantity_level_2_count;
    }

    public String getQuantity_level_3_count() {
        return quantity_level_3_count;
    }

    public void setQuantity_level_3_count(String quantity_level_3_count) {
        this.quantity_level_3_count = quantity_level_3_count;
    }
}
