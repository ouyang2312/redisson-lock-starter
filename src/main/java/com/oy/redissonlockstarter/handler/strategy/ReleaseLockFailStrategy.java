package com.oy.redissonlockstarter.handler.strategy;

import com.oy.redissonlockstarter.model.LockInfo;

/**
 * 释放锁
 *
 * @author ouyang
 * @createDate 2023/6/26 10:07
 */
public interface ReleaseLockFailStrategy {

    void invoke(LockInfo lockInfo);

}
