package com.thowo.jmframework.component;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thowo.jmframework.R;
import com.thowo.jmframework.db.TextViewFiller;
import com.thowo.jmframework.db.jmoRowObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by jimi on 9/23/2017.
 */

public class JMRowObjectListView {
    private jmoRowObject rObj;

    public JMRowObjectListView(jmoRowObject rowObject){
        rObj=rowObject;
    }

    public void displayRowObjectData(JMListViewCache vCache,String imgPath){
        if(rObj!=null){
            for(int i=0;i<rObj.getCount();i++){
                assignRowObject(vCache,i,imgPath);
            }
        }else{
            Log.d(TAG, "displayRowObjectData: NULL");
        }
    }

    private void assignRowObject(JMListViewCache vCache, int columnIndex,String imgPath){
        View vTmp=vCache.getView(columnIndex,rObj.getColumnName(columnIndex));

        if(vTmp!=null){
            if(vTmp.getClass()==TextView.class){
                TextView tv=(TextView) vTmp;
                tv.setText(String.valueOf(rObj.getDBString(columnIndex)));
            }else if(vTmp.getClass()==EditText.class){
                EditText edt=(EditText) vTmp;
                edt.setText(String.valueOf(rObj.getDBString(columnIndex)));
            }else if(vTmp.getClass()==Button.class){
                Button btn=(Button) vTmp;
                btn.setText(String.valueOf(rObj.getDBString(columnIndex)));
            }else if(vTmp.getClass()==JMImageFrame.class){
                JMImageFrame imageFrame=(JMImageFrame) vTmp;
                imageFrame.loadImage(rObj.getDBString(columnIndex));
            }else if(vTmp.getClass()==JMButton.class){
                JMButton jmView=(JMButton) vTmp;
                jmView.displayText(rObj.getValue(columnIndex),rObj.getDataType(columnIndex));
            }else if(vTmp.getClass()==JMEditText.class){
                JMEditText jmView=(JMEditText) vTmp;
                jmView.displayText(rObj.getValue(columnIndex),rObj.getDataType(columnIndex));
            }else if(vTmp.getClass()==JMHorizontalButton.class){
                JMHorizontalButton jmView=(JMHorizontalButton) vTmp;
                jmView.displayText(rObj.getValue(columnIndex),rObj.getDataType(columnIndex));
            }else if(vTmp.getClass()==JMLoadingSprite.class){
                JMLoadingSprite jmView=(JMLoadingSprite) vTmp;
                jmView.displayText(rObj.getValue(columnIndex),rObj.getDataType(columnIndex));
            }else if(vTmp.getClass()==JMTextView.class){
                JMTextView jmView=(JMTextView) vTmp;
                jmView.displayText(rObj.getValue(columnIndex),rObj.getDataType(columnIndex));
            }else if(vTmp.getClass()==JMVerticalButton.class){
                JMVerticalButton jmView=(JMVerticalButton) vTmp;
                jmView.displayText(rObj.getValue(columnIndex),rObj.getDataType(columnIndex));
            }
        }
    }



    public jmoRowObject getRowObject(){
        return rObj;
    }


}
