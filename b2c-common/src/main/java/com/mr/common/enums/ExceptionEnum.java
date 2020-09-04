package com.mr.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {

  PRICE_CANNOT_BE_NULL(400,"价格不能为空",new Date()),
    NAME_TOO_LONG(402,"名字太长",new Date()),
  CATEGORY_IS_NULL(404,"list为空",new Date()),
  MEMBER_IS_NULL(403,"list为空",new Date()),
  USER_IS_NULL(406,"user为空",new Date()),
  ERROR_FOR_SPU_ADD(406,"商品增加失败",new Date()),
  ;


    private int code;
    private String msg;
    private Date times;

}
