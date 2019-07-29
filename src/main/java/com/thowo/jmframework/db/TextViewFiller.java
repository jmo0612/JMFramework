package com.thowo.jmframework.db;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by jimi on 6/6/2017.
 */

public class TextViewFiller {
    private TextView tv;
    private EditText edt;
    private Object object;
    private String format;
    private int dataType;

    private String rsltData;
    private String rsltFormated;

    public static final int FILLER_TEXT_VIEW=0;
    public static final int FILLER_EDIT_TEXT=1;

    public TextViewFiller(Object object, EditText editText, String format, int dataType){
        this.edt=editText;
        this.object=object;
        this.newTextViewFiller(format, dataType, FILLER_EDIT_TEXT);
    }

    public TextViewFiller(Object object, TextView tv, String format, int dataType){
        this.tv=tv;
        this.object=object;
        this.newTextViewFiller(format, dataType, FILLER_TEXT_VIEW);
    }


    public void newTextViewFiller(String format, int dataType, int fillTo){
        this.format=format;
        this.dataType=dataType;

        if(dataType==jmoRowObject.JMO_STRING){
            this.assignString(fillTo);
        }else if(dataType==jmoRowObject.JMO_BOOLEAN){
            this.assignBoolean(fillTo);
        }else if(dataType==jmoRowObject.JMO_DATE){
            this.assignDate(fillTo);
        }else if(dataType==jmoRowObject.JMO_DATETIME){
            this.assignDateTime(fillTo);
        }else if(dataType==jmoRowObject.JMO_DOUBLE){
            this.assignDouble(fillTo);
        }else if(dataType==jmoRowObject.JMO_INT){
            this.assignInt(fillTo);
        }else{
            this.assignObject(fillTo);
        }

    }

    private void assignString(int fillTo){
        try {
            this.rsltData=String.valueOf(this.object);
            this.rsltFormated=rsltData;
        }catch (Exception e){
            this.rsltFormated=e.getMessage();
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }

    private void assignInt(int fillTo){
        try {
            int tmp=(int) this.object;
            this.rsltData=String.valueOf(tmp);
            if(this.format=="" || this.format==null){
                this.rsltFormated=this.rsltData;
            }else{
                DecimalFormat formatter= new DecimalFormat(this.format);
                this.rsltFormated=formatter.format(tmp);
            }
        }catch (Exception e){
            this.rsltFormated=e.getMessage();
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }

    private void assignDouble(int fillTo){
        try {
            Double tmp=(Double) this.object;
            this.rsltData=String.valueOf(tmp);
            if(this.format=="" || this.format==null){
                this.rsltFormated=this.rsltData;
            }else{
                DecimalFormat formatter= new DecimalFormat(this.format);
                this.rsltFormated=formatter.format(tmp);
            }
        }catch (Exception e){
            this.rsltFormated=e.getMessage();
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }

    private void assignObject(int fillTo){
        try {
            this.rsltData=String.valueOf(object);
            this.rsltFormated=this.rsltData;
        }catch (Exception e){
            this.rsltFormated=e.getMessage();
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }

    private void assignBoolean(int fillTo){
        try {
            boolean tmp=(boolean) this.object;
            this.rsltData=String.valueOf(tmp);
            this.rsltFormated=this.rsltData;
        }catch (Exception e){
            this.rsltFormated=e.getMessage();
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }

    private void assignDate(int fillTo){
        String tmp;
        try {
            tmp=(String) this.object;
            DateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
            Date dt=(Date) formatter.parse(tmp);
            this.rsltData=tmp;
            if(this.format=="" || this.format==null){
                this.rsltFormated=this.rsltData;
            }else{
                this.rsltFormated=android.text.format.DateFormat.format(this.format,dt).toString();
            }

        }catch (Exception e){
            //this.rsltFormated=e.getMessage();
            this.rsltFormated="";
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }

    private void assignDateTime(int fillTo){
        try {
            String tmp=(String) this.object;
            DateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt=(Date) formatter.parse(tmp);
            this.rsltData=tmp;
            if(this.format=="" || this.format==null){
                this.rsltFormated=this.rsltData;
            }else{
                this.rsltFormated=android.text.format.DateFormat.format(this.format,dt).toString();
            }
        }catch (Exception e){
            this.rsltFormated=e.getMessage();
        }
        if(fillTo==FILLER_EDIT_TEXT){
            edt.setText(this.rsltFormated);
        }else if(fillTo==FILLER_TEXT_VIEW){
            tv.setText(this.rsltFormated);
        }
    }
}
