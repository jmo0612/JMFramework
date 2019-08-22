package com.thowo.jmframework.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;
import com.thowo.jmframework.db.TextViewFiller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jimi on 6/29/2017.
 */

public class JMEditText extends AppCompatEditText {
    private boolean isDate;
    private Context ctx;
    private String value;
    private String format;
    private int dataType;
    private String font;
    private String dateErrMsg;

    public JMEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx=context;
        setDefaultAttribs();
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.JMEditText);
        int count = typedArray.getIndexCount();
        try{

            for (int i = 0; i < count; ++i) {

                int attr = typedArray.getIndex(i);
                // the attr corresponds to the title attribute
                if(attr == R.styleable.JMEditText_isDate) {
                    isDate=typedArray.getBoolean(attr, false);
                    setAsDate();
                }else if(attr == R.styleable.JMEditText_dateErrorMsg) {
                    dateErrMsg=typedArray.getString(attr);
                }else if(attr == R.styleable.JMView_text) {
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

    private void setAsDate(){
        validateEdtDate();
    }

    private void setDefaultAttribs(){
        setBackgroundResource(R.drawable.text_box);
        setPadding(20,1,20,1);
    }
    private void setFont(){
        Typeface tf=Typeface.createFromAsset(getContext().getAssets(),"fonts/" + font);
        setTypeface(tf);
    }

    public void validateEdtDate(){
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String validDate(String dt){
                return this.workingDate(this.getTgl(dt),this.getBln(dt),this.getThn(dt));
            }



            private String getTgl(String dt){
                String ret="";
                for(int i=0; i<dt.length();i++){
                    String tmp=dt.substring(i,i+1);
                    if(tmp=="/" || tmp=="-"){
                        break;
                    }else{
                        ret+=tmp;
                    }
                }
                return ret;
            }

            private String getBln(String dt){
                String ret="";
                boolean start=false;
                for(int i=0;i<dt.length();i++){
                    String tmp=dt.substring(i,i+1);
                    if(start){
                        if(tmp=="/" || tmp=="-"){
                            break;
                        }else{
                            ret+=tmp;
                        }
                    }else{
                        if(tmp=="/" || tmp=="-"){
                            start=true;
                        }
                    }
                }
                return ret;
            }

            private String getThn(String dt){
                String ret="";
                int start=0;
                for(int i=0;i<dt.length();i++){
                    String tmp=dt.substring(i,i+1);
                    if(start==1){
                        if(tmp=="/" || tmp=="-"){
                            break;
                        }else{
                            ret+=tmp;
                        }
                    }else{
                        if(tmp=="/" || tmp=="-"){
                            start++;
                        }
                    }
                }
                return ret;
            }

            private String workingDate(String tgl, String bln, String thn){
                String ret="";
                if(tgl!="" && bln!="" && thn!=""){
                    ret=tgl+"/"+bln+"/"+thn;
                }else{
                    if(tgl!="" && bln!=""){
                        ret=tgl+"/"+bln;
                    }else{
                        if(tgl!=""){
                            ret=tgl;
                        }
                    }
                }
                return ret;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //edt.setText(working);
                //edt.setSelection(working.length());
                String workingStr=this.validDate(getText().toString());
                //String workingStr=edt.getText().toString();

                boolean isValid=isThisDateValid(workingStr,"dd/MM/yyyy");

                if (!isValid) {
                    setError(dateErrMsg + android.text.format.DateFormat.format("dd/MM/yyyy", Calendar.getInstance().getTime()));
                } else {
                    setError(null);
                }


                if(!getText().toString().equals(workingStr)){
                    setText(workingStr);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean isThisDateValid(String dateToValidate, String dateFromat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void displayText(Object value, int dataType){
        if(value==null)value=this.value;
        if(dataType<0)dataType=this.dataType;
        TextView tmp=new TextView(getContext());
        new TextViewFiller(value,tmp,this.format,dataType);
        this.setText(tmp.getText());
    }

}
