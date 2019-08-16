package com.tschulte.travelassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;
import static com.tschulte.travelassistant.MainActivity.themeAttributeToColor;

public class DetailsActivity extends AppCompatActivity {

    public void startTraffic(JSONObject json) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.JSON, json.toString());
        Intent i = new Intent(this, TrafficDetails.class);
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void startAlcohol(JSONObject json) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.JSON, json.toString());
        Intent i = new Intent(DetailsActivity.this, AlcoholDetails.class);
        i.putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void startTranslation(JSONObject json) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.JSON, json.toString());
        Intent i = new Intent(DetailsActivity.this, TranslationDetails.class);
        i.putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_down);
    }

    @Override
    public void onCreate(Bundle bundle) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        Bundle b = getIntent().getExtras();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        } else {
            setTheme(R.style.Theme_Hell);
        }
        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));

        setContentView(R.layout.detail_view);
        try {
            JSONObject json = new JSONObject(b.getString(MainActivity.JSON));
            ImageView imageView = findViewById(R.id.banner);
            int id = this.getResources().getIdentifier(json.get("name").toString().toLowerCase() + "_square", "drawable", this.getPackageName());
            imageView.setImageResource(id);
            FontFitTextView ff = findViewById(R.id.title_detail);
            Locale lo = new Locale.Builder()
                    .setLanguageTag(json.getString("language"))
                    .setRegion(json.getString("country"))
                    .build();
            String displayCounty = lo.getDisplayCountry();

            ff.setText(displayCounty);
         /*   this.findViewById(R.id.detail_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });*/
            this.findViewById(R.id.detail_lin).setOnClickListener(v -> startTraffic(json));
            this.findViewById(R.id.detail_alc).setOnClickListener(v -> startAlcohol(json));
            this.findViewById(R.id.detail_trans).setOnClickListener(v -> startTranslation(json));

            ScrollView detailScroll = findViewById(R.id.scrollViewDetail);
            OnSwipeTouchListener listener = new OnSwipeTouchListener(DetailsActivity.this) {
                @Override
                public void onSwipeRight() {
                    finish();
                    DetailsActivity.this.overridePendingTransition(0, R.anim.slide_out);
                    super.onSwipeRight();
                }

                @Override
                public void onSwipeLeft() {
                    finish();
                    DetailsActivity.this.overridePendingTransition(0, R.anim.slide_out_reverse);
                    super.onSwipeLeft();
                }

                @Override
                public void onSwipeUp() {
                    // finish();
                    // DetailsActivity.this.overridePendingTransition(0, R.anim.slide_up_weg);
                    super.onSwipeUp();
                }

                @Override
                public void onSwipeDown() {
                    // finish();
                    // DetailsActivity.this.overridePendingTransition(0, R.anim.slide_out_down);
                    super.onSwipeDown();
                }
            };
            detailScroll.setOnTouchListener(listener);


        } catch (JSONException | NullPointerException js) {
            js.printStackTrace();
        }

    }
}
