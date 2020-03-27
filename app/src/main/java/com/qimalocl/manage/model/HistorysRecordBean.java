package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class HistorysRecordBean {



    private String uid;
    private String username;
    private String telphone;
    private String codenum;
    private String prices;
    private String start_end_date;
    private String order_type;

    private int id; //ID
    private String car_number;  //车辆编号
    private String user_name;   //姓名
    private String user_phone;   //手机号
    private String created_at;   //借车时间
    private String car_end_time;   //还车时间
    private String order_amount;   //订单金额
    private int order_state;   //订单状态

    private boolean isLoad; //是否加载过
    private boolean isShowDate; //是否显示日期

    public String getStart_end_date() {
        return start_end_date;
    }

    public void setStart_end_date(String start_end_date) {
        this.start_end_date = start_end_date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getCodenum() {
        return codenum;
    }

    public void setCodenum(String codenum) {
        this.codenum = codenum;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCar_end_time() {
        return car_end_time;
    }

    public void setCar_end_time(String car_end_time) {
        this.car_end_time = car_end_time;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public int getOrder_state() {
        return order_state;
    }

    public void setOrder_state(int order_state) {
        this.order_state = order_state;
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
