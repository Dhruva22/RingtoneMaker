package com.example.ringtonemaker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.RelativeLayout;

import com.example.ringtonemaker.R;
import com.example.ringtonemaker.adapter.RingtoneAdapter;
import com.example.ringtonemaker.custom.CustomButton;
import com.example.ringtonemaker.custom.CustomTextView;
import com.example.ringtonemaker.database.DBHandler;
import com.example.ringtonemaker.model.Ringtones;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ringtonemaker.utils.Constants.size;

public class MyRingtonesActivity extends BaseActivity {
    InterstitialAd mInterstitialAd;

    @BindView(R.id.tvMyRingtones)
    CustomTextView tvMyRingtones;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.content_my_ringtones)
    RelativeLayout contentMyRingtones;
    @BindView(R.id.rvMyRingtones)
    RecyclerView rvMyRingtones;
    @BindView(R.id.tvNoRingtoneMsg)
    CustomTextView tvNoRingtoneMsg;
    @BindView(R.id.btnCreateRingtone)
    CustomButton btnCreateRingtone;

    RingtoneAdapter ringtoneAdapter;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rlAds;
    AdView adView;

    DBHandler dbHandler = new DBHandler(this);

    ArrayList<Ringtones> lstRingtones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ringtones);
        ButterKnife.bind(this);
        rlAds = (RelativeLayout) findViewById(R.id.rlAds);
        adView=(AdView)findViewById(R.id.ad_view);
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

        mInterstitialAd = new InterstitialAd(MyRingtonesActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.intestial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }
        });
        requestNewInterstitial();

        rvMyRingtones.setHasFixedSize(false);

        lstRingtones.clear();
        lstRingtones = dbHandler.getAllRingtones();
        size = lstRingtones.size();

        if (size == 0) {
            tvNoRingtoneMsg.setVisibility(View.VISIBLE);
            btnCreateRingtone.setVisibility(View.VISIBLE);
            rvMyRingtones.setVisibility(View.GONE);
        } else {
            layoutManager = new LinearLayoutManager(getApplicationContext());
            rvMyRingtones.setLayoutManager(layoutManager);

            ringtoneAdapter = new RingtoneAdapter(lstRingtones, this);
            rvMyRingtones.setAdapter(ringtoneAdapter);
        }

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @OnClick(R.id.btnCreateRingtone)
    public void onClick() {
        Intent createRingtone = new Intent(MyRingtonesActivity.this, RingtoneMakerActivity.class);
        startActivity(createRingtone);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {

        }
        finish();
    }
}
