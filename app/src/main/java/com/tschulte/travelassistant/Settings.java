package com.tschulte.travelassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;
import static com.tschulte.travelassistant.MainActivity.themeAttributeToColor;

public class Settings extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
        finish();
        // super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        } else {
            setTheme(R.style.Theme_Hell);
        }

        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));

        setContentView(R.layout.settings);

        Switch ownCountrySetting = findViewById(R.id.ownCountrySetting);
        boolean ownCountrySettingState = preferences.getBoolean(MainActivity.OWN_COUNTRY_SETTING, true);
        ownCountrySetting.setChecked(ownCountrySettingState);
        ownCountrySetting.setOnCheckedChangeListener((buttonView, isChecked) -> editor.putBoolean(MainActivity.OWN_COUNTRY_SETTING, isChecked).apply());

        Switch darkMode = findViewById(R.id.darkThemeSwitch);
        boolean darkModeSettingState = preferences.getBoolean(SETTING_DARK_MODE, false);
        darkMode.setChecked(darkModeSettingState);
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SETTING_DARK_MODE, isChecked).apply();
                recreate();
            }
        });

        Button sources = findViewById(R.id.sourcesButton);
        sources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Sources.class);
                startActivity(intent);
            }
        });

        LinearLayout ln = findViewById(R.id.linearLayoutSettings);
        OnSwipeTouchListener listener = new OnSwipeTouchListener(Settings.this) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
                finish();
                Settings.this.overridePendingTransition(0, R.anim.slide_out_reverse);
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
                finish();
                Settings.this.overridePendingTransition(0, R.anim.slide_out);
                super.onSwipeRight();
            }

            /*
            @Override
            public void onSwipeUp() {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
                finish();
                Settings.this.overridePendingTransition(0, R.anim.slide_up_weg);
                super.onSwipeUp();
            }

            @Override
            public void onSwipeDown() {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
                finish();
                Settings.this.overridePendingTransition(0, R.anim.slide_out_down);
                super.onSwipeDown();
            }*/
        };

        ln.setOnTouchListener(listener);
        ScrollView sv = findViewById(R.id.scrollViewSettings);
        sv.setOnTouchListener(listener);

    }
}
