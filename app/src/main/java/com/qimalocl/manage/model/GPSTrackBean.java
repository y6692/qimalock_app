package com.qimalocl.manage.model;

/**
 * Created by yuanyi on 2019/7/24.
 *
 * {"orientation":24,"lon":117.0934779462,"speed":2,"did":800169,"ctime":"2019-07-23 11:40:39","lat":36.133942489956}
 */

public class GPSTrackBean {

    private String lat;
    private String lon;
    private String speed;
    private String did;
    private String ctime;
    private String orientation;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}
