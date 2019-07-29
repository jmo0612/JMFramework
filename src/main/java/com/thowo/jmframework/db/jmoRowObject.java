package com.thowo.jmframework.db;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.thowo.jmframework.JmoFormatCollection;
import com.thowo.jmframework.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

/**
 * Created by jimi on 6/6/2017.
 */

public class jmoRowObject implements Parcelable{
    private List name;
    private List value;
    private List type;

    /*======================DATA TYPE===============

    0=string
    1=int
    2=Double
    3=object

    10=Boolean
    11=Date
    12=DateTime
     */
    public static final int JMO_STRING = 0;
    public static final int JMO_INT = 1;
    public static final int JMO_DOUBLE = 2;
    public static final int JMO_OBJECT = 3;
    public static final int JMO_BOOLEAN = 10;
    public static final int JMO_DATE = 11;
    public static final int JMO_DATETIME = 12;


    public jmoRowObject(List name, List value){
        this.name=name;
        this.value=value;
        if(this.name!=null){
            type=new ArrayList();
            for(int i=0;i<this.name.size();i++){
                type.add(JMO_STRING);
            }
        }

    }

    public jmoRowObject(List name, List value, List type){
        this.name=name;
        this.value=value;
        this.type=type;
    }

    public void setDataType(String columnName, int dataType){
        int index=getIndex(columnName);
        setDataType(index,dataType);
    }

    public void setDataType(int index, int dataType){
        if(index>=0 && index<this.name.size()){
            this.type.set(index,dataType);
        }
    }

    public int getDataType(String columnName){
        int index=getIndex(columnName);
        return getDataType(index);
    }

    public int getDataType(int index){
        if(index>=0 && index<this.name.size()){
            return (int)this.type.get(index);
        }else{
            return -1;
        }
    }

    public Object getValue(String nm){
        return this.value.get(this.getIndex(nm));
    }

    public Object getValue(int index){
        return this.value.get(index);
    }


    public String getDBString(String nm){
        if(this.value.get(this.getIndex(nm))==null){
            return "";
        }else{
            return String.valueOf(this.value.get(this.getIndex(nm)));
        }
    }

    public String getDBString(String nm, int JMO_STRING_JmoFormatCollection){
        if(this.value.get(this.getIndex(nm))==null){
            return "";
        }else{
            return JmoFormatCollection.stringWithFormat(String.valueOf(this.value.get(this.getIndex(nm))),JMO_STRING_JmoFormatCollection);
        }
    }

    public String getDBString(int index){
        if(this.value.get(index)==null){
            return "";
        }else{
            return String.valueOf(this.value.get(index));
        }
    }

    public String getDBString(int index, int JMO_STRING_JmoFormatCollection){
        if(this.value.get(index)==null){
            return "";
        }else{
            return JmoFormatCollection.stringWithFormat(String.valueOf(this.value.get(index)),JMO_STRING_JmoFormatCollection);
        }
    }


    public int getIndex(String columnName){
        return name.indexOf(columnName);
    }

    public String getColumnName(int index){
        if(index<0 || index>value.size()-1)return "";
        return (String) name.get(index);
    }


    public void setValue(String nm, Object vl){
        int ind=this.getIndex(nm);
        this.value.set(ind,vl);
    }

    public void setValue(int index, Object vl){
        this.value.set(index,vl);
    }

    public int getCount(){
        if(name!=null){
            return name.size();
        }else{
            return 0;
        }
    }



    public static final Creator<jmoRowObject> CREATOR = new Creator<jmoRowObject>() {
        @Override
        public jmoRowObject createFromParcel(Parcel in) {

            List nm=new ArrayList();
            List val=new ArrayList();
            List tp=new ArrayList();
            in.readList(nm,List.class.getClassLoader());
            in.readList(val,List.class.getClassLoader());
            in.readList(tp,List.class.getClassLoader());
            jmoRowObject ret=new jmoRowObject(nm,val,tp);
            return ret;
        }

        @Override
        public jmoRowObject[] newArray(int size) {
            return new jmoRowObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.name);
        dest.writeList(this.value);
        dest.writeList(this.type);
    }


}
