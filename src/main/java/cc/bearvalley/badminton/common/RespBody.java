package cc.bearvalley.badminton.common;

import java.io.Serializable;

/**
 * API接口返回统一格式
 * @param <T> 数据接口
 */
public class RespBody<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T      data;   //服务端数据
    private int    code;   //状态码，0：成功，1：失败
    private String msg;    //描述信息

    public static RespBody isOk() {
        return new RespBody().code(Code.SUCCESS.code).msg(Code.SUCCESS.msg);
    }

    public static RespBody isFail() {
        return new RespBody().code(Code.ERROR.code).msg(Code.ERROR.msg);
    }

    public static RespBody noEntity() {
        return new RespBody().code(Code.SUCCESS.code).msg(Code.NO_ENTITY.msg);
    }

    public RespBody data(T data) {
        this.setData(data);
        return this;
    }

    public RespBody msg(String msg){
        this.setMsg(msg);
        return this;
    }

    public RespBody msg(Throwable e) {
        this.setMsg(e.toString());
        return this;
    }

    public RespBody code(int code) {
        this.setCode(code);
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static enum Code{
        SUCCESS(0, "success"),
        ERROR(1, "error"),
        NAME_CONFLICT(2, "该名字已经存在"),
        NO_ENTITY(3, "该实体不存在");
        final int code;
        final String msg;
        Code(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }
    }
}