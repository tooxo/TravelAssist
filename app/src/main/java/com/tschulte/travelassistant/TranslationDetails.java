package com.tschulte.travelassistant;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TranslationDetails extends AppCompatActivity {

    public Map<String, String> getAllLocales(){
        Map<String, String> locales = new HashMap<>();
        for (String iso : Locale.getISOCountries()){
            Locale l = new Locale("", iso);
            locales.put(l.getDisplayCountry(), iso);
        }
        return locales;
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.translation);
        TextToSpeechController controller = new TextToSpeechController(this);
        try {
            String own = Locale.getDefault().getDisplayCountry(Locale.ENGLISH).toLowerCase();
            JSONObject json = new JSONObject(extras.getString("JSON"));
            ImageView fromLanguage = findViewById(R.id.fromLanguage);
            ImageView toLanguage = findViewById(R.id.toLanguage);
            fromLanguage.setImageResource(getResources().getIdentifier(own+"_square","drawable",getPackageName()));
            toLanguage.setImageResource(getResources().getIdentifier(json.getString("name").toLowerCase()+"_square","drawable",getPackageName()));
            Locale toLocale = new Locale.Builder().setLanguage(json.getString("language")).setRegion(json.getString("country")).build();
            fromLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.speakTheText(Locale.getDefault().getDisplayLanguage(Locale.getDefault()), Locale.getDefault().getLanguage(),Locale.getDefault().getCountry());
                }
            });
            toLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.speakTheText(toLocale.getDisplayLanguage(Locale.getDefault()), Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
                }
            });

            List<JSONObject> ls = new ArrayList<>();
            JSONObject ob = (JSONObject)json.get("translation");
            Iterator<String> iterator = ob.keys();

            while (iterator.hasNext()){
                String re = iterator.next();
                ls.add((JSONObject)ob.get(re));
                Log.v("NICE", re);
            }


        }catch (JSONException js){
            js.printStackTrace();
        }
    }
}
