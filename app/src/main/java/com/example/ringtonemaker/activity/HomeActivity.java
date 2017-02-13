package com.example.ringtonemaker.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ringtonemaker.R;
import com.example.ringtonemaker.custom.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ringtonemaker.utils.Util.hasPermissions;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.tvHome)
    CustomTextView tvHome;
    @BindView(R.id.ivinfo)
    ImageView ivinfo;
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

    boolean callerFlag = false;

    private static final int PERMISSION_ALL = 200;

    String[] PERMISSIONS =
            {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

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
        }
    }

    @Override
    public void onBackPressed() {
        dialogForExit();
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

    @OnClick(R.id.ivinfo)
    public void onClick()
    {
        Intent intent = new Intent(HomeActivity.this,InformationActivity.class);
        startActivity(intent);
    }
}
