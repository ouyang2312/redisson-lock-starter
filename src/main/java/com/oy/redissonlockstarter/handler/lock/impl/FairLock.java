package com.oy.redissonlockstarter.handler.lock.impl;

import com.oy.redissonlockstarter.handler.lock.ILock;
import com.oy.redissonlockstarter.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 公平锁
 *
 * @author ouyang
 * @createDate 2023/6/26 10:18
 */
public class FairLock implements ILock {

    private RLock rLock;
    private final LockInfo lockInfo;
    private RedissonClient redissonClient;

    public FairLock(RedissonClient redissonClient, LockInfo info) {
        this.redissonClient = redissonClient;
        this.lockInfo = info;
    }

    /***
     * 尝试获取锁
     *
     * @return {@link boolean}
     * @author ouyang
     * @date 2023/6/26 10:25
     */
    @Override
    public boolean tryLock() {
        try {
            rLock = redissonClient.getFairLock(lockInfo.getName());
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /***
     * 释放锁
     *
     * @return {@link boolean}
     * @author ouyang
     * @date 2023/6/26 10:26
     */
    @Override
    public boolean releaseLock() {
        if(rLock.isHeldByCurrentThread()){

            try {
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                return false;
            }
        }
        return false;
    }
}
