package com.tschulte.travelassistant;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class AlcoholDetails extends AppCompatActivity {
    @Override
    public void onCreate(Bundle bundle){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        setContentView(R.layout.alcohol);
        Bundle extras = getIntent().getExtras();
        String jsons = extras.getString("JSON");
        Log.v("JSON", jsons);
        try {
            JSONObject json = new JSONObject(jsons);
            ((ImageView) findViewById(R.id.banner_alcohol)).setImageResource(this.getResources().getIdentifier(json.getString("name").toLowerCase()+"_square", "drawable", getPackageName()));
            JSONObject alcohol = (JSONObject) json.get("alcohol");
            ((TextView)findViewById(R.id.drinking_age_alcohol)).setText(alcohol.getString("min_possession_age"));
            ((TextView)findViewById(R.id.purchase_age_alcohol)).setText(alcohol.getString("min_buying_age"));

            ImageView imageView = findViewById(R.id.public_drinking_acceptable);
            int state = alcohol.getInt("public_drinking_acceptable");
            switch (state){
                case 0:
                    imageView.setImageResource(R.drawable.thumb_down);
                    imageView.setColorFilter(Color.rgb(255, 0, 0));
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.thumb_up);
                    imageView.setColorFilter(Color.rgb(0,255,0));
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.thumb_side);
                    imageView.setColorFilter(Color.rgb(255,255,0));
                    break;
            }


        }catch (JSONException js){
            js.printStackTrace();
        }

    }
}
