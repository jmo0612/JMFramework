package com.thowo.jmframework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.thowo.jmframework.component.JMActivity;
import com.thowo.jmframework.db.ServerConnectionSetting;
import com.thowo.jmframework.db.jmoConnection;
import com.thowo.jmframework.db.jmoRowObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by jimi on 8/2/2017.
 */

public class JmoFunctions {
    private static Context currentContext;
    private static JMActivity currentActivity;
    private static ServerConnectionSetting serverConnectionSetting;
    private static jmoConnection mDBConnection;


    public static Object now(int calenderField){
        return Calendar.getInstance().get(calenderField);
    }

    public static void toast(String msg){
        Toast.makeText(getCurrentContext(),msg,Toast.LENGTH_LONG).show();
    }

    public static void displayTitle(String title){
        String tmp= getCurrentActivity().getString(R.string.app_name);
        if(!title.equals("")){
            tmp+=" - "+title;
        }
        getCurrentActivity().setTitle(tmp);
    }

    public static void showActivity(Class cls){
        Intent intent=new Intent(getCurrentActivity().getApplicationContext(),cls);
        showActivity(intent);
    }

    public static void showActivity(Intent intent){
        getCurrentActivity().startActivity(intent);
    }

    public static ServerConnectionSetting getServerConnectionSetting(){
        return serverConnectionSetting;
    }

    public static ServerConnectionSetting getServerConnectionSetting(ServerConnectionSetting srv){
        serverConnectionSetting=srv;
        return serverConnectionSetting;
    }

    public static jmoConnection getCurrentConnection(){
        return mDBConnection;
    }

    public static jmoConnection getCurrentConnection(jmoConnection newJmoConnection){
        mDBConnection=newJmoConnection;
        serverConnectionSetting=mDBConnection.getServerConnectionString();
        return mDBConnection;
    }

    public static void reset(){
        currentContext=null;
        currentActivity=null;
    }

    public static void update(JMActivity activity){
        if(activity!=null){
            currentContext=activity.getBaseContext();
            currentActivity=activity;
        }
    }

    public static Context getCurrentContext(){
        return currentContext;
    }

    public static JMActivity getCurrentActivity(){
        return currentActivity;
    }

    public static void trace(Object o){
        Log.d(TAG, "trace: "+o);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static boolean fileExist(File file){
        if(file==null)return false;
        if(file.exists())return true;
        return false;
    }

    public static boolean deleteFile(File file){
        if(!fileExist(file))return false;
        return file.delete();
    }

    public static boolean createFile(File file){
        if(fileExist(file))return true;
        String dirs=file.getParent();
        File dir=new File(dirs);
        dir.mkdirs();
        if(!fileExist(dir)) return false;
        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void overrideJmoRowObjectListType(List<jmoRowObject> rObjs, String colName, int dataType){
        if(rObjs==null)return;
        for(int i=0;i<rObjs.size();i++){
            rObjs.get(i).setDataType(colName,dataType);
        }
    }

    public static List<File> listFiles(File dir){
        List<File> ret= new ArrayList<>();
        if(!fileExist(dir)) return ret;
        File[] files=dir.listFiles();
        for(File f:files){
            ret.add(f);
        }
        return ret;
    }

    public static String readTxtFile(File file){
        return readTxtFile(file, "", "");
    }

    public static String readTxtFile(File file, String var){
        return readTxtFile(file,var,"=");
    }

    public static String readTxtFile(File file, String var, String operator){
        if(file==null)return "";
        if(!fileExist(file))return "";
        //Context ctx=getCurrentActivity().getApplicationContext();
        try {
            //FileInputStream fileInputStream=ctx.openFileInput(file.getAbsolutePath());
            FileInputStream fileInputStream=new FileInputStream(file);
            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            try {
                String ret="";
                String lineData=bufferedReader.readLine();
                while (lineData!=null){
                    trace("LINE: "+lineData);
                    if(var.equals("")){
                        if(!ret.equals(""))ret+="\n";
                        ret+=lineData;
                    }else{
                        trace("SUBSTR: "+lineData.substring(0,var.length()+operator.length()));
                        if(lineData.substring(0,var.length()+operator.length()).equals(var+operator)){
                            return lineData.substring(var.length()+operator.length());
                        }
                    }
                    lineData=bufferedReader.readLine();
                }
                fileInputStream.close();
                return ret;

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean writeTxtFile(String path, String var, String data){
        return writeTxtFile(path,var,"=",data);
    }

    public static boolean writeTxtFile(String path, String data){
        return writeTxtFile(path,"","",data);
    }

    public static boolean writeTxtFile(String path, String var, String operator, String data){
        File file=new File(path);
        if(!fileExist(file)){
            //create
            if(!createFile(file)) return false;
        }
        try {
            String tmp="";
            if(!var.equals("")){
                FileInputStream fileInputStream=new FileInputStream(file);
                InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                try {
                    String lineData=bufferedReader.readLine();
                    boolean edited=false;
                    while (lineData!=null){
                        if(!tmp.equals(""))tmp+="\n";
                        if(lineData.substring(0,var.length()+operator.length()).equals(var+operator)){
                            tmp+=var+operator+data;
                            edited=true;
                        } else {
                            tmp+=lineData;
                        }
                        lineData=bufferedReader.readLine();
                    }
                    if(!edited){
                        if(!tmp.equals(""))tmp+="\n";
                        tmp+=var+operator+data;
                    }
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }else{
                tmp=data;
            }


            FileOutputStream fileOutputStream=new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
            try {
                bufferedWriter.write(tmp);
                trace("TMP: "+tmp);
                bufferedWriter.close();
                fileOutputStream.close();
                trace("saved");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    
    
    
    
    // INTENT ========================================================================================================================================================
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, boolean message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, byte message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, char message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, short message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, int message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, long message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, float message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, double message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, String message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, CharSequence message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, Parcelable message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, Parcelable[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, Serializable message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, boolean[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, byte[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, short[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, char[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, int[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, long[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, float[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, double[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, String[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, CharSequence[] message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    public Intent intentWithMessage(Class cls, String extraName /* ex:Intent.EXTRA_MESSAGE, com.thowo.myapp.MESSAGE */, Bundle message){
        Intent ret=new Intent(getCurrentActivity().getApplicationContext(),cls);
        if(!extraName.equals(""))ret.putExtra(extraName,message);
        return ret;
    }
    // END INTENT ========================================================================================================================================================
    
    
    

}
