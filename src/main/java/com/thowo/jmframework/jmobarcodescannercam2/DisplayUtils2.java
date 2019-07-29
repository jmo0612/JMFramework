package com.thowo.jmframework.jmobarcodescannercam2;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.WindowManager;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DisplayUtils2 {
    public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenResolution = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution);
        } else {
            screenResolution.set(getDisplayWidth(display), getDisplayHeight(display));
        }

        return screenResolution;
    }

    public static int getScreenOrientation(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getDisplayWidth(display)==getDisplayHeight(display)){
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else{
            if(getDisplayWidth(display) < getDisplayHeight(display)){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }


    private static int getDisplayWidth(Display display){
        int ret=0;
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        ret = size.x;
        return ret;
    }

    private static int getDisplayHeight(Display display){
        int ret=0;
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        ret = size.y;
        return ret;
    }

}
