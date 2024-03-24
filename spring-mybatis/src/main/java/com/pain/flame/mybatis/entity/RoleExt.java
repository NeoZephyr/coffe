package com.pain.flame.mybatis.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/9/22.
 */
public class RoleExt extends Role {

    private List<Privilege> privileges;

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    @Override
    public String toString() {
        return "RoleExt{" +
                "role=" + super.toString() +
                ", privileges=" + privileges +
                '}';
    }
}
