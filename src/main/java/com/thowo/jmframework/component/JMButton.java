package com.thowo.jmframework.component;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.thowo.jmframework.R;
import com.thowo.jmframework.db.TextViewFiller;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by jimi on 6/29/2017.
 */


public class JMButton extends AppCompatButton {
    private String value;
    private String format;
    private String font;
    private int dataType;

    public JMButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultAttribs();
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
    }

    private void setDefaultAttribs(){
        setBackgroundResource(R.drawable.glossy_button_selector);
        setPadding(20,1,20,1);
    }

    private void setFont(){
        Typeface tf=Typeface.createFromAsset(getContext().getAssets(),"fonts/" + font);
        setTypeface(tf);
    }

    public void displayText(Object value, int dataType){
        if(value==null)value=this.value;
        if(dataType<0)dataType=this.dataType;
        TextView tmp=new TextView(getContext());
        new TextViewFiller(value,tmp,this.format,dataType);
        this.setText(tmp.getText());
    }
}
