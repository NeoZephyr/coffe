package com.pain.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Customer implements Serializable {
    public String name;
    public String mobile;
    public String company;
    public String companyAddress;
    public String provinceCity;
    public String industry;

    public boolean warning() {
        return name == null || mobile == null || company == null || companyAddress == null || provinceCity == null || industry == null;
    }
}
