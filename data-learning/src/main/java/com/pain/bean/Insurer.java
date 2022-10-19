package com.pain.bean;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Insurer implements Serializable {

    public String name;
    public String idCard;
    public String gender;
    public String mobile;
    public String email;
    public String province;
    public String city;
    public String address;
    public String marriage;

    public Timestamp birthday;
    public String industry;
    public String salary;
    public String education;
    public String carBrand;
}