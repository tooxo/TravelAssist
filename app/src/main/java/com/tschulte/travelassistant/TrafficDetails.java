package com.tschulte.travelassistant;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class TrafficDetails extends AppCompatActivity {

    void popup(String message, Context ctx){

        final SpannableString s = new SpannableString(message);
        Linkify.addLinks(s, Linkify.ALL);

        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle(getString(R.string.information))
                .setMessage(s)
                .create();
        d.show();
        ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)d.findViewById(android.R.id.message)).setClickable(true);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    @Override
    public void onCreate(Bundle bundle){
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        this.setContentView(R.layout.traffic);
        ImageView imageView = this.findViewById(R.id.banner_traffic);
        Bundle extras = getIntent().getExtras();
        String jsonString = extras.getString("JSON");
        try {
            Context context = this;
            JSONObject js = new JSONObject(jsonString);
            Log.v("String", js.toString());
            JSONObject traffic = (JSONObject) js.get("traffic");
            int id = getResources().getIdentifier(js.getString("name").toLowerCase() + "_square", "drawable", getPackageName());
            imageView.setImageResource(id);
            TextView age = this.findViewById(R.id.min_year);
            age.setText(traffic.get("min_age").toString());
            TextView super_age = this.findViewById(R.id.min_supervised_traffic);
            super_age.setText(traffic.get("min_age_supervision").toString());
            ImageView lane = findViewById(R.id.road_side);
            if (traffic.get("road_side").toString().equals("right")){
                lane.setImageResource(R.drawable.right_lane);
            }else{
                lane.setImageResource(R.drawable.left_lane);
            }
            LinearLayout ll = findViewById(R.id.lane_ll);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String string = getResources().getString(R.string.drive_lane_instructions, traffic.getString("road_side"));
                        popup(string, context);
                    }catch (JSONException js){
                        js.printStackTrace();
                    }

                }
            });
            ImageView highway = this.findViewById(R.id.highway_cost_traffic);
            final int yesno;
            if (traffic.getBoolean("highway_free")){
                highway.setImageResource(R.drawable.thumb_up);
                highway.setColorFilter(Color.rgb(0,255,0));
                yesno = getResources().getIdentifier("is", "string", getPackageName());
            }else{
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
            TextView alcoholTolerance = findViewById(R.id.alcohol_tolerance);
            alcoholTolerance.setText(traffic.getString("alcohol_tolerance_in_bac_percent")+"%");
            LinearLayout alcoholToleranceLayout = findViewById(R.id.alcohol_tolerance_ll);
            alcoholToleranceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup(getResources().getString(R.string.alcohol_tolerance_instructions),context);
                }
            });
        }catch (JSONException js){
            js.printStackTrace();
        }
    }
}
