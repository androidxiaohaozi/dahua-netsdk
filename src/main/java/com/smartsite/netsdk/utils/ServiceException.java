package com.smartsite.netsdk.utils;

/**
 * 自定义服务异常
 * @author Yan Xu
 * @version 1.0
 * @date 2023/4/19
 */
public class ServiceException extends RuntimeException{

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
