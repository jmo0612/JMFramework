package com.thowo.jmframework;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.thowo.jmframework.component.JMActivity;
import com.thowo.jmframework.component.JMLoadingSprite;

/**
 * Created by jimi on 8/16/2017.
 */

public class jmoAsyncTask extends AsyncTask {
    private JMActivity mActivity;
    private Context mContext;
    private JMLoadingSprite mLoading;
    private jmoAsyncTaskDoInBackground mBackgroundCB;

    public interface jmoAsyncTaskDoInBackground{
        Object doBackground(Object[] objects);
    }

    public jmoAsyncTask(JMActivity activity){
        mActivity=activity;
        mContext=activity.getBaseContext();
        mLoading=activity.getLoadingSprite();
        mBackgroundCB=(jmoAsyncTaskDoInBackground) activity;

    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return mBackgroundCB.doBackground(objects);
        //return null;
    }


    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        mLoading.invalidate();
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        mLoading.showLoading();
    }

    @Override
    protected void onPostExecute(Object result){
        super.onPostExecute(result);
        mLoading.hideLoading();
    }
}
