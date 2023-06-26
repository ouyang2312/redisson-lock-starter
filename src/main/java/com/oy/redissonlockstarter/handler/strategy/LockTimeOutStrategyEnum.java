package com.oy.redissonlockstarter.handler.strategy;

import com.oy.redissonlockstarter.exception.LockTimeOutException;
import com.oy.redissonlockstarter.handler.lock.ILock;
import com.oy.redissonlockstarter.model.LockInfo;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.TimeUnit;

/**
 * 锁超时策略
 *
 * @author ouyang
 * @createDate 2023/6/26 11:19
 */
public enum LockTimeOutStrategyEnum implements LockTimeOutStrategy{

    /** 不处理 */
    NO_OPERATION(){
        @Override
        public void invoke(LockInfo lockInfo, ILock lock, JoinPoint joinPoint) {
            // 不操作
        }
    },

    /** 抛异常 */
    FAIL_EXCEPTION(){
        @Override
        public void invoke(LockInfo lockInfo, ILock lock, JoinPoint joinPoint) {
            String msg = String.format("Failed to tryLock(%s) with timeout(%ds)", lockInfo.getName(), lockInfo.getWaitTime());
            throw new LockTimeOutException(msg);
        }
    },

    /** 继续等待 */
    KEEP_TRY(){
        private static final long DEFAULT_INTERVAL = 100L;
        private static final long DEFAULT_MAX_INTERVAL = 30 * 1000L;

        @Override
        public void invoke(LockInfo lockInfo, ILock lock, JoinPoint joinPoint) {
            long interval = DEFAULT_INTERVAL;
            while(!lock.tryLock()) {
                if(interval > DEFAULT_MAX_INTERVAL) {
                    String errorMsg = String.format("Failed to tryLock(%s) after too many times, this may because dead lock occurs.",
                            lockInfo.getName());
                    throw new LockTimeOutException(errorMsg);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    // 乘以2
                    interval <<= 1;
                } catch (InterruptedException e) {
                    throw new LockTimeOutException("Failed to tryLock", e);
                }
            }
        }
    }
    ;


}
