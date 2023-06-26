package com.oy.redissonlockstarter.model;

/**
 * 锁信息
 *
 * @author ouyang
 * @createDate 2023/6/26 10:02
 */
public class LockInfo {

    // 锁的类型
    private LockType type;
    // 锁的名字
    private String name;
    // 等待时间
    private long waitTime;
    // 保持时间
    private long leaseTime;

    public LockInfo(LockType type, String name, long waitTime, long leaseTime) {
        this.type = type;
        this.name = name;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
    }

    public LockInfo() {
    }

    public LockType getType() {
        return type;
    }

    public void setType(LockType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }
}
