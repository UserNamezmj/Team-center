package com.user.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserCenterApplicationTests {

    @Test
    void contextLoads() {

        String userPassword =  "123456";
        String s = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        System.out.println(s);
    }

    @Test
    void contextLoads1() {

//        String userPassword =  "123456";
//        String s = DigestUtils.md5DigestAsHex(userPassword.getBytes());
//        System.out.println(s);
        String key = String.format("Zmj:user:userPageList:%s", 7876878781111111111L);
        System.out.println(key);
    }


}
