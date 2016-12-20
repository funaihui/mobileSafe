package com.customview.xiaohui.mobilesafe.domain;

/**
 * Created by wizardev on 2016/12/18.
 */

public class ContactsBean {
    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "ContactsBean{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
