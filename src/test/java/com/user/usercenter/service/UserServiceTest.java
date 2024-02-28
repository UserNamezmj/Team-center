package com.user.usercenter.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import com.user.usercenter.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import static org.junit.Assert.*;


/*
 * 用户服务测试*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(60, 2000, 200000,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("test");
        user.setAvatarUrl("https://bcdh.yuque.com/dashboard");
        user.setUserAccount("123456");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123456");
        user.setEmail("123456");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);


    }

    @Test
    void userRegister() {
        String userAccount = "xiaohong.jpg";
        int i = userAccount.indexOf('.');
        String substring = userAccount.substring(i);
        System.out.println(substring);
    }

    @Test
    void getUserListTags() {
        List<String> list = Arrays.asList("JSON", "C++", "Web");
        List<User> userListTags = userService.getUserListTags(list);
        //断言某个值是否为空
        Assert.assertNotNull(userListTags);
    }

    /*
     * 测试user数据库的导入*/
//    @Test
//    void getUserList() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        int j = 0;
//        final int MAX_NUM = 100000;
//        ArrayList<User> userList = new ArrayList<>();
////        while (j != MAX_NUM) {
////            j++;
//        for (int i = 0; i <= MAX_NUM; i++) {
//            User user = new User();
//            user.setUsername("发ifa");
//            user.setAvatarUrl("https://mybatis.plus/img/relationship-with-mybatis.png");
//            user.setUserAccount("fireFine");
//            user.setGender(0);
//            user.setUserPassword("123456789");
//            user.setPhone("19079798080");
//            user.setEmail("78696967979@qq.com");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setTags("杭州");
//            userList.add(user);
//        }
//        userService.saveBatch(userList, 100);
//        stopWatch.stop();
//        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
//        System.out.println(totalTimeSeconds);
//    }


//    @Test
//    void getUserList1() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        int j = 0;
//        final int MAX_NUM = 100000;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            List<User> userList = new ArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("发ifa");
//                user.setAvatarUrl("https://mybatis.plus/img/relationship-with-mybatis.png");
//                user.setUserAccount("fireFine");
//                user.setGender(0);
//                user.setUserPassword("123456789");
//                user.setPhone("19079798080");
//                user.setEmail("78696967979@qq.com");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setTags("[杭州]");
//                userList.add(user);
//                if (j % 5000 == 0) {
//                    break;
//                }
//            }
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println(Thread.currentThread().getName());
//                userService.saveBatch(userList, 5000);
//            }, threadPoolExecutor);
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
//        System.out.println(totalTimeSeconds);
//    }

//    }

}