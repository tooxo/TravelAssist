package com.tschulte.travelassistant;

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
        UpdateThread context = this;
        int version_no = prefs.getInt("version", 0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                if (prefs.getInt("last_checked", 0) < date.getTime() - 86400000 && (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
                    Request request = new Request.Builder()
                            .url("https://s.chulte.de/travel/update")
                            .addHeader("User-Agent", "TravelAssistant")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String resp_string = response.body().string();
                        if (resp_string.length() > 0) {
                            try {
                                editor.putInt("last_checked", (int) date.getTime());
                                JSONObject json = new JSONObject(resp_string);
                                if (json.getInt("version") > version_no) {
                                    (UpdateThread.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) context.findViewById(R.id.textView2)).setText(context.getResources().getString(R.string.update_found));
                                        }
                                    });
                                    editor.putInt("version", json.getInt("version"));
                                    editor.putInt("last_update", (int) date.getTime());
                                    editor.putString("JSON", json.toString());
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
                if (prefs.getString("JSON", "").equals("")) {
                    String default_String = "{\"version\":1,\"countries\":[{\"name\":\"France\",\"last_updated\":0,\"language\":\"fr\",\"country\":\"FR\",\"traffic\":{\"min_age_supervision\":15,\"min_age\":18,\"road_side\":\"right\",\"speed_in_town\":50,\"speed_single_lane\":80,\"speed_highway\":110,\"speed_tolerance_in_percent\":5,\"highway_free\":false,\"alcohol_tolerance_in_bac_percent\":0.05,\"alcohol_tolerance_in_promille\":0.5,\"calculator\":\"http://www.autoroutes.fr/index.htm?lang=en\"},\"alcohol\":{\"min_possession_age\":0,\"min_buying_age\":18,\"public_drinking_acceptable\":2,\"public_additional\":\"\",\"steps_of_buying\":{\"18\":0},\"steps_of_possession\":{\"0\":3}},\"translation\":{\"basics\":{\"hello\":\"Bonjour\",\"goodbye\":\"Au revoir\",\"please\":\"S'il vous plaît\",\"yes\":\"Oui\",\"no\":\"Non\",\"excuse_me\":\"Excusez-moi\",\"sorry\":\"Pardon\",\"thanks\":\"Merci beaucoup\",\"you_speak_english\":\"parlez-vous anglais?\"},\"restaurant\":{\"menu_please\":\"Le menu s’il vous plaît\",\"bill\":\"L’addition s’il vous plaît.\"}}},{\"name\":\"Germany\",\"last_updated\":0,\"language\":\"de\",\"country\":\"DE\",\"traffic\":{\"min_age_supervision\":17,\"min_age\":18,\"road_side\":\"right\",\"speed_in_town\":50,\"speed_single_lane\":100,\"speed_highway\":130,\"speed_tolerance_in_percent\":5,\"highway_free\":true,\"alcohol_tolerance_in_bac_percent\":0.05,\"alcohol_tolerance_in_promille\":0.5,\"calculator\":\"\"},\"alcohol\":{\"min_possession_age\":14,\"min_buying_age\":16,\"public_drinking_acceptable\":1,\"public_additional\":\"\",\"steps_of_buying\":{\"16\":1,\"18\":0},\"steps_of_possession\":{\"16\":1,\"18\":0}},\"translation\":{\"basics\":{\"hello\":\"Hallo\",\"goodbye\":\"Auf Wiedersehen\",\"please\":\"Bitte\",\"yes\":\"Ja\",\"no\":\"Nein\",\"excuse_me\":\"Entschuldigung\",\"sorry\":\"Entschuldigung\",\"thanks\":\"Danke\",\"you_speak_english\":\"Sprechen sie Englisch?\"},\"restaurant\":{\"menu_please\":\"Die Karte bitte\",\"bill\":\"Ich würde gerne zahlen.\"}}},{\"name\":\"England\",\"last_updated\":0,\"language\":\"en\",\"country\":\"GB\",\"traffic\":{\"min_age_supervision\":17,\"min_age\":17,\"road_side\":\"left\",\"speed_in_town\":48,\"speed_single_lane\":97,\"speed_highway\":113,\"speed_tolerance_in_percent\":12,\"highway_free\":true,\"alcohol_tolerance_in_bac_percent\":0.08,\"alcohol_tolerance_in_promille\":0.8,\"calculator\":\"\"},\"alcohol\":{\"min_possession_age\":0,\"min_buying_age\":16,\"public_drinking_acceptable\":2,\"public_additional\":\"\",\"steps_of_buying\":{\"5\":3,\"16\":1,\"18\":0},\"steps_of_possession\":{\"0\":3}},\"translation\":{\"basics\":{\"hello\":\"Hello\",\"goodbye\":\"Goodbye\",\"please\":\"Please\",\"yes\":\"Yes\",\"no\":\"No\",\"excuse_me\":\"Excuse Me\",\"sorry\":\"Sorry\",\"thanks\":\"Thank you\",\"you_speak_english\":\"\"},\"restaurant\":{\"menu_please\":\"Could I see the menu, please?\",\"bill\":\"Could I have the bill, please?\"}}}]}";
                    editor.putString("JSON", default_String);
                    editor.apply();
                }
                Intent intent = new Intent(UpdateThread.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        thread.start();
    }
}