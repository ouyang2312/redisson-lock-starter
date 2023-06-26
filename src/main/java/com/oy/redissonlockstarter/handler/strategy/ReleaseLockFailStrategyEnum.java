package com.oy.redissonlockstarter.handler.strategy;

import com.oy.redissonlockstarter.exception.LockTimeOutException;
import com.oy.redissonlockstarter.model.LockInfo;

/**
 * 信息描述
 *
 * @author ouyang
 * @createDate 2023/6/26 12:21
 */
public enum ReleaseLockFailStrategyEnum implements ReleaseLockFailStrategy{

    NO_OPERATION(){
        @Override
        public void invoke(LockInfo lockInfo) {
            // 不操作
        }
    },

    /**
     * 快速失败
     */
    FAIL_EXCEPTION() {
        @Override
        public void invoke(LockInfo lockInfo) {
            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            throw new LockTimeOutException(errorMsg);
        }
    }

}
