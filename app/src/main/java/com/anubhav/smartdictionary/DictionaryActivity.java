package com.anubhav.smartdictionary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.util.StringTokenizer;

public class DictionaryActivity extends AppCompatActivity {
    static DictionaryActivity activity;
    TextView tv_meaningtext;
    EditText et_searchtext;
    ProgressDialog progressDialog;
    boolean loaded = false;
    static boolean finishactivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        tv_meaningtext = (TextView) findViewById(R.id.tv_meaningtext);
        et_searchtext = (EditText) findViewById(R.id.et_searchtext);
        tv_meaningtext.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(this,R.style.MyTheme);

        activity = this;

        et_searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    progressDialog.setCancelable(true);
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.show();
                    performSearch();
                    InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_searchtext.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(finishactivity)
            finish();
    }

    @Override
    protected void onResume() {
        finishactivity = true;
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.smenu_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.settings:
                finishactivity = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishactivity = true;
                    }
                },50);
                startActivity(new Intent(this,MainActivity.class));
                break;
            default:
                break;
        }

        return true;
    }

    String meaningtext;
    RequestQueue requestQueue;

    void performSearch() {
        requestQueue = Volley.newRequestQueue(this);
        final String text_copied = et_searchtext.getText().toString().trim();
        meaningtext = "";
        StringTokenizer tokenizer = new StringTokenizer(text_copied);
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            i++;
            tokenizer.nextToken();
        }
        if (i == 1) {
            if (new Stringcheck().isoneword(text_copied.trim())) {
                if (new wordbank(DictionaryActivity.this).getWord(text_copied) == null) {
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
                                                progressDialog.hide();
                                                if (meaningtext.trim().equals(""))
                                                    return;
                                                loaded = true;
                                                tv_meaningtext.setText(meaningtext);
                                                tv_meaningtext.setBackgroundColor(Color.WHITE);
                                                tv_meaningtext.setVisibility(View.VISIBLE);
                                                new wordbank(DictionaryActivity.this).addword(new Word(text_copied.trim().toLowerCase(), meaningtext.trim()));
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
                /*if (!loaded) {
                    performSearch();
                }
                else {
                    progressDialog.dismiss();
                    loaded = false;
                }*/


                } else {
                    wordbank wordb = new wordbank(this);

                    progressDialog.hide();
                    Word word = wordb.getWord(text_copied);
                    meaningtext = word.getMeaning();
                    if (meaningtext.trim().equals(""))
                        return;
                    loaded = true;
                    tv_meaningtext.setText(meaningtext);
                    tv_meaningtext.setBackgroundColor(Color.WHITE);
                    tv_meaningtext.setVisibility(View.VISIBLE);
                }
            }
        }
        else {
            Toast.makeText(this, "Error: Bad search word", Toast.LENGTH_SHORT).show();
        }
    }
}
