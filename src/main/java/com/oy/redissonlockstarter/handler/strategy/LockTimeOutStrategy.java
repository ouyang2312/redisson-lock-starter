package com.oy.redissonlockstarter.handler.strategy;

import com.oy.redissonlockstarter.handler.lock.ILock;
import com.oy.redissonlockstarter.model.LockInfo;
import org.aspectj.lang.JoinPoint;

/**
 * 超时策略
 *
 * @author ouyang
 * @createDate 2023/6/26 9:57
 */
public interface LockTimeOutStrategy {

    void invoke(LockInfo lockInfo, ILock lock, JoinPoint joinPoint);

}
