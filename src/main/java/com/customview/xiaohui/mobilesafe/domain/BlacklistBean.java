package com.customview.xiaohui.mobilesafe.domain;

/**
 * Created by wizardev on 2016/12/21.
 */

public class BlacklistBean {
    private String phone;
    private int mode;

    public int getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlacklistBean that = (BlacklistBean) o;

        return phone.equals(that.phone);

    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "BlacklistBean{" +
                "mode=" + mode +
                ", phone='" + phone + '\'' +
                '}';
    }
}
