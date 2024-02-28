package com.user.usercenter.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.usercenter.mapper.UserMapper;
import com.user.usercenter.model.User;
import com.user.usercenter.result.PageResult;
import com.user.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SpringUserListTask {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 3 20 * * ?")
    public void taskUserList() {
        RLock lock = redissonClient.getLock("zmj:precachejob:docache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                log.info("lock {}", Thread.currentThread().getId());
                ValueOperations<String, Object> operations = redisTemplate.opsForValue();
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("username", "publicUserAdmin2");
                List<User> userList = userMapper.selectList(queryWrapper);
                //获取用户id
                for (User user : userList
                ) {
                    Long userId = user.getId();
                    // 设置key格式
                    String key = String.format("Zmj:user:userPageList:%s", userId);
                    // 判断是否有缓存
                    if (ObjectUtils.isEmpty(operations.get(key))) {
                        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                        Page<User> userPage = new Page<>(1, 10);
                        Page<User> page = userService.page(userPage, queryWrapper1);
                        PageResult pageResult = new PageResult();
                        pageResult.setTotal(page.getTotal());
                        pageResult.setRecords(page.getRecords());
                        //如果Redis没有缓存就存进去
                        try {
                            operations.set(key, pageResult, 10000, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            log.info("Error Redis key {}", e);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            log.info("error info desc",e);
        } finally {
            //判断只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }

        }


    }

}
