package com.thowo.jmframework.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thowo.jmframework.R;
import com.thowo.jmframework.db.TextViewFiller;

/**
 * Created by jimi on 6/30/2017.
 */

public class JMLoadingSprite extends FrameLayout {
    private String value;
    private String format;
    private String font;
    private int dataType;
    private TextView tv;

    public JMLoadingSprite(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.loading_sprite,this);
        tv=(TextView) findViewById(R.id.messageLS);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JMView);
        int count = typedArray.getIndexCount();
        try{

            for (int i = 0; i < count; ++i) {

                int attr = typedArray.getIndex(i);
                // the attr corresponds to the title attribute
                if(attr == R.styleable.JMView_text) {
                    value=typedArray.getString(attr);
                    displayText(null,-1);
                }else if(attr == R.styleable.JMView_fontTTF) {
                    font=typedArray.getString(attr);
                    setFont();
                }else if(attr == R.styleable.JMView_format) {
                    format=typedArray.getString(attr);
                    displayText(null,-1);
                }else if(attr == R.styleable.JMView_dataType) {
                    dataType=typedArray.getInt(attr,0);
                    displayText(null,-1);
                }

            }
        }

        // the recycle() will be executed obligatorily
        finally {
            // for reuse
            typedArray.recycle();
        }
        hideLoading();
    }

    private void setFont(){
        Typeface tf=Typeface.createFromAsset(getContext().getAssets(),"fonts/" + font);
        tv.setTypeface(tf);
    }

    public void hideLoading(){
        this.setVisibility(INVISIBLE);
        this.invalidate();
    }

    public void showLoading(){
        this.setVisibility(VISIBLE);
        this.invalidate();
    }
    public void displayText(Object value, int dataType){
        if(value==null)value=this.value;
        if(dataType<0)dataType=this.dataType;
        TextView tmp=new TextView(getContext());
        new TextViewFiller(value,tmp,this.format,dataType);
        tv.setText(tmp.getText());
    }
}
