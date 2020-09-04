package com.mr.common.advice;

import com.mr.common.ExceptionResults.ExceptionResult;
import com.mr.common.mrexception.MrException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice//标明为控制层通知类
public class CommonExceptionHandler {

    

    @ExceptionHandler(MrException.class)
    public ResponseEntity<ExceptionResult> handleException(MrException ex){
        System.out.println("捕捉到异常:"+ex.getEnums().getMsg());
        //规定返回的状态码400，然后异常的提示信息设置到body中
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("价格不能为空");
        return ResponseEntity.status(ex.getEnums().getCode()).body(new ExceptionResult(ex.getEnums()));
    }
}
