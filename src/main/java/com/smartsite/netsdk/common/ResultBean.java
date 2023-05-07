package com.smartsite.netsdk.common;

import com.smartsite.netsdk.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 8:56
 */
public class ResultBean extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 0;

    public static final int FAIL = -1;
    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 ResultBean 对象，使其表示一个空消息。
     */
    public ResultBean() {
    }

    /**
     * 初始化一个新创建的 ResultBean 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public ResultBean(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 ResultBean 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public ResultBean(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data)) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ResultBean success() {
        return ResultBean.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static ResultBean success(Object data) {
        return ResultBean.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static ResultBean success(String msg) {
        return ResultBean.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static ResultBean success(String msg, Object data) {
        return new ResultBean(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static ResultBean error() {
        return ResultBean.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ResultBean error(String msg) {
        return ResultBean.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static ResultBean error(String msg, Object data) {
        return new ResultBean(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static ResultBean error(int code, String msg) {
        return new ResultBean(code, msg, null);
    }

    /**
     * 方便链式调用
     *
     * @param key   键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public ResultBean put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
