package com.tschulte.travelassistant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;

public class Sources extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        } else {
            setTheme(R.style.Theme_Hell);
        }

        setContentView(R.layout.sources);

        InputStream is = this.getResources().openRawResource(R.raw.sources);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder textBuilder = new StringBuilder();

        try {
            String line;
            while ((line = br.readLine()) != null) {
                textBuilder.append(line);
                textBuilder.append("\n");
            }

        } catch (IOException ignored) {
        }

        TextView textView = findViewById(R.id.sourcesText);
        textView.setText(textBuilder.toString());

        ScrollView scrollView = findViewById(R.id.scrollSources);
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(Sources.this) {
            @Override
            public void onSwipeRight() {
                finish();
                Sources.this.overridePendingTransition(0, R.anim.slide_out);
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                finish();
                Sources.this.overridePendingTransition(0, R.anim.slide_out_reverse);
                super.onSwipeLeft();
            }
        };
        scrollView.setOnTouchListener(onSwipeTouchListener);
        LinearLayout ll = findViewById(R.id.linearSources);
        textView.setOnTouchListener(onSwipeTouchListener);

    }
}
