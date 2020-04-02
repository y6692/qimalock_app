package com.qimalocl.manage.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class LowPowerBean {

    private int count;  //低电和超低电总数量
    private String xiaoan;  //小安数据
    private String xyt;  //行运兔数据
    private String tbt;  //泰比特数据

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getXiaoan() {
        return xiaoan;
    }

    public void setXiaoan(String xiaoan) {
        this.xiaoan = xiaoan;
    }

    public String getXyt() {
        return xyt;
    }

    public void setXyt(String xyt) {
        this.xyt = xyt;
    }

    public String getTbt() {
        return tbt;
    }

    public void setTbt(String tbt) {
        this.tbt = tbt;
    }
}
