package com.tschulte.travelassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;
import static com.tschulte.travelassistant.MainActivity.themeAttributeToColor;

public class UpdateThread extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle bundle) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        } else {
            setTheme(R.style.Theme_Hell);
        }
        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_main);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        UpdateThread context = this;
        int version_no = prefs.getInt(MainActivity.VERSION_NO, 0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                if (prefs.getInt("last_checked", 0) < date.getTime() - 86400000 && (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
                    Request request = new Request.Builder()
                            .url("https://s.chulte.de/travel/update")
                            .addHeader("User-Agent", "TravelAssistant")
                            .addHeader("Version-Code", String.valueOf(version_no))
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String resp_string = response.body().string();
                        if (resp_string.length() > 0) {
                            try {
                                editor.putInt("last_checked", (int) date.getTime());
                                if (resp_string.equals("NO UPDATE AVAILABLE.")) {
                                    Intent intent = new Intent(UpdateThread.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    JSONObject json = new JSONObject(resp_string);
                                    (UpdateThread.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) context.findViewById(R.id.textView2)).setText(context.getResources().getString(R.string.update_found));
                                        }
                                    });
                                    editor.putInt(MainActivity.VERSION_NO, json.getInt("version"));
                                    editor.putInt("last_update", (int) date.getTime());
                                    editor.putString(MainActivity.JSON, json.toString());
                                    editor.apply();
                                    Log.v("UPDATE", "Updated to " + json.getString("version"));
                                }
                            } catch (JSONException | NullPointerException ignored) {
                            }
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
                if (prefs.getString(MainActivity.JSON, null) == null) {
                    InputStream is = UpdateThread.this.getResources().openRawResource(R.raw.defaults);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    try {
                        String contents = br.readLine();
                        editor.putString(MainActivity.JSON, contents);
                        editor.apply();

                    }catch (IOException io){
                        String empty = "{\"version\":0,\"countries\":[]}";
                        editor.putString(MainActivity.JSON, empty);
                        editor.apply();
                    }
                }
                Intent intent = new Intent(UpdateThread.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        thread.start();
    }
}