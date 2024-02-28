package com.user.usercenter.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class ExcelInformation {

    @ExcelProperty(index = 2)
    private String string;
    @ExcelProperty(index = 1)
    private String date;

}
