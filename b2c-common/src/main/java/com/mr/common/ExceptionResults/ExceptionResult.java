package com.mr.common.ExceptionResults;


import com.mr.common.enums.ExceptionEnum;
import lombok.Data;

import java.util.Date;
@Data
public class ExceptionResult {

    private Integer code;
    private String msg;
    private Date times;
    private String path;
    private String error;


    public ExceptionResult(ExceptionEnum enums) {
        this.code = enums.getCode();
        this.msg = enums.getMsg();
        this.times = enums.getTimes();
    }
}
