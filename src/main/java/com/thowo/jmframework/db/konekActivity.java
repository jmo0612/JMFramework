package com.thowo.jmframework.db;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;
import com.thowo.jmframework.component.JMActivity;
import com.thowo.jmframework.component.JMLoadingSprite;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;


/**
 * Created by jimi on 5/31/2017.
 */

public class konekActivity extends AsyncTask {
    JMLoadingSprite mLoading;
    private boolean isView;
    private boolean isLocal;


    public konekActivity(boolean isView, boolean isLocal){
        this.isView=isView;
        this.isLocal=isLocal;
        JMActivity jmActivity=JmoFunctions.getCurrentActivity();
        if(jmActivity!=null){
            mLoading=(JMLoadingSprite) jmActivity.findViewById(R.id.jm_loading);
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Object ret=null;

        String msg="";

        if(this.isLocal){
            SQLiteDatabase db=SQLiteDatabase.openDatabase((String) params[2], null,SQLiteDatabase.OPEN_READWRITE);


            if(this.isView){

                Cursor c=null;
                try {
                    c=db.rawQuery((String) params[1],null);
                    if(!c.moveToFirst()){
                        c=null;
                    }
                }
                catch (Exception e){
                    msg+=e.toString();
                }
                if(c!=null){
                    ret= (Object) new ResultView(new jmoResultSet(true,null,c),msg);
                }else {
                    ret= (Object) new ResultView(null,msg);
                }
            }else {
                int h=0;
                try {
                    db.execSQL((String) params[1]);
                    h=1;
                }
                catch (Exception e){
                    msg+=e.toString();
                    h=0;
                }
                ret=(Object) new ResultUpdate(h,msg);
            }

        }else{
            String driverClass;
            Connection con=null;
            driverClass="com.mysql.jdbc.Driver";
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                msg+=e.toString();
            }
            ServerConnectionSetting srv= (ServerConnectionSetting) params[0];
            if(this.isView){
                ResultSet rs=null;
                try {
                    con = DriverManager.getConnection(srv.getConnectionString(),srv.getUser(),srv.getPass());
                    Statement st = con.createStatement();

                    rs = st.executeQuery((String) params[1]);
                    if(!rs.first()){
                        rs=null;
                    }
                } catch (SQLException e) {
                    msg+="\n"+e.toString();
                }
                if(rs!=null){
                    ret=(Object) new ResultView(new jmoResultSet(false,rs,null),msg);
                }else {
                    ret=(Object) new ResultView(null,msg);
                }

            }else {
                int h=0;
                try {
                    con = DriverManager.getConnection(srv.getConnectionString(),srv.getUser(),srv.getPass());
                    Statement st = con.createStatement();
                    h=st.executeUpdate((String) params[1]);
                } catch (SQLException e) {
                    msg+="\n"+e.toString();
                    h=0;
                }
                ret=(Object) new ResultUpdate(h,msg);
            }
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        if(mLoading!=null){
            mLoading.invalidate();
        }
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if(mLoading!=null){
            mLoading.showLoading();
        }
    }

    @Override
    protected void onPostExecute(Object result){
        super.onPostExecute(result);
        if(mLoading!=null){
            mLoading.hideLoading();
        }
    }


}
