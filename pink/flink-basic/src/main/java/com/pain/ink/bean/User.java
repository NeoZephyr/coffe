package com.pain.ink.bean;

import java.util.Date;

public class User {
    String name;
    Date createTime;

    public User(String name, Date createTime) {
        this.name = name;
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
