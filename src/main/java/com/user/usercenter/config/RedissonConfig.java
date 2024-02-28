package com.user.usercenter.config;


import com.user.usercenter.service.impl.RedissonDistributeLocker;
import com.user.usercenter.utils.RedissonLockUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Data
@Slf4j
public class RedissonConfig {

    private String host;

    private Integer port;

    @Bean
    public RedissonClient redissonClient() {
        //创建redisson配置信息 地址 端口
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        //  使用单个Redis，没有开集群 useClusterServers()  设置地址和使用库
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);
        //创建redisson的实列
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

//    @Bean
//    public RedissonDistributeLocker redissonDistributeLocker (RedissonClient redissonClient) {
//        RedissonDistributeLocker redissonDistributeLocker = new RedissonDistributeLocker(redissonClient);
//        RedissonLockUtils.setLocker(redissonDistributeLocker);
//        return redissonDistributeLocker;
//    }
}
