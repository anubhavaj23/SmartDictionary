package com.anubhav.smartdictionary;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
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

import java.util.StringTokenizer;

/*
 * Created by anubh on 21-Mar-17.
*/

public class Myservice extends Service {
    ClipboardManager clipBoard;
    Context context;
    String meaningtext;
    public static final String MY_SERVICE="dic.Myservice1";
    RequestQueue requestQueue;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener( new ClipboardListener() );
        return START_STICKY;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = base;
    }

    public static void stopthisservice(){
        Thread.dumpStack();
        Thread.currentThread().interrupt();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /*public void stopThread(){
        if(Myservice.getThread()!=null){
            Myservice.getThread().interrupt();
            Myservice.setThread(null);
        }
    }*/

    class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener
    {

        public void onPrimaryClipChanged()
        {
            if(MainActivity.running)
                ;
            else
                return;
            //final ContextWrapper context = new ContextWrapper(getBaseContext());
            final String text_copied = clipBoard.getPrimaryClip().getItemAt(0).getText().toString();
            meaningtext = "";
            StringTokenizer tokenizer = new StringTokenizer(text_copied);
            int i=0;
            while(tokenizer.hasMoreTokens()){
                i++;
                tokenizer.nextToken();
            }
            if(i==1) {
                    if (new Stringcheck().isoneword(text_copied.trim())) {
                        if(new wordbank(context).getWord(text_copied) == null) {
                            JSONObject jsonObject = null;
                        final JsonObjectRequest meaningreq = new JsonObjectRequest(Request.Method.GET, "http://api.pearson.com/v2/dictionaries/wordwise/entries?search=" + text_copied, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String responsecode;
                                        try {
                                            responsecode = response.get("status").toString();
                                            if (responsecode.equals("200")) {
                                                int count = Integer.parseInt(response.get("count").toString());
                                                if (count > 0) {
                                                    int wordindex = 0;
                                                    JSONArray results = response.getJSONArray("results");
                                                    for (int i = 0; i < results.length(); i++) {
                                                        JSONObject tempobj = results.getJSONObject(i);

                                                        if (tempobj.getString("headword").equalsIgnoreCase(text_copied)) {
                                                            try {
                                                                if (wordindex > 0)
                                                                    meaningtext += ("\n\n");
                                                                meaningtext += ((wordindex + 1) + ".(" + tempobj.getString("part_of_speech") + ")");
                                                            } catch (Exception e) {
                                                                meaningtext += (wordindex + 1) + ".";
                                                            }
                                                            try {
                                                                JSONArray temparray = tempobj.getJSONArray("senses");
                                                                tempobj = temparray.getJSONObject(0);
                                                                meaningtext += (" " + tempobj.getString("definition"));
                                                                wordindex++;
                                                            } catch (Exception e) {
                                                                meaningtext = meaningtext.substring(0, meaningtext.length() - 2);
                                                            }
                                                        }
                                                    }

                                                    if (meaningtext.trim().equals(""))
                                                        return;

                                                    final Intent intent = new Intent(context, MyCustomDialog.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("text_copied", text_copied);
                                                    intent.putExtra("meaningtext", meaningtext);
                                                    new wordbank(context).addword(new Word(text_copied.trim().toLowerCase(),meaningtext.trim()));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            context.startActivity(intent);
                                                        }
                                                    }, 1000);
                                                }
                                            } else {
                                                Toast.makeText(getBaseContext(), "Live Dictionary : bad internet connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getBaseContext(), "Live Dictionary : bad internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        requestQueue = Volley.newRequestQueue(getBaseContext());
                        requestQueue.add(meaningreq);

                    }
                        else {
                            wordbank wordb = new wordbank(context);
                            Word word = wordb.getWord(text_copied);
                            meaningtext = word.getMeaning();
                            final Intent intent = new Intent(context, MyCustomDialog.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("text_copied", text_copied);
                            intent.putExtra("meaningtext", meaningtext);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(intent);
                                }
                            }, 1000);
                        }
                }
            }
        }
    }
}
