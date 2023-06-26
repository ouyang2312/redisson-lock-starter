package com.oy.redissonlockstarter.model;

/**
 * 锁的类型
 *
 * @author ouyang
 * @createDate 2023/6/26 9:36
 */
public enum LockType {

    // 公平锁
    FAIR,
    // 可重入锁
    REENTRANT,
    // 读锁
    Read,
    // 写锁
    Write;
}
