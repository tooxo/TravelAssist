package com.tschulte.travelassistant;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeechController implements TextToSpeech.OnInitListener {

    private Context mContext;

    private TextToSpeech tts;

    TextToSpeechController(Context context) {
        mContext = context;
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(mContext, "This Language is not supported", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext, "Can Not Speak", Toast.LENGTH_LONG).show();
        }
    }

    void stopTTS() {
        tts.stop();
        tts.shutdown();
    }

    void speakTheText(String text, String language, String region) {
        Locale locale = new Locale.Builder().setLanguage(language).setRegion(region).build();
        tts.setLanguage(locale);
        String utterance = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utterance);
    }
}