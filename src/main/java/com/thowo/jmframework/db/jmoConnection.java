package com.thowo.jmframework.db;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;
import com.thowo.jmframework.component.JMLoadingSprite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by jimi on 6/3/2017.
 */

public class jmoConnection {
    private Context context;
    private ServerConnectionSetting srv;
    private String localDBPath;
    private String localDBDir;


    public jmoConnection(String localDBName, int localDBResID, ServerConnectionSetting serverConnectionSetting){
        newJmoConnection(false,localDBName,localDBResID,serverConnectionSetting);
    }

    public jmoConnection(boolean publicLocalDB, String localDBName, int localDBResID, ServerConnectionSetting serverConnectionSetting){
        newJmoConnection(publicLocalDB, localDBName,localDBResID, serverConnectionSetting);
    }

    public jmoConnection(File localDBFile){
        this.context= JmoFunctions.getCurrentContext();
        this.localDBDir=localDBFile.getParent();
        this.localDBPath=localDBFile.getAbsolutePath();
    }

    public void newJmoConnection(boolean publicLocalDB, String localDBName, int localDBResID, ServerConnectionSetting serverConnectionSetting){
        //newJmoConnection(publicLocalDB);

        this.context= JmoFunctions.getCurrentContext();
        srv=serverConnectionSetting;
        if(!publicLocalDB){
            this.localDBDir=context.getFilesDir().toString();
            this.localDBPath=this.localDBDir + "/" + localDBName;
        }else{
            this.localDBDir=context.getExternalFilesDir("database").toString();
            this.localDBPath=this.localDBDir + "/" + localDBName;
        }
        //this.srv=new ServerConnectionSetting(context.getString(R.string.server_host),context.getString(R.string.server_port),context.getString(R.string.server_db),context.getString(R.string.server_user),context.getString(R.string.server_pass));
        this.srv=serverConnectionSetting;
        this.validateLocalDB(localDBResID);
    }

    public void setNewContext(Context ctx){
        this.context=ctx;
    }

    public ServerConnectionSetting getServerConnectionString(){
        return srv;
    }

    private void makeDir(String dir){
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    private void validateLocalDB(int localDBResID){
        if (!new File(this.localDBPath).exists()) {
            try {
                FileOutputStream out = new FileOutputStream(this.localDBPath);
                InputStream in = this.context.getResources().openRawResource(localDBResID);


                byte[] buffer = new byte[1024];
                int readBytes = 0;

                try {
                    while ((readBytes = in.read(buffer)) > 0) {
                        out.write(buffer, 0, readBytes);
                    }
                } finally {
                    out.flush();
                    out.close();
                    in.close();

                }
            }catch (Exception e){
                Toast.makeText(this.context,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void toastError(String er){
        Toast.makeText(this.context,er,Toast.LENGTH_LONG).show();
    }

    public ResultView queryLocal(String sql){
        ResultView ret=null;
        Object[] prm={(Object)this.srv,(Object)sql, (Object) this.localDBPath};
        Object asRes=null;
        try {
            try {
                asRes= new konekActivity(true,true).execute(prm).get(60L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                asRes=null;
                this.toastError(e.getMessage());
            }
        } catch (InterruptedException e) {
            asRes=null;
            this.toastError(e.getMessage());
        } catch (ExecutionException e) {
            asRes=null;
            this.toastError(e.getMessage());
        }
        ret=(ResultView) asRes;
        return ret;
    }

    public ResultView queryServer(String sql){
        ResultView ret=null;
        Object[] prm={(Object)this.srv,(Object)sql, (Object) this.localDBPath};
        Object asRes=null;
        try {
            try {
                asRes= new konekActivity(true,false).execute(prm).get(60L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                asRes=null;
                this.toastError(e.getMessage());
            }
        } catch (InterruptedException e) {
            asRes=null;
            this.toastError(e.getMessage());
        } catch (ExecutionException e) {
            asRes=null;
            this.toastError(e.getMessage());
        }
        ret=(ResultView) asRes;
        return ret;
    }

    public ResultUpdate queryUpdateLocal(String sql){
        ResultUpdate ret=null;
        Object[] prm={(Object)this.srv,(Object)sql, (Object) this.localDBPath};
        Object asRes=null;
        try {
            try {
                asRes= new konekActivity(false,true).execute(prm).get(60L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                asRes=null;
                this.toastError(e.getMessage());
            }
        } catch (InterruptedException e) {
            asRes=null;
            this.toastError(e.getMessage());
        } catch (ExecutionException e) {
            asRes=null;
            this.toastError(e.getMessage());
        }
        ret=(ResultUpdate) asRes;
        return ret;
    }

    public ResultUpdate queryUpdateServer(String sql){
        ResultUpdate ret=null;
        Object[] prm={(Object)this.srv,(Object)sql, (Object) this.localDBPath};
        Object asRes=null;
        try {
            try {
                asRes= new konekActivity(false,false).execute(prm).get(60L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                asRes=null;
                this.toastError(e.getMessage());
            }
        } catch (InterruptedException e) {
            asRes=null;
            this.toastError(e.getMessage());
        } catch (ExecutionException e) {
            asRes=null;
            this.toastError(e.getMessage());
        }
        ret=(ResultUpdate) asRes;
        return ret;
    }
}
