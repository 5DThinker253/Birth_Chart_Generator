package com.ejs.birthchart.fragments;

import static com.ejs.birthchart.utils.constValues.const_debugAds;
import static com.ejs.birthchart.utils.constValues.const_debugApp;
import static com.ejs.birthchart.utils.firebaseUtils.ADDISMISSED;
import static com.ejs.birthchart.utils.firebaseUtils.fetchFrc;
import static com.ejs.birthchart.utils.firebaseUtils.initAdView;
import static com.ejs.birthchart.utils.firebaseUtils.initRemoteConfig;
import static com.ejs.birthchart.utils.firebaseUtils.loadInterstitial;
import static com.ejs.birthchart.utils.firebaseUtils.loadInterstitialRewarded;
import static com.ejs.birthchart.utils.firebaseUtils.loadVideoRewarded;
import static com.ejs.birthchart.utils.firebaseUtils.logEventAnalytics;
import static com.ejs.birthchart.utils.msg.log;
import static com.ejs.birthchart.utils.prefUtils.PREF_SETTINGS;
import static com.ejs.birthchart.utils.prefUtils.getPreferBool;
import static com.ejs.birthchart.utils.prefUtils.getPreferFloat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.adapters.moonAdapter;
import com.ejs.birthchart.data.dataMoonCal;
import com.ejs.birthchart.utils.utils;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kal.rackmonthpicker.RackMonthPicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class fragment_MoonCalendar extends Fragment {
    private final String tag = this.getClass().getSimpleName();
    private final Calendar cal = Calendar.getInstance();
    private RecyclerView recyclerView;
    private AppCompatActivity mCompat;
    private ArrayList<dataMoonCal> arrayMooncal;
    private TextView tv_month;
    private double dLat, dLon;
    private AdView mAdView;
    private boolean faselunaVIP = false;
    private FirebaseRemoteConfig frc;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;
    private boolean admobFRC = true;

    public fragment_MoonCalendar(AppCompatActivity mCompat, double dLat, double dLon) {
        this.mCompat = mCompat;
        this.dLat = dLat;
        this.dLon = dLon;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView!= null) mAdView.destroy();
    }
    @Override
    public void onResume(){
        super.onResume();
        fetchFrc(mCompat, frc, requestPermissionLauncher);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moon_calendar, container, false);
        tv_month = view.findViewById(R.id.tv_month);

        faselunaVIP = getPreferBool(mCompat, PREF_SETTINGS, "faselunaVIP");


        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(mCompat.getString(R.string.birthchart_generator));
        mCompat.setSupportActionBar(toolbar);
        ActionBar actionBar = mCompat.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        logEventAnalytics(mCompat, FirebaseAnalytics.Event.APP_OPEN, tag);
/*
        String path = mCompat.getExternalFilesDir(null).getAbsolutePath() + "/moonphase";
        File file = new File(path);
        if (!file.exists()){
            moonphaseDownload(mCompat);
        }*/

        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);

        LocalDate date = LocalDate.now();
        final int days = date.lengthOfMonth();
        final int months = date.getMonth().getValue();
        final int years = date.getYear();
        final int lengthOfMonth = date.lengthOfMonth();

        recyclerView = view.findViewById(R.id.rv_moonCal);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mCompat, LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mCompat, 1, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
        cal.set(Calendar.MONTH, months -1);
        String month_name = month_date.format(cal.getTime()) + " " + years;
        tv_month.setText(month_name);

        final RackMonthPicker rackMonthPicker = new RackMonthPicker(mCompat)
                /*.setMonthType(MonthType.NUMBER)*/
                .setPositiveButton((month, startDate, endDate, year, monthLabel) -> {
                    if (debugApp){
                        if (debugAds) {
                            int ranDom = new Random().nextInt(3);
                            switch (ranDom){
                                case 0:
                                    loadInterstitial(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            callMonth(month,startDate,endDate,year, dLat, dLon);
                                        }
                                    }, debugAds);
                                    break;
                                case 1:
                                    loadInterstitialRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            callMonth(month,startDate,endDate,year, dLat, dLon);
                                        }
                                    }, debugAds);
                                    break;
                                case 2:
                                    loadVideoRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            callMonth(month,startDate,endDate,year, dLat, dLon);
                                        }
                                    }, debugAds);
                                    break;
                            }
                        } else {
                            callMonth(month,startDate,endDate,year, dLat, dLon);
                        }
                    } else {
                        if (frc.getBoolean("admob")) {
                            int ranDom = new Random().nextInt(3);
                            switch (ranDom){
                                case 0:
                                    loadInterstitial(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            callMonth(month,startDate,endDate,year, dLat, dLon);
                                        }
                                    }, debugAds);
                                    break;
                                case 1:
                                    loadInterstitialRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            callMonth(month,startDate,endDate,year, dLat, dLon);
                                        }
                                    }, debugAds);
                                    break;
                                case 2:
                                    loadVideoRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            callMonth(month,startDate,endDate,year, dLat, dLon);
                                        }
                                    }, debugAds);
                                    break;
                            }
                        } else {
                            callMonth(month,startDate,endDate,year, dLat, dLon);
                        }
                    }
                }).setLocale(Locale.getDefault())
                .setNegativeButton(AppCompatDialog::dismiss);
        tv_month.setOnClickListener(v -> rackMonthPicker.show());
        arrayMooncal = fillAdapter(months, years, lengthOfMonth, dLat, dLon);
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        moonAdapter moonadapter = new moonAdapter(mCompat, arrayMooncal);
        recyclerView.setAdapter(moonadapter); // set the Adapter to RecyclerView
        return view;
    }

    private void callMonth(int month, int startDate, int endDate, int year, double dLat, double dLon){
        arrayMooncal.clear();

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        int monthnum=month;
        cal.set(Calendar.MONTH,monthnum-1);
        String month_name = month_date.format(cal.getTime());
        tv_month.setText(month_name + " " + year);
        arrayMooncal = fillAdapter(month, year, endDate, dLat, dLon);

        moonAdapter moonadapter = new moonAdapter(mCompat, arrayMooncal);
        recyclerView.setAdapter(moonadapter); // set the Adapter to RecyclerView
        moonadapter.notifyDataSetChanged();
    }

    private ArrayList<dataMoonCal> fillAdapter(int month, int year, int lengthOfMonth, double dLat, double dLon){

        ArrayList<dataMoonCal> arrayMooncal = new ArrayList<>();

        String timeZone = TimeZone.getDefault().getID();

        final double[] COLOGNE = new double[] { dLat, dLon };

        arrayMooncal = utils.getMoonDetails2(mCompat, arrayMooncal, COLOGNE, year, month, 1, lengthOfMonth);
        arrayMooncal = utils.getMoonDetails(mCompat, arrayMooncal,COLOGNE, year, month, 1, 0);
        arrayMooncal = utils.getMoonDetails(mCompat, arrayMooncal,COLOGNE, year, month, 1, 90);
        arrayMooncal = utils.getMoonDetails(mCompat, arrayMooncal,COLOGNE, year, month, 1, 180);
        arrayMooncal = utils.getMoonDetails(mCompat, arrayMooncal,COLOGNE, year, month, 1, 270);

        return arrayMooncal;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {

        } else {

        }
    });

}
