package com.thowo.jmframework.component;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;
import com.thowo.jmframework.db.jmoRowObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by jimi on 7/7/2017.
 */

public class JMListViewCache {
    private View baseView;
    private List<View> vg;
    private List<String> vgTag;


    public JMListViewCache ( View baseView) {

        this.baseView = baseView;
        vg=new ArrayList<View>();
        vgTag=new ArrayList<String>();

    }

    public View getViewBase ( ) {
        return baseView;
    }

    public View getView(int columnIndex, String columnName){

        int index=vgTag.indexOf(String.valueOf(columnIndex));

        if(index==-1){
            if(!columnName.equals("")){
                index=vgTag.indexOf(columnName);
            }
        }

        View ret=null;
        String tmp="";

        if(index==-1){
            ViewGroup base=(ViewGroup) baseView;
            ret=(View) baseView.findViewWithTag(String.valueOf(columnIndex));
            if(ret==null){
                ret=(View) baseView.findViewWithTag(columnName);
                tmp=columnName;
            }else{
                tmp=String.valueOf(columnIndex);
            }
            if(ret==null)return null;
            vgTag.add(tmp);
            vg.add(ret);
        }else{
            ret=vg.get(index);
        }

        return ret;
    }


}
