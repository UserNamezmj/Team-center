package com.user.usercenter.service;


import com.user.usercenter.model.Information;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class informationTest {

    @Resource
    private InformationService informationService;



    @Test
    void  informationTest() {
        System.out.println(new Date().toString());
    }
}
