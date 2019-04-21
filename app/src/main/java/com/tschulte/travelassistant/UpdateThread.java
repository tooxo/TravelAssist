package com.tschulte.travelassistant;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Date;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateThread extends AppCompatActivity {
    @Override
    public void onCreate(Bundle bundle) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_main);
        OkHttpClient client = new OkHttpClient();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Context context = this;
        int version_no = prefs.getInt("version", 0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                if (prefs.getInt("last_checked", 0) < date.getTime() - 86400000 && (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
                    Request request = new Request.Builder()
                            .url("https://s.chulte.de/travel/update")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String resp_string = response.body().string();
                        if (resp_string.length() > 0) {
                            try {
                                editor.putInt("last_checked", (int) date.getTime());
                                JSONObject json = new JSONObject(resp_string);
                                if (json.getInt("version") > version_no) {

                                    ((UpdateThread) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView)((UpdateThread) context).findViewById(R.id.textView2)).setText(context.getResources().getString(R.string.update_found));
                                        }
                                    });
                                    editor.putInt("version", json.getInt("version"));
                                    editor.putInt("last_update", (int) date.getTime());
                                    editor.putString("JSON", json.toString());
                                    editor.apply();
                                    Log.v("UPDATE", "Updated to " + json.getString("version"));
                                } else {}
                            } catch (JSONException js) {
                                js.printStackTrace();
                            }
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
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