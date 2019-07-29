package com.thowo.jmframework.component;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;

/**
 * Created by jimi on 8/16/2017.
 */

public class JMActivity extends AppCompatActivity{
    private LinearLayout mContent;
    private JMLoadingSprite mLoading;
    private int contentViewR=R.layout.activity_jm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JmoFunctions.update(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(contentViewR);
        mLoading=(JMLoadingSprite) findViewById(R.id.jm_loading);
        mContent=(LinearLayout) findViewById(R.id.jm_content);
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        inflater.inflate(layoutResID,mContent);
    }


    public void setContentView(int layoutResID, boolean scrollable){
        if(scrollable){
            contentViewR=R.layout.activity_jm;
        }else{
            contentViewR=R.layout.activity_jm_unscrollable;
        }
        setContentView(layoutResID);
    }

    @Override
    public void onResume(){
        super.onResume();
        JmoFunctions.update(this);
    }

    public JMLoadingSprite getLoadingSprite(){
        return mLoading;
    }


}
