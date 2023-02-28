package com.pain.apple.lab.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Size(max = 5, message = "太长了")
    private String name;
}
