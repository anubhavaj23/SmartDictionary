package com.anubhav.smartdictionary;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by anubh on 21-Mar-17.
 */

public class MyCustomDialog extends Activity {
    TextView tv_meaning;
    TextView tv_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(true);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog);
            initializeContent();
            tv_search.setText(getIntent().getStringExtra("text_copied"));
            tv_meaning.setText(getIntent().getExtras().getString("meaningtext"));


        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }

    }
    private void initializeContent(){
        tv_meaning = (TextView) findViewById(R.id.tv_meaning);
        tv_search   = (TextView) findViewById(R.id.tv_search);
        tv_meaning.setWidth(1000);
    }
}
