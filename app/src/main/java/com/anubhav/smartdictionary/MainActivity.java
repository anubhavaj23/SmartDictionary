package com.anubhav.smartdictionary;

import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ContextWrapper cnt;
    SharedPreferences preferences;
    static boolean running = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cnt = new ContextWrapper(getBaseContext());
        final Button button = (Button) findViewById(R.id.button);
        //final Intent intent = new Intent(cnt,Myservice.class);

        final Intent intent = new Intent(createExplicitFromImplicitIntent(cnt,new Intent(Myservice.MY_SERVICE)));

        if(isMyServiceRunning(Myservice.class)){
            button.setBackgroundColor(Color.rgb(229,41,41));
            button.setText("Disable");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button.getText().toString().trim().equalsIgnoreCase("enable")){
                    //cnt.startService(intent);
                    running = true;
                    startService(intent);

                    button.setBackgroundColor(Color.rgb(229,41,41));
                    button.setText("Disable");
                }
                else{
                    Toast.makeText(cnt, "disable button clicked", Toast.LENGTH_SHORT).show();
                    Myservice.stopthisservice();

                    //cnt.stopService(intent);
                    running = false;
                    //if(!isMyServiceRunning((new Intent(Myservice.MY_SERVICE)).getClass())) {
                        button.setBackgroundColor(Color.rgb(143, 208, 29));
                        button.setText("Enable");
                    //}
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        DictionaryActivity.finishactivity = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.super.onBackPressed();
            }
        },100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DictionaryActivity.activity.onPause();
        finish();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

// Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

// Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

// Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

// Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
