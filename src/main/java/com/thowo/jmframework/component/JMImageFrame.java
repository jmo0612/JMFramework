package com.thowo.jmframework.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;

import java.io.File;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.MaskTransformation;

/**
 * Created by jimi on 10/30/2017.
 */

public class JMImageFrame extends RelativeLayout {
    private int drawDef;
    private int drawSrc;
    private int drawFront;
    private int drawMask;
    private ImageView ivMain;
    private ImageView ivFront;


    public JMImageFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context,R.layout.image_container_jm,this);

        if(!setDefaultAttribs())return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JMImageFrame);
        int count = typedArray.getIndexCount();
        try{

            for (int i = 0; i < count; ++i) {

                int attr = typedArray.getIndex(i);
                // the attr corresponds to the title attribute
                if(attr == R.styleable.JMImageFrame_defImg) {
                    drawDef=typedArray.getResourceId(attr,0);
                }else if(attr == R.styleable.JMImageFrame_srcImg) {
                    drawSrc=typedArray.getResourceId(attr,0);
                }else if(attr == R.styleable.JMImageFrame_frontImg) {
                    drawFront=typedArray.getResourceId(attr,0);
                    ivFront.setImageDrawable(typedArray.getDrawable(attr));
                }else if(attr == R.styleable.JMImageFrame_maskImg) {
                    drawMask=typedArray.getResourceId(attr,0);
                }
            }
        }

        // the recycle() will be executed obligatorily
        finally {
            // for reuse
            typedArray.recycle();
        }
    }

    private boolean setDefaultAttribs(){
        ivMain=(ImageView) findViewById(R.id.ivImg);
        if(ivMain==null) return false;

        ivFront=(ImageView) findViewById(R.id.ivBorder);
        if(ivFront==null) return false;

        return  true;
    }

    public void loadImage(File file){
        loadImage(file,-666,"",null);
    }
    public void loadImage(int resId){
        loadImage(null,resId,"",null);
    }
    public void loadImage(String path){
        loadImage(null,-666,path,null);
    }
    public void loadImage(Uri uri){
        loadImage(null,-666,"",uri);
    }
    public void loadImage(){
        loadImage(null,-666,"",null);
    }

    private void loadImage(File file, int resId, String path, Uri uri){
        try {
            ivMain.setImageBitmap(null);
            if(file!=null){
                Picasso.with(ivMain.getContext()).load(file).transform(new CropSquareTransformation()).transform(new MaskTransformation(ivMain.getContext(),drawMask)).placeholder(drawDef).into(ivMain);
            }else if(resId!=-666){
                Picasso.with(ivMain.getContext()).load(resId).transform(new CropSquareTransformation()).transform(new MaskTransformation(ivMain.getContext(),drawMask)).placeholder(drawDef).into(ivMain);
            }else if(!path.equals("")){
                Picasso.with(ivMain.getContext()).load(path).transform(new CropSquareTransformation()).transform(new MaskTransformation(ivMain.getContext(),drawMask)).placeholder(drawDef).into(ivMain);
            }else if(uri!=null){
                Picasso.with(ivMain.getContext()).load(uri).transform(new CropSquareTransformation()).transform(new MaskTransformation(ivMain.getContext(),drawMask)).placeholder(drawDef).into(ivMain);
            }else{
                Picasso.with(ivMain.getContext()).load(drawSrc).transform(new CropSquareTransformation()).transform(new MaskTransformation(ivMain.getContext(),drawMask)).placeholder(drawDef).into(ivMain);
            }
        }catch (Exception e){
            JmoFunctions.trace(e.getMessage());
        }
    }

}
