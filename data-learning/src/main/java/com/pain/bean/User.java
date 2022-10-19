package com.pain.bean;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class User implements Serializable {

    public Timestamp statDate;
    public Timestamp birthday;
    public String idCard;
    public String name;
    public String mobile;
    public String address;
    public String gender;

    public boolean warning() {
        return name == null || mobile == null || address == null || gender == null;
    }
}
