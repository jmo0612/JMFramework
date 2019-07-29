package com.thowo.jmframework.db;

import android.content.Context;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimi on 6/2/2017.
 */

public class ResultView {
    private jmoResultSet hsl;
    private String msg;

    public ResultView(jmoResultSet hsl, String msg){
        this.hsl=hsl;
        this.msg=msg;
    }

    public List toList(){
        List ret=null;
        if(hsl!=null){
            ret=new ArrayList();
            hsl.first();
            do{
                List cn=null;
                List cv=null;
                List ct=null;
                int num=hsl.getColumnCount();
                if(num>0){
                    cn=new ArrayList();
                    cv= new ArrayList();
                    ct=new ArrayList();
                    for(int i=0;i<num;i++){
                        cn.add((String)hsl.getColumnName(i));
                        cv.add(hsl.getValue(hsl.getColumnName(i)));
                        ct.add((int) hsl.convertedDataType(hsl.getColumnName(i)));
                    }
                }
                ret.add(new jmoRowObject(cn,cv,ct));

            }while (hsl.next());
            hsl.first();
        }
        return ret;
    }

    public jmoResultSet getResult(){
        return  hsl;
    }

    public String getMsg(){
        return msg;
    }
}
