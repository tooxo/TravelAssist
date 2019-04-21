package com.tschulte.travelassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    public void startTraffic(JSONObject json){
        Bundle bundle = new Bundle();
        bundle.putString("JSON", json.toString());
        Log.v("Traffic", "Im here");
        Intent i = new Intent(this, TrafficDetails.class);
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void startAlcohol(JSONObject json){
        Bundle bundle = new Bundle();
        bundle.putString("JSON", json.toString());
        Intent i = new Intent(DetailsActivity.this, AlcoholDetails.class);
        i.putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void startTranslation(JSONObject json){
        Bundle bundle = new Bundle();
        bundle.putString("JSON", json.toString());
        Intent i = new Intent(DetailsActivity.this, TranslationDetails.class);
        i.putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle bundle){
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        Bundle b = getIntent().getExtras();
        setContentView(R.layout.detail_view);
        try {
            JSONObject json = new JSONObject(b.getString("JSON"));
            ImageView imageView = findViewById(R.id.banner);
            int id = this.getResources().getIdentifier(json.get("name").toString().toLowerCase() + "_square", "drawable", this.getPackageName());
            imageView.setImageResource(id);
            FontFitTextView ff = findViewById(R.id.title_detail);
            ff.setText(json.getString("name"));
         /*   this.findViewById(R.id.detail_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });*/
            this.findViewById(R.id.detail_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTraffic(json);
                }
            });
            this.findViewById(R.id.detail_alc).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAlcohol(json);
                }
            });
            this.findViewById(R.id.detail_trans).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTranslation(json);
                }
            });

        }catch (JSONException | NullPointerException js){
            js.printStackTrace();
        }

    }
}
