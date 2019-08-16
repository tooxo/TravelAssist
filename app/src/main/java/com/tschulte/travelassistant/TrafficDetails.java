package com.tschulte.travelassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tschulte.travelassistant.MainActivity.SETTING_DARK_MODE;
import static com.tschulte.travelassistant.MainActivity.themeAttributeToColor;

public class TrafficDetails extends AppCompatActivity {

    void popup(String message, Context ctx) {

        final SpannableString s = new SpannableString(message);
        Linkify.addLinks(s, Linkify.ALL);

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle(getString(R.string.information))
                .setMessage(s)
                .create();
        d.show();
        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) d.findViewById(android.R.id.message)).setClickable(true);

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
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SETTING_DARK_MODE, false)) {
            setTheme(R.style.Theme_Dunkel);
        } else {
            setTheme(R.style.Theme_Hell);
        }
        this.getWindow().setStatusBarColor(themeAttributeToColor(R.attr.colorPrimary, this, getResources().getColor(R.color.colorPrimary)));

        this.setContentView(R.layout.traffic);
        ImageView imageView = this.findViewById(R.id.banner_traffic);
        Bundle extras = getIntent().getExtras();
        String jsonString = extras.getString(MainActivity.JSON);
        try {
            Context context = this;
            JSONObject js = new JSONObject(jsonString);
            JSONObject traffic = (JSONObject) js.get("traffic");
            int id = getResources().getIdentifier(js.getString("name").toLowerCase() + "_square", "drawable", getPackageName());
            imageView.setImageResource(id);
            TextView age = this.findViewById(R.id.min_year);
            age.setText(traffic.get("min_age").toString());
            TextView super_age = this.findViewById(R.id.min_supervised_traffic);
            super_age.setText(traffic.get("min_age_supervision").toString());
            ImageView lane = findViewById(R.id.road_side);
            String laneSide;
            if (traffic.get("road_side").toString().equals("right")) {
                lane.setImageResource(R.drawable.right_lane);
                laneSide = getResources().getString(R.string.right);
            } else {
                lane.setImageResource(R.drawable.left_lane);
                laneSide = getResources().getString(R.string.left);
            }
            LinearLayout ll = findViewById(R.id.lane_ll);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = getResources().getString(R.string.drive_lane_instructions, laneSide);
                    popup(string, context);
                }
            });
            ImageView highway = this.findViewById(R.id.highway_cost_traffic);
            final int yesno;
            if (traffic.getBoolean("highway_free")) {
                highway.setImageResource(R.drawable.thumb_up);
                highway.setColorFilter(Color.rgb(0, 255, 0));
                yesno = getResources().getIdentifier("is", "string", getPackageName());
            } else {
                highway.setImageResource(R.drawable.thumb_down);
                highway.setColorFilter(Color.rgb(255, 0, 0));
                yesno = getResources().getIdentifier("not", "string", getPackageName());
            }
            LinearLayout hwll = findViewById(R.id.highway_ll);
            String calculator = traffic.getString("calculator");
            hwll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup(getResources().getString(R.string.highway_free_instructions, getResources().getString(yesno), calculator), context);
                }
            });
            TextView in_town = findViewById(R.id.speedmeter_in_town);
            in_town.setText(traffic.getString("speed_in_town"));
            TextView country_roads = findViewById(R.id.speedmeter_country_roads);
            country_roads.setText(traffic.getString("speed_single_lane"));
            TextView highwayspeed = findViewById(R.id.speedmeter_highway);
            highwayspeed.setText(traffic.getString("speed_highway"));
            TextView speedTolerance = findViewById(R.id.speed_tolerance);
            speedTolerance.setText(traffic.getString("speed_tolerance_in_percent") + "%");

            //TODO: ALCOHOL TOLERANCE UNITS

            TextView alcoholTolerance = findViewById(R.id.alcohol_tolerance);
            alcoholTolerance.setText(traffic.getString("alcohol_tolerance_in_promille") + "%");
            LinearLayout alcoholToleranceLayout = findViewById(R.id.alcohol_tolerance_ll);
            alcoholToleranceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup(getResources().getString(R.string.alcohol_tolerance_instructions), context);
                }
            });
        } catch (JSONException js) {
            js.printStackTrace();
        }


        ScrollView scrollViewTraffic = findViewById(R.id.scrollViewTraffic);
        scrollViewTraffic.setOnTouchListener(new OnSwipeTouchListener(TrafficDetails.this) {
            @Override
            public void onSwipeRight() {
                finish();
                TrafficDetails.this.overridePendingTransition(0, R.anim.slide_out);
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                finish();
                TrafficDetails.this.overridePendingTransition(0, R.anim.slide_out_reverse);
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeUp() {
                // finish();
                // TrafficDetails.this.overridePendingTransition(0, R.anim.slide_up_weg);
                super.onSwipeUp();
            }

            @Override
            public void onSwipeDown() {
                // finish();
                // TrafficDetails.this.overridePendingTransition(0, R.anim.slide_out_down);
                super.onSwipeDown();
            }
        });


    }
}
