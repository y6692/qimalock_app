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
}
