package com.ejs.birthchart.fragments;

import static android.os.Build.VERSION.SDK_INT;

import static com.ejs.birthchart.utils.DateTimeUtils.dateTimeFormatter12;
import static com.ejs.birthchart.utils.constValues.const_debugAds;
import static com.ejs.birthchart.utils.constValues.const_debugApp;
import static com.ejs.birthchart.utils.firebaseUtils.ADDISMISSED;
import static com.ejs.birthchart.utils.firebaseUtils.checkUpdates;
import static com.ejs.birthchart.utils.firebaseUtils.fetchFrc;
import static com.ejs.birthchart.utils.firebaseUtils.initAdView;
import static com.ejs.birthchart.utils.firebaseUtils.initRemoteConfig;
import static com.ejs.birthchart.utils.firebaseUtils.loadInterstitial;
import static com.ejs.birthchart.utils.firebaseUtils.loadInterstitialRewarded;
import static com.ejs.birthchart.utils.firebaseUtils.loadVideoRewarded;
import static com.ejs.birthchart.utils.msg.log;
import static com.ejs.birthchart.utils.notifications.alertUpdate;
import static com.ejs.birthchart.utils.notifications.notificationUpdate;
import static com.ejs.birthchart.utils.prefUtils.PREF_UPDATE;
import static com.ejs.birthchart.utils.prefUtils.getPreferBool;
import static com.ejs.birthchart.utils.prefUtils.getPreferInt;
import static com.ejs.birthchart.utils.prefUtils.getPreferString;
import static com.ejs.birthchart.utils.prefUtils.savePreferBool;
import static com.ejs.birthchart.utils.prefUtils.savePreferInt;
import static com.ejs.birthchart.utils.prefUtils.savePreferString;
import static com.ejs.birthchart.utils.utils.checkPermissions;
import static com.ejs.birthchart.utils.utils.permissionsPost;
import static com.ejs.birthchart.utils.utils.permissionsStorage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.astrology.builder.CuspBuilder;
import com.ejs.birthchart.astrology.builder.PlanetBuilder;
import com.ejs.birthchart.astrology.domain.Cusp;
import com.ejs.birthchart.astrology.domain.Planet;
import com.ejs.birthchart.interfaces.JavaScriptInterface;
import com.ejs.birthchart.utils.utils;
import com.google.android.gms.ads.AdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class fragment_chart extends Fragment {
    private final AppCompatActivity mCompat;
    private static final int REQUEST_PERMISSION = 1;

    private final LocalDateTime event, event2;
    private final int type;
    private final String houseSystem, name, zodiac, tmz;
    private final double[] location;

    private FirebaseRemoteConfig frc;
    private AdView mAdView;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    public fragment_chart(AppCompatActivity mCompat, int type, LocalDateTime event, LocalDateTime event2, String houseSystem, String zodiac, String name, double[] location, String tmz) {
        this.mCompat = mCompat;
        this.type = type;
        this.event = event;
        this.event2 = event2;
        this.houseSystem = houseSystem;
        this.zodiac = zodiac;
        this.name = name;
        this.location = location;
        this.tmz = tmz;
    }
    /*public fragment_chart(AppCompatActivity mCompat, LocalDateTime event, String houseSystem, String zodiac, String name, double[] location, String tmz) {
        this.mCompat = mCompat;
        this.event = event;
        this.houseSystem = houseSystem;
        this.zodiac = zodiac;
        this.name = name;
        this.location = location;
        this.tmz = tmz;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        WebView wview = view.findViewById(R.id.wv);

        checkPermissions(mCompat, requestPermissionLauncher, permissionsStorage());
        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());
        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);

        Planet planetEphemeris = null;
        Cusp cuspEphemeris = null;
        Planet planetEphemeris2 = null;
        Cusp cuspEphemeris2 = null;

        planetEphemeris = new PlanetBuilder(event).planets().topo(location[0], location[1], location[2]).build();
        if (zodiac.equals("Tropical")){
            cuspEphemeris = new CuspBuilder(event).houses(houseSystem).topo(location[0], location[1], location[2]).build();
        } else {
            cuspEphemeris = new CuspBuilder(event).houses(houseSystem).topo(location[0], location[1], location[2]).zodiac(zodiac).build();
        }
        if (type == 2) {
            planetEphemeris2 = new PlanetBuilder(event2).planets().topo(location[0], location[1], location[2]).build();
            if (zodiac.equals("Tropical")){
                cuspEphemeris2 = new CuspBuilder(event2).houses(houseSystem).topo(location[0], location[1], location[2]).build();
            } else {
                cuspEphemeris2 = new CuspBuilder(event2).houses(houseSystem).topo(location[0], location[1], location[2]).zodiac(zodiac).build();
            }
        }
        /*cuspEphemeris = new CuspBuilder(event)
                .houses(houseSystem).topo(location[0], location[1], location[2]).build();*/

        try {
            // Crear un objeto JSONObject a partir del JSON
            JSONObject jsonObject = null;
            JSONObject planetsObject = null;
            JSONObject jsonObject1 = null;
            JSONArray cuspsObject = null;

            JSONObject jsonObject2 = null;
            JSONObject planetsObject2 = null;
            JSONObject jsonObject12 = null;
            JSONArray cuspsObject2 = null;

            jsonObject = new JSONObject(planetEphemeris.toJSON());
            planetsObject = jsonObject.getJSONObject("planets");
            jsonObject1 = new JSONObject(cuspEphemeris.toJSON());
            cuspsObject = jsonObject1.getJSONArray("cusps");
            log("e", "data",  " planetsObject:" + planetsObject +  "\n") ;
            log("e", "data",  " cuspsObject:" + cuspsObject +  "\n") ;
            if (type == 1) {
                jsonObject2 = new JSONObject(planetEphemeris.toJSON());
                planetsObject2 = jsonObject2.getJSONObject("planets");
                jsonObject12 = new JSONObject(cuspEphemeris.toJSON());
                cuspsObject2 = jsonObject12.getJSONArray("cusps");
                log("e", "data",  " planetsObject:" + planetsObject2 +  "\n") ;
                log("e", "data",  " cuspsObject:" + cuspsObject2 +  "\n") ;
            }
            if (type == 2) {
                jsonObject2 = new JSONObject(planetEphemeris2.toJSON());
                planetsObject2 = jsonObject2.getJSONObject("planets");
                jsonObject12 = new JSONObject(cuspEphemeris2.toJSON());
                cuspsObject2 = jsonObject12.getJSONArray("cusps");
                log("e", "data",  " planetsObject2:" + planetsObject2 +  "\n") ;
                log("e", "data",  " cuspsObject2:" + cuspsObject2 +  "\n") ;

            }
            log("e", "data",  " type:" + type +  "\n") ;

            String javascriptCode = "date.innerText = `Date: "+ event.atZone(ZoneOffset.UTC).format(dateTimeFormatter12) + " UTC `;" +
                    "Latitude.innerText = `Latitude: "+ location[1] +"`;" +
                    "Longitude.innerText = `Longitude: "+ location[0] +"`;" +
                    "name.innerText = `Name: "+ name +"`;" +
                    "house.innerText = `House System: "+ houseSystem +"`;" +
                    "mode.innerText = `Mode: "+ zodiac +"`;" +
                    "var data = {\"planets\":" + planetsObject.toString() + "," +
                    "\"cusps\":" + cuspsObject.toString() + "};" +
                    "const chart = new astrology.Chart('paper', 600, 600, {MARGIN:100, SYMBOL_SCALE:0.8}).radix(data);"+
                    "chart.addPointsOfInterest( {\"As\":[data.cusps[0]],\"Ic\":[data.cusps[3]],\"Ds\":[data.cusps[6]],\"Mc\":[data.cusps[9]]});\n" +

                    "var data2 = {};" +
                    "chart.aspects();"+
                    "var type = " + type +";"+
                    "if (type == 2) {" +
                    "data2 = {\"planets\":" + planetsObject2.toString() + "," +
                    "\"cusps\":" + cuspsObject2.toString() + "};" +
                    "chart.aspects();\n" +
                    "var transit = chart.transit( data2 );\n" +
                    "transit.aspects();"+
                    "}" +
                    "const aspectCalculator = new astrology.AspectCalculator(" + planetsObject + ", astrology.ASPECTS);"+
                    "const radialAspects = aspectCalculator.radix(" + planetsObject + ");\n" +
                    "const planetasOrdenados = ordenarPlanetas(" + planetsObject + ", orderPlanet);"+
                    "generarTablaAspectos(newAspects(radialAspects, orderPlanet), orderPlanet, ta);"+
                    "generarTablaAspectosOrbes(tao, newAspects(radialAspects, orderPlanet));\n" +
                    "generarTablaPlanetas(planetasOrdenados, tp);\n" +
                    "generarTablaAspectosInfo(tai);" ;
            String htmlUrl = "file:///android_asset/astrochart.html";
            wview.getSettings().setSafeBrowsingEnabled(true);
            wview.getSettings().setGeolocationEnabled(false);
            wview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            wview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            wview.getSettings().setAllowFileAccess(true);
            wview.getSettings().setBuiltInZoomControls(true);
            wview.getSettings().setSupportZoom(true);
            wview.getSettings().setUseWideViewPort(true);
            wview.getSettings().setLoadWithOverviewMode(true);
            wview.getSettings().setMinimumFontSize(1);
            wview.getSettings().setJavaScriptEnabled(true);
            wview.setWebChromeClient(new WebChromeClient());
            wview.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.loadUrl("javascript:"+javascriptCode);
                }
            });
            // Agregar un JavaScriptInterface para recibir llamadas desde el código JavaScript
            wview.addJavascriptInterface(new JavaScriptInterface(mCompat, frc), "AndroidInterface");
            wview.loadUrl(htmlUrl);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchFrc(mCompat, frc, requestPermissionLauncher);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView!= null) mAdView.destroy();
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            if (SDK_INT >= 33) {
                for (int i=0; i < permissions().length; i++) {
                    /*if (shouldShowRequestPermissionRationale(permissions()[i])) {
                        showPermissionRationale(permissions()[i]);
                    } else {
                        showSettingDialog();
                    }*/
                }
            }
        }
    });
    private void showPermissionRationale(Context context, String title, String msg, String permission) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (SDK_INT >= 33) {
                        requestPermissionLauncher.launch(permission);
                    } else {
                        requestPermissionLauncher.launch(permission);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public static String[] permissions() {
        String[] p;
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = new String[]{
                    android.Manifest.permission.READ_MEDIA_IMAGES/*,
                    android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.READ_MEDIA_VIDEO*/
            };
        } else {
            p = new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
        return p;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(mCompat, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {

                    try {
                        Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                        startActivity(intent);
                    } catch (Exception ex){
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                    }
                }*/
                /*ActivityCompat.requestPermissions(MainActivity.this, permissions(), 1);*/
                // Permiso denegado
                Toast.makeText(mCompat, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}