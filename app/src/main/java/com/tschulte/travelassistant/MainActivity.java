package com.tschulte.travelassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // GLOBAL SETTING VARIABLES
    public static String OWN_COUNTRY_SETTING = "OWN_COUNTRY_SETTING_PREFERENCES";
    public static String VERSION_NO = "VERSION_NO_UPDATE_SETTING";
    public static String JSON = "JSON_STORE";
    public static String SETTING_DARK_MODE = "DARK_MODE_SETTING";


    OkHttpClient client;

    public static int themeAttributeToColor(int themeAttributeId,
                                            Context context,
                                            int fallbackColorId) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        boolean wasResolved =
                theme.resolveAttribute(
                        themeAttributeId, outValue, true);
        if (wasResolved) {
            return ContextCompat.getColor(
                    context, outValue.resourceId);
        } else {
            // fallback colour handling
            return fallbackColorId;
        }
    }

    void LaunchDetails(JSONObject object) {
        Bundle bundle = new Bundle();
        bundle.putString(JSON, object.toString());

        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);
    }

    void popup(String message, Context ctx) {

        final SpannableString s = new SpannableString(message);
        Linkify.addLinks(s, Linkify.ALL);

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle(getString(R.string.information))
                .setMessage(s)
                .create();
        d.show();
        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        d.findViewById(android.R.id.message).setClickable(true);

    }

    public void findMe() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String country_code = tm.getNetworkCountryIso().toUpperCase();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            JSONObject bigJson = new JSONObject(prefs.getString(MainActivity.JSON, ""));
            JSONArray countries = bigJson.getJSONArray("countries");
            for (int i = 0; i < countries.length(); i++) {
                try {
                    JSONObject c = countries.getJSONObject(i);
                    if (c.getString("country").equals(country_code)) {
                        LaunchDetails(c);
                        return;
                    }
                } catch (JSONException ignored) {
                }
            }

            popup("Sorry, " + country_code + "' is not implemented yet.", MainActivity.this);
            return;
        } catch (NullPointerException | JSONException ignored) {
        }


        if ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Request request = new Request.Builder()
                            .url("http://ip-api.com/json")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String response_string = response.body().string();
                        if (response_string.length() > 0) {
                            try {
                                JSONObject json = new JSONObject(response_string);
                                String country = json.getString("country");
                                String country_code = json.getString("countryCode");
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                JSONObject bigJson = new JSONObject(prefs.getString(MainActivity.JSON, ""));
                                JSONArray countries = bigJson.getJSONArray("countries");
                                for (int i = 0; i < countries.length(); i++) {
                                    try {
                                        JSONObject c = countries.getJSONObject(i);
                                        if (c.getString("name").equals(country)) {
                                            LaunchDetails(c);
                                            return;
                                        }
                                    } catch (JSONException ignored) {
                                    }
                                }
                                runOnUiThread(() -> popup("Sorry, " + country + "' is not implemented yet.", MainActivity.this));

                            } catch (JSONException ignored) {
                            }
                        }
                    } catch (IOException ignored) {
                    }
                }
            }).start();
        }
    }

    void layout(List<JSONObject> lsob, Context con) {
        try {
            Resources r = con.getResources();
            LinearLayout layout = findViewById(R.id.select_lin);
            layout.removeAllViews();
            client = new OkHttpClient();
            int id = 12000;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
            boolean showOwnCounty = prefs.getBoolean(OWN_COUNTRY_SETTING, true);

            for (JSONObject o : lsob) {
                if (!showOwnCounty) {
                    String language = o.getString("language");
                    String own_language = Locale.getDefault().getLanguage();
                    if (language.equals(own_language)) {
                        continue;
                    }
                }
                LinearLayout l = new LinearLayout(this, null, R.attr.borderlessButtonStyle);
                l.setOrientation(LinearLayout.HORIZONTAL);
                l.setId(id);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics())));
                lp.setMargins(0, (int) getResources().getDimension(R.dimen.between_margin_main), 0, 0);

                l.setLayoutParams(lp);
                id = id + 1;

                SquareImageView imageView = new SquareImageView(this);
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(lp);
                imageView.setImageResource(getResources().getIdentifier(o.getString("name").toLowerCase(), "drawable", this.getApplicationInfo().packageName));
                imageView.setAdjustViewBounds(true);
                l.addView(imageView);

                FontFitTextView fontFitTextView = new FontFitTextView(this);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                fontFitTextView.setLayoutParams(lp);
                fontFitTextView.setGravity(Gravity.CENTER);
                fontFitTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                fontFitTextView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 55, r.getDisplayMetrics()));

                Locale lo = new Locale.Builder()
                        .setLanguageTag(o.getString("language"))
                        .setRegion(o.getString("country"))
                        .build();
                String displayCounty = lo.getDisplayCountry();

                // fontFitTextView.setText(o.getString("name"));
                fontFitTextView.setText(displayCounty);
                fontFitTextView.setTypeface(null, Typeface.BOLD);
                fontFitTextView.setTextColor(themeAttributeToColor(R.attr.primaryTextColor, MainActivity.this, ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                l.addView(fontFitTextView);
                l.setOnClickListener(v -> LaunchDetails(o));
                layout.addView(l);
            }
            SquareImageView pin = findViewById(R.id.pin);
            pin.setOnClickListener(v -> findMe());
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        }else {
            setTheme(R.style.Theme_Hell);
        }
        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));
        setContentView(R.layout.country_select);

        try {
            String jsonString = prefs.getString(MainActivity.JSON, "");
            JSONObject json = new JSONObject(jsonString);
            JSONArray array = json.getJSONArray("countries");
            List<JSONObject> obList = new ArrayList<>();
            for (int x = 0; x < array.length(); x++) {
                obList.add((JSONObject) array.get(x));
            }
            layout(obList, this);

            EditText search = findViewById(R.id.search);
            Context con = this;
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<JSONObject> ls = new ArrayList<>();
                    if (count > 0) {
                        for (JSONObject a : obList) {
                            try {
                                Locale lo = new Locale.Builder()
                                        .setLanguageTag(a.getString("language"))
                                        .setRegion(a.getString("country"))
                                        .build();
                                String displayCounty = lo.getDisplayCountry();
                                if (a.get("name").toString().toLowerCase().contains(s.toString().toLowerCase()) || displayCounty.toLowerCase().contains(s.toString().toLowerCase())) {
                                    ls.add(a);
                                }
                            } catch (JSONException ignored) {
                            }
                        }
                        layout(ls, con);
                    } else {
                        layout(obList, con);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        } catch (Exception js) {
            // Forcing an update by setting the version num to 0
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(VERSION_NO, 0).apply();
            js.printStackTrace();
        }

        SquareImageView settings = findViewById(R.id.settingsButton);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            finish();
        });

    }
}
