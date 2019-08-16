package com.tschulte.travelassistant;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;
import static com.tschulte.travelassistant.MainActivity.themeAttributeToColor;

public class AlcoholDetails extends AppCompatActivity {


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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        }else {
            setTheme(R.style.Theme_Hell);
        }
        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));


        setContentView(R.layout.alcohol);
        Bundle extras = getIntent().getExtras();
        String json_string = extras.getString(MainActivity.JSON);
        try {
            JSONObject json = new JSONObject(json_string);
            ((ImageView) findViewById(R.id.banner_alcohol)).setImageResource(this.getResources().getIdentifier(json.getString("name").toLowerCase() + "_square", "drawable", getPackageName()));
            JSONObject alcohol = (JSONObject) json.get("alcohol");
            ((TextView) findViewById(R.id.drinking_age_alcohol)).setText(alcohol.getString("min_possession_age"));
            ((TextView) findViewById(R.id.purchase_age_alcohol)).setText(alcohol.getString("min_buying_age"));

            ImageView imageView = findViewById(R.id.public_drinking_acceptable);
            int state = alcohol.getInt("public_drinking_acceptable");
            switch (state) {
                case 0:
                    imageView.setImageResource(R.drawable.thumb_down);
                    imageView.setColorFilter(Color.rgb(255, 0, 0));
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.thumb_up);
                    imageView.setColorFilter(Color.rgb(0, 255, 0));
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.thumb_side);
                    imageView.setColorFilter(Color.rgb(255, 255, 0));
                    break;
            }


        } catch (JSONException js) {
            js.printStackTrace();
        }


        ScrollView alcoholScroll = findViewById(R.id.scrollViewAlcohol);
        alcoholScroll.setOnTouchListener(new OnSwipeTouchListener(AlcoholDetails.this) {
            @Override
            public void onSwipeRight() {
                finish();
                AlcoholDetails.this.overridePendingTransition(0, R.anim.slide_out);
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                finish();
                AlcoholDetails.this.overridePendingTransition(0, R.anim.slide_out_reverse);
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeUp() {
                // finish();
                // AlcoholDetails.this.overridePendingTransition(0, R.anim.slide_up_weg);
                super.onSwipeUp();
            }

            @Override
            public void onSwipeDown() {
                // finish();
                // AlcoholDetails.this.overridePendingTransition(0, R.anim.slide_out_down);
                super.onSwipeDown();
            }
        });


    }
}
