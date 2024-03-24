package com.pain.flame.mybatis.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/9/10.
 */
public class UserExt extends User {
    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserExt{" +
                "user=" + super.toString() +
                ", roles=" + roles +
                '}';
    }
}
