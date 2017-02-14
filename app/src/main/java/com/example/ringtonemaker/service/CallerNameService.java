package com.example.ringtonemaker.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Mishtiii on 13-02-2017.
 */

public class CallerNameService extends Service
        implements TextToSpeech.OnInitListener {
    int result = 0;
    TextToSpeech tts;
    String str_ringtone = "", path = "";
    RingtoneManager ringtoneManager;

    private static final int REQ_TTS_STATUS_CHECK = 0;
    private static final String TAG = "TTS Demo";

    HashMap<String, String> myHashRender = new HashMap<String, String>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final int[] volume = new int[1];
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        volume[0] = am.getStreamVolume(AudioManager.STREAM_RING);
        am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);

        Log.e("CallerNameService", "onCreate of Service called");

        String caller_name = intent.getStringExtra("caller_name");

        Log.e("caller", caller_name);

        tts = new TextToSpeech(this, this);

        str_ringtone = "Hey " + caller_name + " is calling you,pic up the phone";

        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, str_ringtone);

        if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "This Language is not supported");
        } else {

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        ringtoneManager = new RingtoneManager(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            speakOut(str_ringtone);

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        tts.stop();
        tts = null;
        super.onDestroy();
    }

    private void speakOut(String str_ringtone) {
        //final int[] volume = new int[1];
        Log.i("path", path);
        final String ring = str_ringtone;
    /*    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_MUSIC));
    */    tts.speak(str_ringtone, TextToSpeech.QUEUE_FLUSH, myHashRender);

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
               /* AudioManager am =
                        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                volume[0] = am.getStreamVolume(AudioManager.STREAM_RING);
                am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_PLAY_SOUND);*/

            }

            @Override
            public void onDone(String utteranceId) {
                Log.e("done", utteranceId);

                tts.speak(ring, TextToSpeech.QUEUE_FLUSH, myHashRender);

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TTS_STATUS_CHECK) {
            switch (resultCode) {
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS: {

                    break;
                }
                case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL: {
                    Intent installIntent = new Intent();
                    installIntent.setAction(
                            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                    break;
                }
                default: {
                    Log.e(TAG, "Got a failure. TTS apparently not available");
                }
            }
        }
    }

}

