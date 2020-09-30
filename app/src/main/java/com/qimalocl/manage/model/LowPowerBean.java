package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class LowPowerBean {

    private int low_power_total;    //低电和超低电总数量
    private int low_total;    //低电总数量
    private int ultra_low_total;    //超低电总数量

    private String low_power_data;  //数据

    public int getLow_power_total() {
        return low_power_total;
    }

    public void setLow_power_total(int low_power_total) {
        this.low_power_total = low_power_total;
    }

    public int getLow_total() {
        return low_total;
    }

    public void setLow_total(int low_total) {
        this.low_total = low_total;
    }

    public int getUltra_low_total() {
        return ultra_low_total;
    }

    public void setUltra_low_total(int ultra_low_total) {
        this.ultra_low_total = ultra_low_total;
    }

    public String getLow_power_data() {
        return low_power_data;
    }

    public void setLow_power_data(String low_power_data) {
        this.low_power_data = low_power_data;
    }
}
