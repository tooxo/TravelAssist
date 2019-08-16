package com.tschulte.travelassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;
import static com.tschulte.travelassistant.MainActivity.themeAttributeToColor;

public class TranslationDetails extends AppCompatActivity {

    TextToSpeechController controller;

    private String determineText(Context context, String textIdentifier, int category) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json_string = prefs.getString(MainActivity.JSON, "");
        if (json_string.equals("")) {
            return textIdentifier;
        }
        JSONObject json;
        try {
            json = new JSONObject(json_string);
        } catch (JSONException je) {
            je.printStackTrace();
            return textIdentifier;
        }
        JSONArray countries;
        try {
            countries = json.getJSONArray("countries");
        } catch (JSONException je) {
            je.printStackTrace();
            return textIdentifier;
        }

        String ownLang = Locale.getDefault().getLanguage();

        try {
            if (countries != null) {
                for (int i = 0; i < countries.length(); i++) {
                    JSONObject country = countries.getJSONObject(i);
                    if (country.getString("language").equals(ownLang)) {
                        JSONObject o = country.getJSONObject("translation");
                        Iterator<String> iterator = o.keys();

                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            Iterator<String> iterator1 = o.getJSONObject(key).keys();

                            while (iterator1.hasNext()) {
                                String key1 = iterator1.next();
                                if (key1.equals(textIdentifier)) {
                                    return o.getJSONObject(key).getString(key1);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return textIdentifier;
        }
        try {
            if (countries != null) {
                for (int i = 0; i < countries.length(); i++) {
                    JSONObject country = countries.getJSONObject(i);
                    if (country.getString("language").equals("en")) {

                        JSONObject o = country.getJSONObject("translation");
                        Iterator<String> iterator = o.keys();

                        while (iterator.hasNext()) {
                            String key = iterator.next();

                            Iterator<String> iterator1 = o.getJSONObject(key).keys();

                            while (iterator1.hasNext()) {
                                String key1 = iterator1.next();
                                if (key1.equals(textIdentifier)) {
                                    return o.getJSONObject(key).getString(key1);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return textIdentifier;
        }
        return textIdentifier;
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.stopTTS();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Bundle extras = getIntent().getExtras();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        } else {
            setTheme(R.style.Theme_Hell);
        }
        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));


        setContentView(R.layout.translation);
        controller = new TextToSpeechController(this);
        try {
            String own = Locale.getDefault().getDisplayCountry(Locale.ENGLISH).toLowerCase();
            JSONObject json = new JSONObject(extras.getString(MainActivity.JSON));
            ImageView bannerAlcohol = findViewById(R.id.banner_alcohol);
            String c;
            try {
                c = json.getString("name");
            } catch (JSONException je) {
                c = own;
            }
            bannerAlcohol.setImageResource(getResources().getIdentifier(c.toLowerCase() + "_square", "drawable", getPackageName()));
            ImageView fromLanguage = findViewById(R.id.fromLanguage);
            ImageView toLanguage = findViewById(R.id.toLanguage);
            fromLanguage.setImageResource(getResources().getIdentifier(own + "_square", "drawable", getPackageName()));
            toLanguage.setImageResource(getResources().getIdentifier(json.getString("name").toLowerCase() + "_square", "drawable", getPackageName()));
            Locale toLocale = new Locale.Builder().setLanguage(json.getString("language")).setRegion(json.getString("country")).build();
            fromLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.speakTheText(Locale.getDefault().getDisplayLanguage(Locale.getDefault()), Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
                }
            });
            toLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.speakTheText(toLocale.getDisplayLanguage(Locale.getDefault()), Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
                }
            });

            List<JSONObject> ls = new ArrayList<>();
            JSONObject ob = (JSONObject) json.get("translation");
            Iterator<String> iterator = ob.keys();

            while (iterator.hasNext()) {
                String re = iterator.next();
                ls.add((JSONObject) ob.get(re));
            }

            LinearLayout trans_holder = findViewById(R.id.language_holder);

            int textColor = MainActivity.themeAttributeToColor(R.attr.primaryTextColor, this, getResources().getColor(R.color.colorPrimary));

            for (int a = 0; a < ls.size(); a++) {

                iterator = ls.get(a).keys();

                while (iterator.hasNext()) {
                    String item = iterator.next();

                /*
                    Remove the translation of "Do you speak english", because it's not needed in english.
                 */
                    if (item.equals("you_speak_english")) {
                        try {
                            if (json.getString("language").equals("en")) {
                                continue;
                            }
                        } catch (JSONException ignored) {
                        }
                    }

                    LinearLayout general_holder = new LinearLayout(this);
                    LinearLayout.LayoutParams general_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    general_holder.setOrientation(LinearLayout.VERTICAL);
                    general_holder.setLayoutParams(general_params);


                    LinearLayout first_lang_holder = new LinearLayout(this);
                    LinearLayout.LayoutParams first_lang_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    first_lang_params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()), 0);
                    first_lang_holder.setLayoutParams(first_lang_params);

                    LinearLayout second_lang_holder = new LinearLayout(this);
                    LinearLayout.LayoutParams second_lang_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    second_lang_params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0);
                    second_lang_holder.setLayoutParams(second_lang_params);

                    TextView first_lang_text = new TextView(this);
                    LinearLayout.LayoutParams first_lang_text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    first_lang_text.setGravity(Gravity.BOTTOM | Gravity.START);
                    first_lang_text_params.gravity = Gravity.BOTTOM | Gravity.START;
                    first_lang_text.setTextColor(textColor);
                    first_lang_text.setLayoutParams(first_lang_text_params);
                    String text = determineText(this, item, a);
                    first_lang_text.setText(text);
                    first_lang_text.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

                    first_lang_text.setOnClickListener(v ->
                            controller.speakTheText(first_lang_text.getText().toString(), Locale.getDefault().getLanguage(), Locale.getDefault().getCountry()));

                    TextView second_lang_text = new TextView(this);
                    LinearLayout.LayoutParams second_lang_text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    second_lang_text_params.gravity = Gravity.TOP | Gravity.END;
                    second_lang_text.setTextColor(textColor);
                    second_lang_text.setLayoutParams(second_lang_text_params);
                    second_lang_text.setGravity(Gravity.TOP | Gravity.END);
                    second_lang_text.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    second_lang_text.setText(ls.get(a).getString(item));

                    second_lang_text.setOnClickListener(v -> {
                        try {
                            String lang = json.getString("language");
                            String country = json.getString("country");
                            controller.speakTheText(second_lang_text.getText().toString(), lang, country);
                        } catch (JSONException ignored) {
                        }
                    });

                    TextView footer = new TextView(this);
                    LinearLayout.LayoutParams footer_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
                    footer_params.setMargins(
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
                    );
                    footer.setLayoutParams(footer_params);
                    footer.setBackgroundColor(textColor);

                    first_lang_holder.addView(first_lang_text);
                    second_lang_holder.addView(second_lang_text);
                    general_holder.addView(first_lang_holder);
                    general_holder.addView(second_lang_holder);
                    general_holder.addView(footer);
                    trans_holder.addView(general_holder);
                }
            }

            TextView pronunciation_help = new TextView(this);
            LinearLayout.LayoutParams pronunciation_help_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pronunciation_help.setLayoutParams(pronunciation_help_params);
            pronunciation_help.setTextColor(textColor);
            pronunciation_help.setText(R.string.pronunciation);
            pronunciation_help.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics()));
            pronunciation_help.setTypeface(null, Typeface.BOLD);
            pronunciation_help.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            trans_holder.addView(pronunciation_help);

        } catch (JSONException js) {
            js.printStackTrace();
        }

        ScrollView scrollViewTranslation = findViewById(R.id.scrollViewTranslation);
        scrollViewTranslation.setOnTouchListener(new OnSwipeTouchListener(TranslationDetails.this) {
            @Override
            public void onSwipeRight() {
                finish();
                TranslationDetails.this.overridePendingTransition(0, R.anim.slide_out);
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                finish();
                TranslationDetails.this.overridePendingTransition(0, R.anim.slide_out_reverse);
                super.onSwipeLeft();
            }
        });

    }
}
