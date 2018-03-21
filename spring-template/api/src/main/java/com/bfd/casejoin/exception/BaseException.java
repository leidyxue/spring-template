package com.bfd.casejoin.exception;

public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 5440592369178141702L;

    public BaseException() {
        super();
    }

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(String msg, Throwable th) {
        super(msg, th);
    }

    public abstract String getCode();
}
