package com.haizhi.authcenter.response;

/**
 * Created by JuniFire on 2017/9/6.
 */
public class RespData {

    private Boolean error;

    private String code;

    private Object data;

    public RespData(Boolean error){
        this.error = error;
    }

    public static RespData SUCCESS(){
        return new RespData(false);
    }

    public static RespData ERROR(){
        return new RespData(true);
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public RespData setData(Object data) {
        this.data = data;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
