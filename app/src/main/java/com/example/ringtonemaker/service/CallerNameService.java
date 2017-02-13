package com.example.ringtonemaker.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
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
        implements TextToSpeech.OnInitListener
{
    int result = 0;
    TextToSpeech tts;
    String str_ringtone = "",path="",ringtoneName="";
    RingtoneManager ringtoneManager;
    Ringtone defaultRingtone;
    Uri defaultRingtoneUri;
    String ringing_state = "";
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private static final String TAG = "TTS Demo";
    HashMap<String, String> myHashRender = new HashMap<String, String>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Log.e("CallerNameService","onCreate of Service called");

        String RingtoneUri = intent.getStringExtra("ringtone_uri");
        String caller_name = intent.getStringExtra("caller_name");

        tts = new TextToSpeech(this, this);

        defaultRingtoneUri = Uri.parse(RingtoneUri);
        Log.i("default",defaultRingtoneUri+"");
        path = defaultRingtoneUri.getPath();

        defaultRingtone = RingtoneManager.getRingtone(this, defaultRingtoneUri);
        ringtoneName = defaultRingtone.getTitle(this);

        path = new File(path).getParent();
        Log.i("ringtoneName",ringtoneName);

        str_ringtone = "Hey " + caller_name + " is calling you,pic up the phone";

        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, str_ringtone);

        if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED)
        {
            Log.e("TTS", "This Language is not supported");
        }
        else
        {

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate()
    {
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

        if (status == TextToSpeech.SUCCESS)
        {
            speakOut(str_ringtone);

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String str_ringtone)
    {
        tts.speak(str_ringtone, TextToSpeech.QUEUE_FLUSH, myHashRender);

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId)
            {

            }

            @Override
            public void onDone(String utteranceId)
            {
                Log.e("done",utteranceId);
                tts.stop();
                tts = null;

                path = path + "/" + ringtoneName;
                Log.i("uri",path);
                File k = new File(path);

                Log.i("path",k.getAbsolutePath());
                Log.i("name",k.getName());

                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, "content://media" + k.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, k.getName());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
                values.put(MediaStore.Audio.Media.DURATION, 230);
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                values.put(MediaStore.Audio.Media.IS_ALARM, false);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                Uri newUri = getContentResolver().insert(Uri.parse("content://media" + path + ".mp3"), values);

                RingtoneManager.setActualDefaultRingtoneUri(
                        CallerNameService.this,
                        RingtoneManager.TYPE_RINGTONE,
                        newUri
                );

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_TTS_STATUS_CHECK)
        {
            switch (resultCode)
            {
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
