package com.user.usercenter.service;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LambdaTest {


    @Test
    void test() {
        List<String> list1 = new ArrayList<>();
        list1.add("张老三");
        list1.add("张小三");
        list1.add("李四");
        list1.add("赵五");
        list1.add("张六");
        list1.add("王八");

//        ArrayList<String> list2 = new ArrayList<>();
//        list1.forEach(item -> {
//            String s = "张";
//            if (item.contains(s) && item.length() == 3) {
//               list2.add(item);
//            }
//        });
        List<String> list = list1.stream().filter((String item) -> item.contains("张")).filter((String name) -> name.length() == 3).collect(Collectors.toList());
        System.out.println(list);

    }
}
