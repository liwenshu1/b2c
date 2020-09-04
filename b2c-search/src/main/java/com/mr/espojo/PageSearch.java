package com.mr.espojo;

import lombok.Data;

import java.util.Map;

@Data
public class PageSearch {

    private String key;
    private Integer page=0;//默认0页
    private Integer size=10;//默认每页二十条

    private Map<String,String> filter;//过滤条件

}
