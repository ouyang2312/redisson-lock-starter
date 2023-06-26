package com.oy.redissonlockstarter.exception;

/**
 * 锁调用超时异常
 *
 * @author ouyang
 * @createDate 2023/6/26 11:23
 */
public class LockInvocationException extends RuntimeException{

    private String message;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public LockInvocationException(String message) {
        this.message = message;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public LockInvocationException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }
}
