package com.user.usercenter.service;


import com.user.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RedisTemplateTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;


    @Test
    void residsTemplate() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set("ZMJ", "快来吃饭了");
//        valueOperations.set("ZMJ", 23);
//        valueOperations.set("ZMJ", "耳鸣");

        Object zmj = valueOperations.get("ZMJ");
        log.info(" {}", zmj);
    }

    @Test
    void residsTemplate1() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("ZMJ","快来吃饭了");
//        valueOperations.set("ZM", 23);
//        valueOperations.set("Z", "耳鸣");
//        Object zmj = valueOperations.get("ZM");
//        User loginUser = userService.getLoginUser(request);
//        Long userId = 1726180489257328642L;
//        String key = String.format("Zmj:user:userPageList:%s", "1726180489257328642");
//        Object o = valueOperations.get(key);
//        log.info(" {}", o);

    }

}
