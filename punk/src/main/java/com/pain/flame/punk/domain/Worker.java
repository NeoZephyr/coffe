package com.pain.flame.punk.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Worker implements Serializable {

    private Integer id;
    private String name;
}
