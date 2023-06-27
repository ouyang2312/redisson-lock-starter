package com.oy.redissonlockstarter;

import com.oy.redissonlockstarter.config.LockConfig;
import com.oy.redissonlockstarter.core.BusinessKeyProvider;
import com.oy.redissonlockstarter.core.LockAspectHandler;
import com.oy.redissonlockstarter.core.LockInfoProvider;
import com.oy.redissonlockstarter.handler.LockFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 锁 自动装配
 *
 * @author ouyang
 * @createDate 2023/6/26 14:18
 */
@EnableConfigurationProperties(LockConfig.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "enable", havingValue = "true")
@Configuration
public class LockAutoConfiguration {

    @Autowired
    private LockConfig lockConfig;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redisson() {
        Config config = new Config();
        // 如果有集群
        if (lockConfig.getClusterServer() != null) {
            config
                    .useClusterServers()
                    .setPassword(lockConfig.getPassword())
                    .addNodeAddress(lockConfig.getClusterServer().getNodeAddresses());
        } else {
            config
                    .useSingleServer()
                    .setAddress(lockConfig.getAddress())
                    .setDatabase(lockConfig.getDatabase())
                    .setPassword(lockConfig.getPassword());
        }
        config.setEventLoopGroup(new NioEventLoopGroup());
        return Redisson.create(config);
    }

    @Bean
    public LockAspectHandler lockAspectHandler(){
        return new LockAspectHandler();
    }

    @Bean
    public LockInfoProvider lockInfoProvider() {
        return new LockInfoProvider();
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider() {
        return new BusinessKeyProvider();
    }

    @Bean
    public LockFactory lockFactory() {
        return new LockFactory();
    }

}
