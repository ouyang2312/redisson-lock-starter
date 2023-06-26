package com.oy.redissonlockstarter.handler.lock;

/**
 * 锁
 *
 * @author ouyang
 * @createDate 2023/6/26 10:14
 */
public interface ILock {

    /***
     * 尝试获取锁
     *
     * @return {@link boolean}
     * @author ouyang
     * @date 2023/6/26 10:25
     */
    boolean tryLock();

    /***
     * 释放锁
     *
     * @return {@link boolean}
     * @author ouyang
     * @date 2023/6/26 10:26
     */
    boolean releaseLock();

}
