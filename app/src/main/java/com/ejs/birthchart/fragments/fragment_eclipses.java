package com.ejs.birthchart.fragments;

import static com.ejs.birthchart.utils.DateTimeUtils.convertUTCtoLocalTime;
import static com.ejs.birthchart.utils.DateTimeUtils.dateTimeFormatter12;
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
import static com.ejs.birthchart.utils.prefUtils.PREF_ECLIPSE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.adapters.eclipseGlobalAdapter;
import com.ejs.birthchart.adapters.eclipseLocalAdapter;
import com.ejs.birthchart.adapters.eclipseLunarAdapter;
import com.ejs.birthchart.data.dataEclipse;
import com.ejs.birthchart.utils.prefUtils;
import com.ejs.birthchart.utils.utils;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import io.github.cosinekitty.astronomy.Astronomy;
import io.github.cosinekitty.astronomy.DateTime;
import io.github.cosinekitty.astronomy.EclipseKind;
import io.github.cosinekitty.astronomy.GlobalSolarEclipseInfo;
import io.github.cosinekitty.astronomy.LocalSolarEclipseInfo;
import io.github.cosinekitty.astronomy.LunarEclipseInfo;
import io.github.cosinekitty.astronomy.Observer;
import io.github.cosinekitty.astronomy.Time;

public class fragment_eclipses extends Fragment {
    private final String tag = this.getClass().getSimpleName();
    private final AppCompatActivity mCompat;
    ArrayList<dataEclipse> globalSolar = new ArrayList<>(0), localSolar = new ArrayList<>(0), lunarEclipses = new ArrayList<>(0);
    private EditText year_solar_eclipse;
    private TextView next_eclipse, next_type_eclipse, next_latitude_eclipse, next_longitude_eclipse;
    private Button btn_eclipse_search;
    private RecyclerView global_solar_eclipses_list, local_solar_eclipses_list, lunar_eclipses_list;
    private boolean faselunaVIP = false;
    private FirebaseRemoteConfig frc;
    private AdView mAdView;
    private double lat, lon;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    public fragment_eclipses(AppCompatActivity mCompat, double lat, double lon) {
        this.mCompat = mCompat;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchFrc(mCompat, frc, requestPermissionLauncher);

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {

        } else {

        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView!= null) mAdView.destroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_eclipse, container, false);
        /*ProgressAlert progress;
        progress = ProgressAlert.getInstance(mCompat, true);
        progress.setMessage("Please Wait!!");
        progress.setCancelable(false);
        progress.show();*/

        mCompat.setTitle(mCompat.getString(R.string.str_eclipse));
        next_eclipse = view.findViewById(R.id.next_eclipse);
        next_type_eclipse = view.findViewById(R.id.next_type_eclipse);
        next_latitude_eclipse = view.findViewById(R.id.next_latitude_eclipse);
        next_longitude_eclipse = view.findViewById(R.id.next_longitude_eclipse);
        year_solar_eclipse = view.findViewById(R.id.year_solar_eclipse);
        global_solar_eclipses_list = view.findViewById(R.id.global_solar_eclipses_list);
        local_solar_eclipses_list = view.findViewById(R.id.local_solar_eclipses_list);
        lunar_eclipses_list = view.findViewById(R.id.lunar_eclipses_list);
        btn_eclipse_search = view.findViewById(R.id.btn_eclipse_search);

        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);

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
        faselunaVIP = prefUtils.getPreferBool(mCompat, PREF_ECLIPSE, "faselunaVIP");

        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTime dt = new DateTime(zonedDateTime.getYear(),zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),zonedDateTime.getHour(),zonedDateTime.getMinute(),zonedDateTime.getSecond());
        Time nowTime = Time.fromMillisecondsSince1970(dt.toTime().toMillisecondsSince1970());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.getDefault());
        GlobalSolarEclipseInfo gsei = Astronomy.searchGlobalSolarEclipse(nowTime);

        ZonedDateTime savedEclipseDate;
        if (prefUtils.getPreferString(mCompat, PREF_ECLIPSE, "eclipseDate") == null){
            savedEclipseDate = zonedDateTime.minusDays(1);
        } else if (prefUtils.getPreferString(mCompat, PREF_ECLIPSE, "eclipseDate").equals("")){
            savedEclipseDate = zonedDateTime.minusDays(1);
        } else {
            String eDate = prefUtils.getPreferString(mCompat, PREF_ECLIPSE, "eclipseDate");
            if (!eDate.equals("")) {
                savedEclipseDate = ZonedDateTime.parse(prefUtils.getPreferString(mCompat, PREF_ECLIPSE, "eclipseDate"));
            } else {
                savedEclipseDate = ZonedDateTime.now();
            }

        }
        String eclipseLat = String.format(Locale.getDefault(), "%.4f",gsei.getLatitude());
        String eclipseLon = String.format(Locale.getDefault(), "%.4f",gsei.getLongitude());
        ZonedDateTime zdts = ZonedDateTime.parse(gsei.getPeak().toDateTime().toString());
        String date = convertUTCtoLocalTime(gsei.getPeak().toDateTime().toString(), dateTimeFormatter12);
        //next_eclipse.setText(zdts.format(dateTimeFormatter));
        next_eclipse.setText(" " + date);
        next_type_eclipse.setText(" " + gsei.getKind().name());
        next_latitude_eclipse.setText(" " + eclipseLat);
        next_longitude_eclipse.setText(" " + eclipseLon);
        year_solar_eclipse.setText(String.valueOf(zonedDateTime.getYear()));
        if (zonedDateTime.isAfter(savedEclipseDate)) {
            prefUtils.savePreferString(mCompat, PREF_ECLIPSE,"eclipseType", "Solar");
            prefUtils.savePreferString(mCompat, PREF_ECLIPSE,"eclipseDate", gsei.getPeak().toDateTime().toString());
            prefUtils.savePreferString(mCompat, PREF_ECLIPSE,"eclipseKind", gsei.getKind().name());
            prefUtils.savePreferString(mCompat, PREF_ECLIPSE,"eclipseLat", eclipseLat);
            prefUtils.savePreferString(mCompat, PREF_ECLIPSE,"eclipseLon", eclipseLon);
            prefUtils.savePreferString(mCompat, PREF_ECLIPSE,"eclipseMagnitude", (int)(gsei.getObscuration() * 100) + "%");
        }

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mCompat, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mCompat, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(mCompat, LinearLayoutManager.VERTICAL, false);
        global_solar_eclipses_list.setLayoutManager(mLayoutManager1);
        local_solar_eclipses_list.setLayoutManager(mLayoutManager2);
        lunar_eclipses_list.setLayoutManager(mLayoutManager3);


        logEventAnalytics(mCompat, FirebaseAnalytics.Event.APP_OPEN, tag);

        searchEclipse(zonedDateTime.getYear(), lat, lon);
        btn_eclipse_search.setOnClickListener(v -> {
            if (year_solar_eclipse.length() > 0) {
                if (TextUtils.isDigitsOnly(year_solar_eclipse.getText())) {
                    if (debugApp){
                        if (debugAds) {
                            int ranDom = new Random().nextInt(3);
                            int selYear = Integer.parseInt(year_solar_eclipse.getText().toString());
                            switch (ranDom){
                                case 0:
                                    loadInterstitial(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            searchEclipse(selYear, lat, lon);
                                        }
                                    }, debugAds);
                                    break;
                                case 1:
                                    loadInterstitialRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            searchEclipse(selYear, lat, lon);
                                        }
                                    }, debugAds);
                                    break;
                                case 2:
                                    loadVideoRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            searchEclipse(selYear, lat, lon);
                                        }
                                    }, debugAds);
                                    break;
                            }
                        } else {
                            int selYear = Integer.parseInt(year_solar_eclipse.getText().toString());
                            searchEclipse(selYear, lat, lon);
                        }
                    } else {
                        if (frc.getBoolean("admob")) {
                            int ranDom = new Random().nextInt(3);
                            int selYear = Integer.parseInt(year_solar_eclipse.getText().toString());
                            switch (ranDom){
                                case 0:
                                    loadInterstitial(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            searchEclipse(selYear, lat, lon);
                                        }
                                    }, debugAds);
                                    break;
                                case 1:
                                    loadInterstitialRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            searchEclipse(selYear, lat, lon);
                                        }
                                    }, debugAds);
                                    break;
                                case 2:
                                    loadVideoRewarded(mCompat, result -> {
                                        log("e","adstest", "loadInterstitial " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            searchEclipse(selYear, lat, lon);
                                        }
                                    }, debugAds);
                                    break;
                            }
                        } else {
                            int selYear = Integer.parseInt(year_solar_eclipse.getText().toString());
                            searchEclipse(selYear, lat, lon);
                        }
                    }
                }
            }
        });
        return view;
    }
    private void searchEclipse(int year, double lat, double lon) {


        globalSolar = new ArrayList<>(0);
        localSolar = new ArrayList<>(0);
        lunarEclipses = new ArrayList<>(0);

        globalSolar.clear();
        localSolar.clear();
        lunarEclipses.clear();

        /*String strlat = getPreferString(mCompat, "latitud");
        String strlon = getPreferString(mCompat, "longitud");
        if (TextUtils.isDigitsOnly(strlat)) lat = Double.parseDouble(strlat);
        if (TextUtils.isDigitsOnly(strlon)) lon = Double.parseDouble(strlon);*/

        DateTime dt = new DateTime(year,1,1,1,0,0);
        Time GlobalTime = Time.fromMillisecondsSince1970(dt.toTime().toMillisecondsSince1970());
        Time LocalTime = Time.fromMillisecondsSince1970(dt.toTime().toMillisecondsSince1970());
        Time LunarTime = Time.fromMillisecondsSince1970(dt.toTime().toMillisecondsSince1970());

        GlobalSolarEclipseInfo gsei = Astronomy.searchGlobalSolarEclipse(GlobalTime);
        while (gsei.getPeak().toDateTime().getYear() == year) {
            dataEclipse dataGlobal = new dataEclipse();
            if (gsei.getKind().name().equals(EclipseKind.Total.name())){
                dataGlobal.setImage(R.drawable.eclipse_total);
            }
            if (gsei.getKind().name().equals(EclipseKind.Annular.name())){
                dataGlobal.setImage(R.drawable.eclipse_anular);
            }
            if (gsei.getKind().name().equals(EclipseKind.Partial.name())){
                dataGlobal.setImage(R.drawable.eclipse_parcial);
            }
            dataGlobal.setPeak(gsei.getPeak());
            dataGlobal.setKind(gsei.getKind());
            dataGlobal.setObscuration(gsei.getObscuration());
            dataGlobal.setILatitude(gsei.getLatitude());
            dataGlobal.setLongitude(gsei.getLongitude());
            dataGlobal.setDistance(gsei.getDistance());
            globalSolar.add(dataGlobal);
            gsei = Astronomy.nextGlobalSolarEclipse(gsei.getPeak());
        }

        if (lat == -1 || lon == -1){
            localSolar.clear();
        } else {
            Observer observer = new Observer(lat, lon,0.0);
            LocalSolarEclipseInfo lsei = Astronomy.searchLocalSolarEclipse(LocalTime, observer);
            while (lsei.getPeak().getTime().toDateTime().getYear() == year) {
                dataEclipse dataLocal = new dataEclipse();
                if (lsei.getKind().name().equals(EclipseKind.Total.name())){
                    dataLocal.setImage(R.drawable.eclipse_total);
                }
                if (lsei.getKind().name().equals(EclipseKind.Annular.name())){
                    dataLocal.setImage(R.drawable.eclipse_anular);
                }
                if (lsei.getKind().name().equals(EclipseKind.Partial.name())){
                    dataLocal.setImage(R.drawable.eclipse_parcial);
                }
                dataLocal.setPeak(lsei.getPeak().getTime());
                dataLocal.setKind(lsei.getKind());
                dataLocal.setObscuration(lsei.getObscuration());

                if (lsei.getKind().name().equals(EclipseKind.Partial.name())){
                    dataLocal.setPartialBegin(lsei.getPartialBegin());
                    dataLocal.setPartialEnd(lsei.getPartialEnd());
                }
                if (lsei.getKind().name().equals(EclipseKind.Total.name())){
                    dataLocal.setTotalBegin(lsei.getTotalBegin());
                    dataLocal.setTotalEnd(lsei.getTotalEnd());
                }
                localSolar.add(dataLocal);
                lsei = Astronomy.nextLocalSolarEclipse(lsei.getPeak().getTime(), observer);
            }
        }

        LunarEclipseInfo lei = Astronomy.searchLunarEclipse(LunarTime);
        while (lei.getPeak().toDateTime().getYear() == year) {
            dataEclipse dataLunar = new dataEclipse();
            if (lei.getKind().name().equals(EclipseKind.Total.name())){
                dataLunar.setImage(R.drawable.eclipse_lunar_total);
            }
            if (lei.getKind().name().equals(EclipseKind.Partial.name())){
                dataLunar.setImage(R.drawable.eclipse_lunar_parcial);
            }
            if (lei.getKind().name().equals(EclipseKind.Penumbral.name())){
                dataLunar.setImage(R.drawable.eclipse_lunar_penumbral);
            }
            dataLunar.setPeak(lei.getPeak());
            dataLunar.setKind(lei.getKind());
            dataLunar.setObscuration(lei.getObscuration());
            dataLunar.setSdPartial(lei.getSdPartial());
            dataLunar.setSdPenum(lei.getSdPenum());
            dataLunar.setSdTotal(lei.getSdTotal());
            lunarEclipses.add(dataLunar);
            /*if (!lei.getKind().name().equals(EclipseKind.Penumbral.name())){
            }*/
            lei = Astronomy.nextLunarEclipse(lei.getPeak());
        }

        eclipseGlobalAdapter globalAdapter = new eclipseGlobalAdapter(mCompat, globalSolar);
        eclipseLocalAdapter localAdapter = new eclipseLocalAdapter(mCompat, localSolar);
        eclipseLunarAdapter lunarAdapter = new eclipseLunarAdapter(mCompat, lunarEclipses);
        global_solar_eclipses_list.setAdapter(globalAdapter);
        local_solar_eclipses_list.setAdapter(localAdapter);
        lunar_eclipses_list.setAdapter(lunarAdapter);
    }
}
