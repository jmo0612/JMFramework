package com.thowo.jmframework.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thowo.jmframework.R;
import com.thowo.jmframework.db.TextViewFiller;

/**
 * Created by jimi on 6/29/2017.
 */

public class JMHorizontalButton extends LinearLayout {
    private String value;
    private String format;
    private String font;
    private int dataType;

    private TextView tv;
    private ImageView iv;

    public JMHorizontalButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context,R.layout.horizontal_button,this);
        tv=(TextView) findViewById(R.id.captionHB);
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
                }else if(attr == R.styleable.JMView_icon) {
                    ImageView iv=(ImageView)findViewById(R.id.leftIconHB);
                    Drawable dr=typedArray.getDrawable(attr);
                    iv.setImageDrawable(dr);
                }else if(attr == R.styleable.JMView_icon) {
                    iv=(ImageView)findViewById(R.id.leftIconHB);
                    Drawable dr=typedArray.getDrawable(attr);
                    iv.setImageDrawable(dr);
                }

            }
        }

        // the recycle() will be executed obligatorily
        finally {
            // for reuse
            typedArray.recycle();
        }



    }

    private void setFont(){
        Typeface tf=Typeface.createFromAsset(getContext().getAssets(),"fonts/" + font);
        tv.setTypeface(tf);
    }

    public void setMyOnClickedListener(View.OnClickListener listener){
        ImageView clickArea=(ImageView)findViewById(R.id.clickAreaHB);
        clickArea.setOnClickListener(listener);
    }

    public void displayText(Object value, int dataType){
        if(value==null)value=this.value;
        if(dataType<0)dataType=this.dataType;
        TextView tmp=new TextView(getContext());
        new TextViewFiller(value,tmp,this.format,dataType);
        tv.setText(tmp.getText());
    }
}
