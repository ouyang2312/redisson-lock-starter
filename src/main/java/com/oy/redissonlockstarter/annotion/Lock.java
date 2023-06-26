package com.oy.redissonlockstarter.annotion;

import com.oy.redissonlockstarter.handler.strategy.LockTimeOutStrategyEnum;
import com.oy.redissonlockstarter.handler.strategy.ReleaseLockFailStrategyEnum;
import com.oy.redissonlockstarter.model.LockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法锁
 *
 * @author ouyang
 * @createDate 2023/6/26 9:25
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁的名称
     * @return name
     */
    String name() default "";
    /**
     * 锁类型，默认可重入锁
     * @return lockType
     */
    LockType lockType() default LockType.REENTRANT;
    /**
     * 尝试加锁，最多等待时间
     * @return waitTime
     */
    long waitTime() default Long.MIN_VALUE;
    /**
     *上锁以后xxx秒自动解锁
     * @return leaseTime
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 自定义业务key
     * @return keys
     */
    String [] keys() default {};

    /**
     * 加锁超时的处理策略
     * @return lockTimeoutStrategy
     */
    LockTimeOutStrategyEnum lockTimeoutStrategy() default LockTimeOutStrategyEnum.NO_OPERATION;

    /**
     * 自定义加锁超时的处理策略
     * @return customLockTimeoutStrategy
     */
    String customLockTimeoutStrategy() default "";

    /**
     * 释放锁时已超时的处理策略
     * @return releaseTimeoutStrategy
     */
    ReleaseLockFailStrategyEnum releaseTimeoutStrategy() default ReleaseLockFailStrategyEnum.NO_OPERATION;

    /**
     * 自定义释放锁时已超时的处理策略
     * @return customReleaseTimeoutStrategy
     */
    String customReleaseTimeoutStrategy() default "";

}
