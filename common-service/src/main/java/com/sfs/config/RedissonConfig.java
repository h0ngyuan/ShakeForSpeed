package com.sfs.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.redisson.spring.session.config.EnableRedissonHttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRedissonHttpSession(maxInactiveIntervalInSeconds = 1800) // 可选：集成 Spring Session
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 1. 单机模式（最常用）
        SingleServerConfig singleServer = config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(database)
                // 连接池配置
                .setConnectionPoolSize(16)                    // 连接池大小
                .setConnectionMinimumIdleSize(4)             // 最小空闲连接
                .setIdleConnectionTimeout(10000)             // 空闲连接超时（ms）
                .setConnectTimeout(10000)                    // 连接超时
                .setTimeout(3000);                           // 命令超时
        // 2. 通用配置
        config.setCodec(new JsonJacksonCodec())             // 序列化：JSON 更易调试
                .setTransportMode(TransportMode.NIO)         // NIO 模式（默认）
                .setNettyThreads(32)                         // Netty worker 线程数
                .setThreads(16);                             // Redisson 内部线程数

        // 3. 锁看门狗超时（关键！）
        config.setLockWatchdogTimeout(30000); // 30秒，Watchdog 会自动续期

        // 4. 创建并返回 RedissonClient
        return Redisson.create(config);
    }
}