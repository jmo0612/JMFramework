package com.thowo.jmframework.db;

/**
 * Created by jimi on 6/2/2017.
 */

public class ResultUpdate {
    private int hsl;
    private String msg;

    public ResultUpdate(int hsl, String msg){
        this.hsl=hsl;
        this.msg=msg;
    }

    public int getResult(){
        return  hsl;
    }

    public String getMsg(){
        return msg;
    }
}
