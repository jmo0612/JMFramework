package com.thowo.jmframework;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.thowo.jmframework.component.JMActivity;

public class CrashActivity extends JMActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        //final GlobalClass global = (GlobalClass) getApplicationContext();
        //global.setLoading(this);

        TextView tv = (TextView)findViewById(R.id.text_view);
        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("error")!= null)
        {
            //TODO here get the string stored in the string variable and do
            // setText() on userName
            tv.setText(bundle.getString("error"));
        }
    }
}
