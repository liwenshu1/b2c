package com.mr.common.mrexception;

import com.mr.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MrException extends RuntimeException{

    private ExceptionEnum enums;




}
