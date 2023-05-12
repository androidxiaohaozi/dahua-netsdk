package com.smartsite.netsdk.utils;

/**
 * 自定义服务异常
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/27 16:15
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
