package com.example.ringtonemaker.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ringtonemaker.R;
import com.example.ringtonemaker.custom.CustomButton;
import com.example.ringtonemaker.custom.CustomEditText;
import com.example.ringtonemaker.custom.CustomTextView;
import com.example.ringtonemaker.database.DBHandler;
import com.example.ringtonemaker.model.Ringtones;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.ringtonemaker.utils.Constants.size;
import static com.example.ringtonemaker.utils.Util.getStoragePath;
import static com.example.ringtonemaker.utils.Util.hasPermissions;

public class RingtoneMakerActivity extends BaseActivity implements
        TextToSpeech.OnInitListener {

    @BindView(R.id.tvRingtoneMaker)
    CustomTextView tvRingtoneMaker;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvSelectPrefixLabel)
    CustomTextView tvSelectPrefixLabel;
    @BindView(R.id.spPrefix)
    Spinner spPrefix;
    @BindView(R.id.tvEnterNameLabel)
    CustomTextView tvEnterNameLabel;
    @BindView(R.id.etEnterName)
    CustomEditText etEnterName;
    @BindView(R.id.tvSelectPostfixLabel)
    CustomTextView tvSelectPostfixLabel;
    @BindView(R.id.spPostfix)
    Spinner spPostfix;
    @BindView(R.id.tvLanguageLabel)
    CustomTextView tvLanguageLabel;
    @BindView(R.id.spLanguage)
    Spinner spLanguage;
    @BindView(R.id.tvVolumeLabel)
    CustomTextView tvVolumeLabel;
    @BindView(R.id.tvVolume)
    CustomTextView tvVolume;
    @BindView(R.id.sbVolume)
    SeekBar sbVolume;
    @BindView(R.id.btnSave)
    CustomButton btnSave;
    @BindView(R.id.btnPlay)
    ImageButton btnPlay;
    @BindView(R.id.btnSetAs)
    CustomButton btnSetAs;
    @BindView(R.id.content_ringtone_maker)
    RelativeLayout contentRingtoneMaker;

    TextToSpeech tts;
    String str_ringtone = "", lang = "";
    int volume, position, id = 0;
    DBHandler dbHandler;
    private MediaPlayer player = null;
    String selectedPrefix = "", selectedPostfix = "", selectedLanguage = "", destinationFile;
    ArrayAdapter<String> prefixAdapter;
    ArrayAdapter<String> postfixAdapter;
    ArrayAdapter<String> languageAdapter;
    HashMap<String, String> myHashRender = new HashMap<String, String>();

    private static final int REQ_TTS_STATUS_CHECK = 0;
    private static final String TAG = "TTS Demo";
    private static final int PERMISSION_ALL = 100;

    String[] setAs = new String[]{
            "Ringtone",
            "SMS Tone",
            "Alarm Tone"};

    List<String> lstPrefixSpinner;
    List<String> lstPostfixSpinner;
    List<String> lstLanguageSpinner;

    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone_maker);
        ButterKnife.bind(this);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);

        dbHandler = new DBHandler(this);

        lang = getSharedPreferences("RINGTONE_MAKER", 0).getString("language", "US");

        lstPrefixSpinner = new ArrayList<String>();
        lstPrefixSpinner.add("None");
        lstPrefixSpinner.add("Heya");
        lstPrefixSpinner.add("Hello");
        lstPrefixSpinner.add("Hii");
        lstPrefixSpinner.add("Mister");
        lstPrefixSpinner.add("Miss");
        lstPrefixSpinner.add("Doctor");
        lstPrefixSpinner.add("Excuse Me");
        lstPrefixSpinner.add("What's up");

        lstPostfixSpinner = new ArrayList<String>();
        lstPostfixSpinner.add("None");
        lstPostfixSpinner.add("Please answer the phone");
        lstPostfixSpinner.add("Your phone is ringing");
        lstPostfixSpinner.add("Pick up the phone");
        lstPostfixSpinner.add("Your phone is ringing please answer the phone");
        lstPostfixSpinner.add("someone is calling you");
        lstPostfixSpinner.add("Please receive your call");

        lstLanguageSpinner = new ArrayList<String>();
        lstLanguageSpinner.add("US");
        lstLanguageSpinner.add("Hindi");
        lstLanguageSpinner.add("Canada-French");
        lstLanguageSpinner.add("Germany");
        lstLanguageSpinner.add("Italy");
        lstLanguageSpinner.add("Japan");
        lstLanguageSpinner.add("China");

        prefixAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner, lstPrefixSpinner);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefix.setAdapter(prefixAdapter);
        addListenerOnSpinnerItemSelection();

        postfixAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner, lstPostfixSpinner);
        postfixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPostfix.setAdapter(postfixAdapter);
        addListenerOnSpinnerItemSelection();

        languageAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner, lstLanguageSpinner);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLanguage.setAdapter(languageAdapter);
        addListenerOnSpinnerItemSelection();

        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVolume.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvVolume.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sbVolume.setProgress(seekBar.getProgress());
                tvVolume.setText(String.valueOf(seekBar.getProgress()));
            }
        });

        volume = this.getSharedPreferences("RINGTONE_MAKER", 0).getInt("volume", 20);
        tvVolume.setText(String.valueOf(volume));
        sbVolume.setProgress(volume);


    }

    public void addListenerOnSpinnerItemSelection() {
        spPrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                adapterView.getItemAtPosition(pos).toString();
                selectedPrefix = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spPostfix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                adapterView.getItemAtPosition(pos).toString();
                selectedPostfix = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                adapterView.getItemAtPosition(pos).toString();
                selectedLanguage = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {


        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @OnClick({R.id.btnSave, R.id.btnPlay, R.id.btnSetAs})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
            {
                if(!btnSave.isEnabled())
                {
                    Toast.makeText(this, "Please enter th text and play to save!!!", Toast.LENGTH_SHORT).show();
                }
                if (tts.isSpeaking())
                {
                    tts.stop();
                }
                if (hasPermissions(RingtoneMakerActivity.this, PERMISSIONS))
                {
                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, str_ringtone);
                    final EditText input = new EditText(RingtoneMakerActivity.this);
                    AlertDialog.Builder alert = new AlertDialog.Builder(RingtoneMakerActivity.this);
                    alert.setTitle("Save As:");
                    alert.setView(input);
                    alert.setPositiveButton("Ok", new DialogInterface.
                            OnClickListener() {
                        File path = getStoragePath(RingtoneMakerActivity.this);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String tempFilename = input.getText().toString() + ".mp3";
                            tempFilename = tempFilename.trim();
                            destinationFile = path.getAbsolutePath() + tempFilename;
                            if (tts.synthesizeToFile(str_ringtone, myHashRender, destinationFile)
                                    == TextToSpeech.SUCCESS) {

                                id = ++size;
                                Ringtones ringtones = new Ringtones();
                                ringtones.setId(++id);
                                ringtones.setRingtone_name(tempFilename);
                                ringtones.setRingtone_source(destinationFile);
                                dbHandler.addRingtone(ringtones);
                                savePath("path", destinationFile);
                                btnSetAs.setEnabled(true);
                                new SweetAlertDialog(RingtoneMakerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Good job!")
                                        .setContentText("Sound file created!")
                                        .show();
                                //Toast.makeText(getBaseContext(), "Sound file created", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                new SweetAlertDialog(RingtoneMakerActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Sound file not created!")
                                        .show();
                                //Toast.makeText(getBaseContext(), "Oops! Sound file not created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);


                } else {
                    requestToPermissions();
                }
            }
            break;
            case R.id.btnPlay: {
                str_ringtone = "";
                if (!selectedPrefix.equals("None")) {
                    str_ringtone += selectedPrefix
                            + " ";
                }
                if (!etEnterName.equals("")) {
                    str_ringtone += etEnterName.getText().toString()
                            + " ";
                }
                if (!selectedPostfix.equals("None")) {
                    str_ringtone += selectedPostfix;
                }

                if (str_ringtone.equals("")) {
                    Toast.makeText(this, "Please Enter some text!!!", Toast.LENGTH_SHORT).show();
                    spPrefix.setFocusable(true);
                    btnSave.setEnabled(false);
                    btnSetAs.setEnabled(false);
                }
                else
                {
                    btnSave.setEnabled(true);


                    int result = 0;

                    switch (selectedLanguage) {
                        case "US": {
                            result = tts.setLanguage(new Locale("en", "US", "variant"));
                            break;
                        }
                        case "Hindi": {
                            result = tts.setLanguage(new Locale("hin", "IND", "variant"));
                            break;
                        }
                        case "Canada-French": {
                            result = tts.setLanguage(new Locale("fr", "CA", "variant"));
                            break;
                        }
                        case "Germany": {
                            result = tts.setLanguage(new Locale("de", "DE", "variant"));
                            break;
                        }
                        case "Italy": {
                            result = tts.setLanguage(new Locale("it", "IT", "variant"));
                            break;
                        }
                        case "Japan": {
                            result = tts.setLanguage(new Locale("ja", "JP", "variant"));
                            break;
                        }
                        case "China": {
                            result = tts.setLanguage(new Locale("zh", "TW", "variant"));
                            break;
                        }

                    }
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {
                        speakOut(str_ringtone);
                    }
                }
                break;
            }
            case R.id.btnSetAs:
            {
                if(!btnSetAs.isEnabled())
                {
                    Toast.makeText(this, "Please save the tone first to set as ringtone, notification or alarm tone!!! ", Toast.LENGTH_SHORT).show();
                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RingtoneMakerActivity.this);
                alertDialog.setTitle("Set As:");


                alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setItems(setAs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.System.canWrite(RingtoneMakerActivity.this)) {
                                String path = getSharedPreferences("RINGTONE_MAKER", 0).getString("path", "");
                                String strName = Arrays.asList(setAs).get(which);
                                if (strName.equals("Ringtone"))
                                {
                                    File k = new File(path);

                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                                    values.put(MediaStore.MediaColumns.TITLE, k.getName());
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                                    values.put(MediaStore.Audio.Media.DURATION, 230);
                                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                                    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                                    values.put(MediaStore.Audio.Media.IS_ALARM, false);
                                    values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                    //Insert it into the database
                                    Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
                                    Uri newUri = getContentResolver().insert(uri, values);

                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            RingtoneMakerActivity.this,
                                            RingtoneManager.TYPE_RINGTONE,
                                            newUri
                                    );
                                }
                                else if (strName.equals("SMS Tone")) {
                                    File k = new File(path);

                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                                    values.put(MediaStore.MediaColumns.TITLE, k.getName());
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                                    values.put(MediaStore.Audio.Media.DURATION, 200);
                                    values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                                    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                                    values.put(MediaStore.Audio.Media.IS_ALARM, false);
                                    values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                    //Insert it into the database
                                    Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
                                    Uri newUri = getContentResolver().insert(uri, values);

                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            RingtoneMakerActivity.this,
                                            RingtoneManager.TYPE_RINGTONE,
                                            newUri
                                    );
                                }
                                else
                                {
                                    File k = new File(path);

                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                                    values.put(MediaStore.MediaColumns.TITLE, k.getName());
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                                    values.put(MediaStore.Audio.Media.DURATION, 150);
                                    values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                                    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                                    values.put(MediaStore.Audio.Media.IS_ALARM, true);
                                    values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                    //Insert it into the database
                                    Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
                                    Uri newUri = getContentResolver().insert(uri, values);

                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            RingtoneMakerActivity.this,
                                            RingtoneManager.TYPE_RINGTONE,
                                            newUri
                                    );
                                }
                            } else {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                });
                alertDialog.show();

                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    private void speakOut(String str_ringtone) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        /*int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);*/
        int amStreamMusicMaxVol = Integer.parseInt(tvVolume.getText().toString());
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        tts.speak(str_ringtone, TextToSpeech.QUEUE_FLUSH, null);
    }

    /*private void saveSelectedLanguage(String key, String selectedLanguage) {
        SharedPreferences preferences = this.getSharedPreferences("RINGTONE_MAKER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, selectedLanguage);
        editor.commit();
    }*/

    private void savePath(String key, String path) {
        SharedPreferences preferences = this.getSharedPreferences("RINGTONE_MAKER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, path);
        editor.commit();
    }

    private void requestToPermissions()
    {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TTS_STATUS_CHECK) {
            switch (resultCode) {
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS: {
                    tts = new TextToSpeech(this, this);
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
