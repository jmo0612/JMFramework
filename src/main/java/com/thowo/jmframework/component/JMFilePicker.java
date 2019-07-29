package com.thowo.jmframework.component;

import android.content.DialogInterface;
import android.os.Environment;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class JMFilePicker {
    private JMFilePickerListener listener;

    public JMFilePicker(JMActivity activity, String filter /* extension */, JMFilePickerListener listener){
        JMFilePicker me=this;
        setJMFilePickerListener(listener);
        new ChooserDialog().with(activity)
                .withFilter(false, false, filter)
                .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        //Toast.makeText(MainActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        //db[0] =path;
                        picked(path, pathFile);
                    }
                })
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        unpicked();
                    }
                })
                .build()
                .show();
    }

    private void picked(String path, File pathFile){
        if(listener==null)return;
        listener.onPicked(pathFile);
    }

    private void unpicked(){
        if(listener==null)return;
        listener.onCancel();
    }

    public void setJMFilePickerListener(JMFilePickerListener listener){
        this.listener=listener;
    }
}
