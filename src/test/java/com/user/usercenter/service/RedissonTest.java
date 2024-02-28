package com.user.usercenter.service;


import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Test
    void RedissonClientTest() {
        RList<Object> name = redissonClient.getList("name");
//        name.add("小明");
        System.out.println(name.get(0));
    }

    @Test
    public void testRedisLock() {

        long currentTime = new Date().getTime();



        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime intervalTime = localDateTime.plusSeconds(10L);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM hh:mm:ss");
        System.out.println(intervalTime.format(dateTimeFormatter));
        intervalTime.format(dateTimeFormatter).toUpperCase(Locale.ROOT);

//        // intervalTime是限流的时间
//        Integer count = redisTemplate.opsForZSet()
//                .rangeByScore("limit", currentTime - intervalTime, currentTime).size();
//
//// 把这个请求放入 zset 中
//        redisTemplate.opsForZSet()
//                .add("limit", UUID.randomUUID().toString(), currentTime);
    }


}
