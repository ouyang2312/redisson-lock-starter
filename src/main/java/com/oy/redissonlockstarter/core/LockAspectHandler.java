package com.oy.redissonlockstarter.core;

import com.oy.redissonlockstarter.annotion.Lock;
import com.oy.redissonlockstarter.exception.LockInvocationException;
import com.oy.redissonlockstarter.handler.LockFactory;
import com.oy.redissonlockstarter.handler.lock.ILock;
import com.oy.redissonlockstarter.model.LockInfo;
import com.oy.redissonlockstarter.util.StrUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 给 @Lock 加入切面处理
 * order 给最小，比事务要先
 *
 * @author ouyang
 * @createDate 2023/6/26 13:39
 */
@Aspect
@Order(0)
public class LockAspectHandler {

    private static final Logger logger = LoggerFactory.getLogger(LockAspectHandler.class);

    @Autowired
    LockFactory lockFactory;
    @Autowired
    private LockInfoProvider lockInfoProvider;

    // 存放锁
    private final Map<String, LockRes> currentThreadLock = new ConcurrentHashMap<>();


    @Around(value = "@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        LockInfo lockInfo = lockInfoProvider.getLockInfo(joinPoint, lock);
        String curentLock = this.getCurrentLockId(joinPoint, lock);
        currentThreadLock.put(curentLock, new LockRes(lockInfo, false));
        ILock iLock = lockFactory.getLock(lockInfo);
        boolean lockRes = iLock.tryLock();

        //如果获取锁失败了，则进入失败的处理逻辑
        if (!lockRes) {
            if (logger.isWarnEnabled()) {
                logger.warn("Timeout while acquiring Lock({})", lockInfo.getName());
            }
            //如果自定义了获取锁失败的处理策略，则执行自定义的降级处理策略
            if (StrUtil.isNotBlank(lock.customLockTimeoutStrategy())) {
                return handleCustomLockTimeout(lock.customLockTimeoutStrategy(), joinPoint);
            } else {
                //否则执行预定义的执行策略
                //注意：如果没有指定预定义的策略，默认的策略为静默啥不做处理
                lock.lockTimeoutStrategy().invoke(lockInfo, iLock, joinPoint);
            }
        }

        currentThreadLock.get(curentLock).setLock(iLock);
        currentThreadLock.get(curentLock).setRes(true);

        return joinPoint.proceed();
    }

    @AfterReturning(value = "@annotation(lock)")
    public void afterReturning(JoinPoint joinPoint, Lock lock) throws Throwable {
        String curentLock = this.getCurrentLockId(joinPoint, lock);
        releaseLock(lock, joinPoint, curentLock);
        cleanUpThreadLocal(curentLock);
    }


    @AfterThrowing(value = "@annotation(lock)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Lock lock, Throwable ex) throws Throwable {
        String curentLock = this.getCurrentLockId(joinPoint, lock);
        releaseLock(lock, joinPoint, curentLock);
        cleanUpThreadLocal(curentLock);
        throw ex;
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res = null;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /**
     * 释放锁
     */
    private void releaseLock(Lock lock, JoinPoint joinPoint, String curentLock) throws Throwable {
        LockRes lockRes = currentThreadLock.get(curentLock);
        if (Objects.isNull(lockRes)) {
            throw new NullPointerException("Please check whether the input parameter used as the lock key value has been modified in the method, which will cause the acquire and release locks to have different key values and throw null pointers.curentLockKey:" + curentLock);
        }
        if (lockRes.getRes()) {
            boolean releaseRes = currentThreadLock.get(curentLock).getLock().releaseLock();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {
                handleReleaseTimeout(lock, lockRes.getLockInfo(), joinPoint);
            }
        }
    }


    private void cleanUpThreadLocal(String curentLock) {
        currentThreadLock.remove(curentLock);
    }

    /**
     * 获取当前锁在map中的key
     *
     * @param joinPoint
     * @param lock
     * @return
     */
    private String getCurrentLockId(JoinPoint joinPoint, Lock lock) {
        LockInfo lockInfo = lockInfoProvider.getLockInfo(joinPoint, lock);
        String curentLock = Thread.currentThread().getId() + lockInfo.getName();
        return curentLock;
    }

    /**
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(Lock lock, LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {
        if (logger.isWarnEnabled()) {
            logger.warn("Timeout while release Lock({})", lockInfo.getName());
        }
        if (StrUtil.isNotBlank(lock.customReleaseTimeoutStrategy())) {
            handleCustomReleaseTimeout(lock.customReleaseTimeoutStrategy(), joinPoint);
        } else {
            lock.releaseTimeoutStrategy().invoke(lockInfo);
        }
    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }



    private class LockRes {
        private LockInfo lockInfo;
        private ILock lock;
        // 加锁结果
        private Boolean res;

        LockRes(LockInfo lockInfo, Boolean res) {
            this.lockInfo = lockInfo;
            this.res = res;
        }

        LockInfo getLockInfo() {
            return lockInfo;
        }

        public ILock getLock() {
            return lock;
        }

        public void setLock(ILock lock) {
            this.lock = lock;
        }

        Boolean getRes() {
            return res;
        }

        void setRes(Boolean res) {
            this.res = res;
        }

        void setLockInfo(LockInfo lockInfo) {
            this.lockInfo = lockInfo;
        }
    }

}
