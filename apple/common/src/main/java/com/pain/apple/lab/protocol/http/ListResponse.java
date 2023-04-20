package com.pain.apple.lab.protocol.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListResponse<E> {

    private int page;
    private long total;
    private List<E> rows;
}