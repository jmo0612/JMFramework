package com.thowo.jmframework.component;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.R;
import com.thowo.jmframework.db.jmoRowObject;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

/**
 * Created by jimi on 7/7/2017.
 */

public class JMListAdapter extends ArrayAdapter<jmoRowObject> {
    private int resource;
    private LayoutInflater inflater;
    private Context context;
    private String imagePath;

    public JMListAdapter ( Context ctx, int resourceId, List objects, String imgPath ) {

        super( ctx, resourceId, objects );
        resource = resourceId;
        inflater = LayoutInflater.from( ctx );
        context=ctx;
        imagePath=imgPath;
    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent ) {

        /* create a new view of my layout and inflate it in the row */

        JmoFunctions.trace(position);

        JMListViewCache viewCache;

        jmoRowObject cur= getItem(position);

        JMRowObjectListView rObj=new JMRowObjectListView(cur);


        if ( convertView == null ) {



            convertView = (RelativeLayout) inflater.inflate(resource, parent,false);

            if(convertView.getBackground()==null){
                convertView.setBackgroundResource(R.drawable.glossy_button_selector);
            }

            viewCache = new JMListViewCache(convertView);

            convertView.setTag( viewCache );
        }
        else {
            viewCache = ( JMListViewCache ) convertView.getTag();
            convertView=(RelativeLayout)viewCache.getViewBase();
        }

        rObj.displayRowObjectData(viewCache,imagePath);



        return convertView;
    }
}
