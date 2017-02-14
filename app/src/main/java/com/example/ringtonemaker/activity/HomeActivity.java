package com.example.ringtonemaker.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ringtonemaker.R;
import com.example.ringtonemaker.custom.CustomTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ringtonemaker.utils.Util.hasPermissions;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.tvHome)
    CustomTextView tvHome;
    @BindView(R.id.ivinfo)
    ImageView ivinfo;
    @BindView(R.id.ivprivacy)
    ImageView ivprivacy;
    @BindView(R.id.ivthumb)
    ImageView ivthumb;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvCallerIcon)
    TextView tvCallerIcon;
    @BindView(R.id.tvSpeakCallerName)
    CustomTextView tvSpeakCallerName;
    @BindView(R.id.tvon_off_slider)
    TextView tvonOffSlider;
    @BindView(R.id.tvOff)
    TextView tvOff;
    @BindView(R.id.tvOn)
    TextView tvOn;
    @BindView(R.id.content_home)
    LinearLayout contentHome;
    @BindView(R.id.tvCreateIcon)
    TextView tvCreateIcon;
    @BindView(R.id.tvCreateNewRingtone)
    CustomTextView tvCreateNewRingtone;
    @BindView(R.id.tvListIcon)
    TextView tvListIcon;
    @BindView(R.id.tvDisplayAllRingtones)
    CustomTextView tvDisplayAllRingtones;

    RelativeLayout rlAds;
    AdView adView;


    boolean callerFlag = false;

    private static final int PERMISSION_ALL = 200;

    String[] PERMISSIONS =
            {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        rlAds = (RelativeLayout) findViewById(R.id.rlAds);
        adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                rlAds.setVisibility(View.VISIBLE);
                super.onAdLoaded();

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();

            }
        });

        callerFlag = getSharedPreferences("RINGTONE_MAKER", 0).getBoolean("callerFlag", false);
        if (callerFlag) {
            tvOff.setVisibility(View.GONE);
            tvOn.setVisibility(View.VISIBLE);
        } else {
            tvOn.setVisibility(View.GONE);
            tvOff.setVisibility(View.VISIBLE);
        }
        if (hasPermissions(HomeActivity.this, PERMISSIONS)) {
            tvOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvOff.setVisibility(View.GONE);
                    tvOn.setVisibility(View.VISIBLE);
                    callerFlag = true;
                    saveCallerFlagValue("callerFlag", callerFlag);
                }
            });

            tvOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvOn.setVisibility(View.GONE);
                    tvOff.setVisibility(View.VISIBLE);
                    callerFlag = false;
                    saveCallerFlagValue("callerFlag", callerFlag);
                }
            });

            tvCreateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createRingtone = new Intent(HomeActivity.this, RingtoneMakerActivity.class);
                    startActivity(createRingtone);
                }
            });

            tvListIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myRingtone = new Intent(HomeActivity.this, MyRingtonesActivity.class);
                    startActivity(myRingtone);
                }
            });

        } else {
            requestToPermissions();
            tvOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(HomeActivity.this)) {
                            tvOff.setVisibility(View.GONE);
                            tvOn.setVisibility(View.VISIBLE);
                            callerFlag = true;
                            saveCallerFlagValue("callerFlag", callerFlag);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            });

            tvOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvOn.setVisibility(View.GONE);
                    tvOff.setVisibility(View.VISIBLE);
                    callerFlag = false;
                    saveCallerFlagValue("callerFlag", callerFlag);
                }
            });

            tvCreateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createRingtone = new Intent(HomeActivity.this, RingtoneMakerActivity.class);
                    startActivity(createRingtone);
                }
            });

            tvListIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myRingtone = new Intent(HomeActivity.this, MyRingtonesActivity.class);
                    startActivity(myRingtone);
                }
            });

        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File path = new File(root + "/Ringtone_Maker");

            boolean flag = deleteDirectory(path);
            Log.d("flag", flag + "");


            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }

    @Override
    public void onBackPressed() {
        dialogForExit();
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists() && path.isDirectory()) {
            Log.e("true", "true");

            path.setReadable(true);
            File temp[] = path.listFiles();
            for (File file : temp) {
                file.delete();
            }
            return true;
        } else {
            Log.e("false", "false");
            return false;
        }
    }

    public void dialogForExit() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.item_exit_alert);
        Button btnAlertOk = (Button) dialog.findViewById(R.id.btnAlertExit);
        Button btnAlertCancle = (Button) dialog.findViewById(R.id.btnAlertCancle);
        btnAlertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAlertCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void saveCallerFlagValue(String key, boolean flag) {
        SharedPreferences preferences = getSharedPreferences("RINGTONE_MAKER", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    private void requestToPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @OnClick({R.id.ivinfo,R.id.ivprivacy,R.id.ivthumb})
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.ivinfo: {
                Intent intent = new Intent(HomeActivity.this, InformationActivity.class);
                startActivity(intent);
            }
            case R.id.ivprivacy:
            {

            }
            case R.id.ivthumb:
            {

            }
        }
    }
}
