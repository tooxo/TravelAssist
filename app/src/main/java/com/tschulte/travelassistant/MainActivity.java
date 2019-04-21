package com.tschulte.travelassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    void LaunchDetails(JSONObject object){
        Bundle bundle = new Bundle();
        bundle.putString("JSON", object.toString());

        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        //finish();
    }

    void layout(List<JSONObject> lsob, Context con){
        try{
        Resources r = con.getResources();
        LinearLayout layout = findViewById(R.id.select_lin);
        layout.removeAllViews();
        int id = 12000;
        for (JSONObject o : lsob){
            LinearLayout l = new LinearLayout(this, null, R.attr.borderlessButtonStyle);
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setId(id);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics())));
            lp.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics()), 0, 0);
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
            fontFitTextView.setText(o.getString("name"));
            l.addView(fontFitTextView);
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        LaunchDetails(o);
                }
            });
            layout.addView(l);
        }}catch (Exception e){

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.country_select);

        Resources r = this.getResources();
        try {
            String jsonString = prefs.getString("JSON", "");
            JSONObject json = new JSONObject(jsonString);
            JSONArray array = json.getJSONArray("countries");
            Log.v("country", array.get(0).toString());
            List<JSONObject> obList = new ArrayList<JSONObject>();
            for (int x = 0; x < array.length(); x++){
                obList.add((JSONObject) array.get(x));
            }
            layout(obList, this);

            EditText search = findViewById(R.id.search);
            Context con = this;
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<JSONObject> ls = new ArrayList<JSONObject>();
                    if (count > 0){
                    for (JSONObject a : obList){
                        try {
                            if (a.get("name").toString().toLowerCase().contains(s.toString().toLowerCase())) {
                                ls.add(a);
                            }
                        }catch (JSONException js){}
                    }
                    layout(ls, con);
                }else{
                    layout(obList, con);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

        } catch (Exception js){
            // DATA ERROR > UPDATE NEEDED
            js.printStackTrace();
        }
    }
}
