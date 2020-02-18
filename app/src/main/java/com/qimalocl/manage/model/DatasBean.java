package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class DatasBean {

    private String delivered_cars;  //投放车辆数量
    private String is_using_cars;   //正在使用车辆数量
    private String longtime_not_used_cars;  //长时间未使用车辆数量
    private String not_recycled_cars;   //未回收车辆数量
    private String not_fixed_cars;  //未修好车辆数量
    private String fixed_not_used_cars;   //修好未使用车辆数量

    public String getDelivered_cars() {
        return delivered_cars;
    }

    public void setDelivered_cars(String delivered_cars) {
        this.delivered_cars = delivered_cars;
    }

    public String getIs_using_cars() {
        return is_using_cars;
    }

    public void setIs_using_cars(String is_using_cars) {
        this.is_using_cars = is_using_cars;
    }

    public String getLongtime_not_used_cars() {
        return longtime_not_used_cars;
    }

    public void setLongtime_not_used_cars(String longtime_not_used_cars) {
        this.longtime_not_used_cars = longtime_not_used_cars;
    }

    public String getNot_recycled_cars() {
        return not_recycled_cars;
    }

    public void setNot_recycled_cars(String not_recycled_cars) {
        this.not_recycled_cars = not_recycled_cars;
    }

    public String getNot_fixed_cars() {
        return not_fixed_cars;
    }

    public void setNot_fixed_cars(String not_fixed_cars) {
        this.not_fixed_cars = not_fixed_cars;
    }

    public String getFixed_not_used_cars() {
        return fixed_not_used_cars;
    }

    public void setFixed_not_used_cars(String fixed_not_used_cars) {
        this.fixed_not_used_cars = fixed_not_used_cars;
    }
}
