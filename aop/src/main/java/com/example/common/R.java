package com.example.common;

import lombok.Data;

@Data
public class R {

    private int code;

    private String msg;

    private Object data;

    public R(int code, String msg, Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R success(Object data){
        R r = new R(200, "成功", data);
        return r;
    }

    public static R error(){
        R r = new R(400, "失败", null);
        return r;
    }
}
