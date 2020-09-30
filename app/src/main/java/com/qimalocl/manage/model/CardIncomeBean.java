package com.qimalocl.manage.model;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class CardIncomeBean {

    private String user_name;   //姓名

    private String user_phone;   //手机号

    private String card_name;   //套餐卡名称

    private String order_amount;   //订单金额

    private int order_state;   //订单状态 0、已取消 10、待支付 20、已完成

    private String payment_time;   //支付时间


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

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
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

    public String getPayment_time() {
        return payment_time;
    }

    public void setPayment_time(String payment_time) {
        this.payment_time = payment_time;
    }
}
