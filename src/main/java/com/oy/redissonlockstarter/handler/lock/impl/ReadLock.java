package com.oy.redissonlockstarter.handler.lock.impl;

import com.oy.redissonlockstarter.handler.lock.ILock;
import com.oy.redissonlockstarter.model.LockInfo;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 读锁
 *
 * @author ouyang
 * @createDate 2023/6/26 10:29
 */
public class ReadLock implements ILock {

    private RReadWriteLock readWriteLock;
    private RedissonClient redissonClient;
    private final LockInfo lockInfo;

    public ReadLock(RedissonClient redissonClient, LockInfo info) {
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
            readWriteLock = redissonClient.getReadWriteLock(lockInfo.getName());
            return readWriteLock.readLock().tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
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
        if(readWriteLock.writeLock().isHeldByCurrentThread()){
            try {
                return readWriteLock.readLock().forceUnlockAsync().get();
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                return false;
            }
        }
        return false;
    }


}
