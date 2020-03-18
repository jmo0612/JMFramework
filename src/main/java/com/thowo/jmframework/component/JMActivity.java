package com.thowo.jmframework.component;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private String title;


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


    public void setContentView(int layoutResID,int titleRes, boolean scrollable){
        if(scrollable){
            contentViewR=R.layout.activity_jm;
        }else{
            contentViewR=R.layout.activity_jm_unscrollable;
        }
        this.title=getString(titleRes);
        String tmp= getString(R.string.app_name);
        if(!title.equals("")){
            tmp+=" - "+title;
        }
        setTitle(tmp);
        setContentView(layoutResID);
    }

    public void confirmExit(String msg){
        confirmExit("Konfirmasi", msg, ContextCompat.getDrawable(this, R.drawable.icon_question), "Ya", "Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==AlertDialog.BUTTON_POSITIVE)finishIt();
            }
        });
    }

    public void confirmExit(final String title, final String msg, final Drawable resIcon, final String positive, final String negative, DialogInterface.OnClickListener onClickListener){
        new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.Theme_AppCompat_Dialog_Alert))
                .setTitle(title)
                .setMessage(msg)
                .setIcon(resIcon)
                .setPositiveButton(positive, onClickListener)
                .setNegativeButton(negative, onClickListener).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        JmoFunctions.update(this);
    }


    public void finishIt(){
        super.finish();
    }


    public JMLoadingSprite getLoadingSprite(){
        return mLoading;
    }


}
