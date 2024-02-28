package com.user.usercenter.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

@Slf4j
public class ExcelTest {


    public static void main(String[] args) {

        /**
         * 指定列的下标或者列名
         *
         * <p>1. 创建excel对应的实体对象,并使用{@link ExcelProperty}注解. 参照{@link IndexOrNameData}
         * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link IndexOrNameDataListener}
         * <p>3. 直接读即可
         */
//        indexOrNameRead();
        simpleRead();

    }


    /*
     * 第一种监听读法*/
    private static void simpleRead() {
        String fileName = "D:\\BaiduNetdiskDownload\\Team-center\\src\\main\\resources\\运营数据统计报表.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
//        EasyExcel.read(fileName, ExcelInformation.class, new PageReadListener<ExcelInformation>(dataList -> {
//            for (ExcelInformation demoData : dataList) {
//                log.info("读取到一条数据{}", demoData);
//            }
//        })).sheet().doRead();
        //使用自定义监听器
        EasyExcel.read(fileName, ExcelInformation.class, new DemoDataListener()).doReadAll();
    }


    /*
     * 第二种同步读法*/
    public static void indexOrNameRead() {
        String fileName = "D:\\BaiduNetdiskDownload\\Team-center\\src\\main\\resources\\运营数据统计报表.xlsx";
        // 这里默认读取第一个sheet
//        EasyExcel.read(fileName, IndexOrNameData.class, new IndexOrNameDataListener()).sheet().doRead();
        List<ExcelInformation> list = EasyExcel.read(fileName).head(ExcelInformation.class).sheet().doReadSync();
        for (ExcelInformation excel : list
        ) {
            System.out.println(excel);
        }
    }
}
