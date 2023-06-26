package com.oy.redissonlockstarter.handler;

import com.oy.redissonlockstarter.handler.lock.ILock;
import com.oy.redissonlockstarter.handler.lock.impl.FairLock;
import com.oy.redissonlockstarter.handler.lock.impl.ReadLock;
import com.oy.redissonlockstarter.handler.lock.impl.ReentrantLock;
import com.oy.redissonlockstarter.handler.lock.impl.WriteLock;
import com.oy.redissonlockstarter.model.LockInfo;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 锁工厂
 *
 * @author ouyang
 * @createDate 2023/6/26 13:52
 */
public class LockFactory {

    @Autowired
    private RedissonClient redissonClient;

    public ILock getLock(LockInfo lockInfo){
        switch (lockInfo.getType()) {
            case REENTRANT:
                return new ReentrantLock(redissonClient, lockInfo);
            case FAIR:
                return new FairLock(redissonClient, lockInfo);
            case Read:
                return new ReadLock(redissonClient, lockInfo);
            case Write:
                return new WriteLock(redissonClient, lockInfo);
            default:
                return new ReentrantLock(redissonClient, lockInfo);
        }
    }


}
