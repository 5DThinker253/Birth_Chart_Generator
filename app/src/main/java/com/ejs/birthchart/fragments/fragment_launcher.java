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
import static com.ejs.birthchart.utils.msg.log;
import static com.ejs.birthchart.utils.utils.checkPermissions;
import static com.ejs.birthchart.utils.utils.collapseView;
import static com.ejs.birthchart.utils.utils.expandView;
import static com.ejs.birthchart.utils.utils.getCoordinates;
import static com.ejs.birthchart.utils.utils.isdataValid;
import static com.ejs.birthchart.utils.utils.openfragment;
import static com.ejs.birthchart.utils.utils.permissionsLocation;
import static com.ejs.birthchart.utils.utils.showNotFoundDialog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.utils.MyLocation;
import com.ejs.birthchart.utils.utils;
import com.google.android.gms.ads.AdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class fragment_launcher extends Fragment {
    private final AppCompatActivity mCompat;
    private final int type;

    private LinearLayout gpsLayout, cityCountryLayout, manualLayout;
    private TextView cityCoordinates, coordinatesTextView, tv_title;
    private EditText timezoneEditText, nameSurnameEditText, dateOfBirthEditText, cityEditText, latitudeEditText, longitudeEditText;

    private RadioButton radioGps;
    private String cityName="", countryName="";
    private double latitude = -100, longitude = -200;
    private int selectedGeolocationMethod = 0;
    
    private FirebaseRemoteConfig frc;
    private AdView mAdView;


    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    public fragment_launcher(AppCompatActivity mCompat, int type) {
        this.mCompat = mCompat;
        this.type = type;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher, container, false);

        tv_title = view.findViewById(R.id.tv_title);
        Button btn_open = view.findViewById(R.id.btn_open);
        Button btn_getGPSLocation = view.findViewById(R.id.btn_getGPSLocation);
        Button btn_getCityLocation = view.findViewById(R.id.btn_getCityLocation);
        RadioGroup geolocationRadioGroup = view.findViewById(R.id.geolocationRadioGroup);
        cityCountryLayout = view.findViewById(R.id.cityCountryLayout);
        gpsLayout = view.findViewById(R.id.gpsLayout);
        manualLayout = view.findViewById(R.id.manualLayout);
        cityEditText = view.findViewById(R.id.cityEditText);
        cityCoordinates = view.findViewById(R.id.cityCoordinates);
        coordinatesTextView = view.findViewById(R.id.coordinatesTextView);
        latitudeEditText = view.findViewById(R.id.latitudeEditText);
        longitudeEditText = view.findViewById(R.id.longitudeEditText);

        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);

        Toolbar toolbar = mCompat.findViewById(R.id.toolbar);
        toolbar.setTitle(mCompat.getString(R.string.birthchart_generator));
        mCompat.setSupportActionBar(toolbar);
        ActionBar actionBar = mCompat.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        switch (type){
            case 1:
                tv_title.setText(mCompat.getString(R.string.eclipse_explorer));
                btn_open.setText(mCompat.getString(R.string.eclipse_explorer));
                break;
            case 2:
                tv_title.setText(mCompat.getString(R.string.moon_calendar));
                btn_open.setText(mCompat.getString(R.string.moon_calendar));
                break;
            case 3:
                tv_title.setText(mCompat.getString(R.string.moonphase_calculator));
                btn_open.setText(mCompat.getString(R.string.moonphase_calculator));
                break;
        }

        geolocationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> handleGeolocationSelection(checkedId));
        latitudeEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (latitudeEditText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
                    double Latitude = Double.parseDouble(latitudeEditText.getText().toString().trim());
                    if (Latitude >= -90 && Latitude <= 90) {
                        latitude = Latitude;
                    } else {
                        latitude = -100;
                        showNotFoundDialog(mCompat, getString(R.string.str_invalid_latitud_range));
                    }
                } else {
                    latitude = -100;
                    showNotFoundDialog(mCompat, getString(R.string.str_invalid_latitud_format));
                }
            }
        });
        longitudeEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (longitudeEditText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
                    double Longitude = Double.parseDouble(longitudeEditText.getText().toString().trim());
                    if (Longitude >= -180 && Longitude <= 180) {
                        longitude = Longitude;
                    } else {
                        longitude = -200;
                        showNotFoundDialog(mCompat, getString(R.string.str_invalid_longitud_range));
                    }
                } else {
                    longitude = -200;
                    showNotFoundDialog(mCompat, getString(R.string.str_invalid_longitud_format));
                }
            }
        });
        btn_getCityLocation.setOnClickListener(v -> {
            if (cityEditText != null && !cityEditText.equals("")) {
                // Obtener los valores ingresados por el usuario
                String cityCountry = cityEditText.getText().toString().trim();

                // Verificar si se ingresó una ciudad y país separados por coma
                if (cityCountry.contains(",")) {
                    // Separar la ciudad y el país
                    String[] parts = cityCountry.split(",");
                    cityName = parts[0].trim();
                    countryName = parts[1].trim();

                    // Utilizar el Geocoder para obtener las coordenadas
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    List<Address> addresses = getCoordinates(mCompat,cityName, countryName);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        String res = address.getLatitude() + "," + address.getLongitude();
                        cityCoordinates.setText(res);
                    } else {
                        cityCoordinates.setText(getString(R.string.location_unable_to_geocode));
                        showNotFoundDialog(mCompat, getString(R.string.location_unable_to_geocode));
                        Toast.makeText(mCompat, getString(R.string.location_unable_to_geocode), Toast.LENGTH_SHORT).show();
                        Log.e("astral", getString(R.string.location_unable_to_geocode));
                    }
                    assert addresses != null;
                    if (addresses.isEmpty()) {
                        String message = String.format(getString(R.string.location_not_found), cityName + "," +countryName);
                        showNotFoundDialog(mCompat, message);
                    }
                } else {
                    // No se ingresó una ciudad y país válidos
                    cityCoordinates.setText(getString(R.string.location_unable_to_geocode));
                }
            }
        });
        btn_getGPSLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(mCompat, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mCompat, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                    @Override
                    public void gotLocation(final Location location){
                        if (location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            String coord = latitude + ", " + longitude;
                            coordinatesTextView.setText(coord);
                        }
                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(mCompat, locationResult);
            } else {
                checkPermissions(mCompat, requestPermissionLauncher, permissionsLocation());
            }
        });

        btn_open.setOnClickListener(v12 -> {
            if (selectedGeolocationMethod > 0) {
                if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
                    if (!debugApp) {
                        if (frc.getBoolean("admob")) {
                            int ranDom = new Random().nextInt(3);
                            //ranDom = 2;
                            String res;
                            switch (ranDom){
                                case 0:
                                    loadInterstitial(mCompat, result -> {
                                        log("e","adstest", "callback " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            runCode();
                                        }
                                    }, debugAds);
                                    break;
                                case 1:
                                    loadInterstitialRewarded(mCompat, result -> {
                                        log("e","adstest", "callback " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            runCode();
                                        }
                                    }, debugAds);
                                    break;
                                case 2:
                                    loadVideoRewarded(mCompat, result -> {
                                        log("e","adstest", "callback " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            runCode();
                                        }
                                    }, debugAds);
                                    break;
                            }
                        } else {
                            runCode();
                        }
                    } else {
                        if (debugAds) {
                            int ranDom = new Random().nextInt(3);
                            //ranDom = 2;
                            String res;
                            switch (ranDom){
                                case 0:
                                    loadInterstitial(mCompat, result -> {
                                        log("e","adstest", "callback " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            runCode();
                                        }
                                    }, debugAds);
                                    break;
                                case 1:
                                    loadInterstitialRewarded(mCompat, result -> {
                                        log("e","adstest", "callback " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            runCode();
                                        }
                                    }, debugAds);
                                    break;
                                case 2:
                                    loadVideoRewarded(mCompat, result -> {
                                        log("e","adstest", "callback " + result);
                                        // Realiza las acciones necesarias en función del resultado obtenido
                                        if (result.equals(ADDISMISSED)) {
                                            runCode();
                                        }
                                    }, debugAds);
                                    break;
                            }
                        } else {
                            runCode();
                        }
                    }
                } else {
                    log("e","frc", "latitude " + latitude);
                    log("e","frc", "longitude " + longitude);
                    showNotFoundDialog(mCompat, getString(R.string.str_fields_required));
                }
            } else {
                log("e","frc", "latitude " + latitude);
                log("e","frc", "longitude " + longitude);
                showNotFoundDialog(mCompat, getString(R.string.str_fields_required));
            }
        });

        return view;
    }
    private void runCode() {
        switch (selectedGeolocationMethod) {
            case 0:
               log("e","frc", "latitude " + latitude);
               log("e","frc", "longitude " + longitude);
                showNotFoundDialog(mCompat, getString(R.string.str_fields_required));
                break;
            case 1:
            case 2:
            case 3:
                switch (type) {
                    case 1:
                        log("e","frc", "fragment_eclipses ");
                        log("e","frc", "latitude " + latitude);
                        log("e","frc", "longitude " + longitude);
                        openfragment(mCompat, new fragment_eclipses(mCompat, latitude, longitude), R.id.fcv_setting, "fragment_eclipses");
                        break;
                    case 2:
                        log("e","frc", "fragment_MoonCalendar ");
                        log("e","frc", "latitude " + latitude);
                        log("e","frc", "longitude " + longitude);
                        openfragment(mCompat, new fragment_MoonCalendar(mCompat, latitude, longitude), R.id.fcv_setting, "fragment_MoonCalendar");
                        break;
                    case 3:
                        log("e","frc", "fragment_moonphase ");
                        log("e","frc", "latitude " + latitude);
                        log("e","frc", "longitude " + longitude);
                        openfragment(mCompat, new fragment_moonphase(mCompat, latitude, longitude), R.id.fcv_setting, "fragment_moonphase");
                        break;
                }
                break;
        }
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {

        } else {

        }
    });

    private void handleGeolocationSelection(int checkedId) {
        if (checkedId == R.id.radioGps) {
            selectedGeolocationMethod = 1;
            expandView(mCompat, gpsLayout);
            collapseView(mCompat, cityCountryLayout);
            collapseView(mCompat, manualLayout);
        } else if (checkedId == R.id.radioCityCountry) {
            selectedGeolocationMethod = 2;
            collapseView(mCompat, gpsLayout);
            expandView(mCompat, cityCountryLayout);
            collapseView(mCompat, manualLayout);
        } else if (checkedId == R.id.radioManual) {
            selectedGeolocationMethod = 3;
            collapseView(mCompat, gpsLayout);
            collapseView(mCompat, cityCountryLayout);
            expandView(mCompat, manualLayout);
        }
    }
}
