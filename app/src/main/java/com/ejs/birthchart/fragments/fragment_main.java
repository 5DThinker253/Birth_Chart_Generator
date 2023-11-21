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
import static com.ejs.birthchart.utils.firebaseUtils.logEventAnalytics;
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
import static com.ejs.birthchart.utils.utils.getTimeFromTimeZone;
import static com.ejs.birthchart.utils.utils.openfragment;
import static com.ejs.birthchart.utils.utils.permissionsPost;
import static com.ejs.birthchart.utils.utils.permissionsStorage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.classes.NumberPickerDialog;
import com.ejs.birthchart.classes.welcomeDialogFragment;
import com.ejs.birthchart.utils.utils;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class fragment_main extends Fragment {
    private final AppCompatActivity mCompat;
    private static final int REQUEST_LOCATION_PERMISSION = 4001;
    private static final int REQUEST_PERMISSION = 1;

    private ConsentInformation consentInformation;
    private ConsentForm consentform;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    private FirebaseRemoteConfig frc;
    private AdView mAdView;

    public fragment_main(AppCompatActivity mCompat) {
        super();
        this.mCompat = mCompat;
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchFrc(mCompat, frc, requestPermissionLauncher);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompat.finish();
        if (mAdView!= null) mAdView.destroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(mCompat.getTitle());
        mCompat.setSupportActionBar(toolbar);
        ActionBar actionBar = mCompat.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }


        CardView btn_more = view.findViewById(R.id.btn_more);
        CardView btn_support = view.findViewById(R.id.btn_support);
        CardView btn_share = view.findViewById(R.id.btn_share);
        CardView btn_eclipses = view.findViewById(R.id.btn_eclipses);
        CardView btn_moonPhase = view.findViewById(R.id.btn_moonPhase);
        CardView btn_moonCal = view.findViewById(R.id.btn_moonCal);
        CardView btn_birthchart = view.findViewById(R.id.btn_birthchart);

        checkPermissions(mCompat, requestPermissionLauncher, permissionsStorage());
        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());

        MobileAds.initialize(mCompat, initializationStatus -> {
            Log.e("data", "MobileAds initializationStatus " + initializationStatus.toString());
        });

        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);

        // Set tag for under age of consent. false means users are not under
        // age.
        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false).build();

        ConsentDebugSettings consentDebugSettings = new ConsentDebugSettings.Builder(mCompat)
                .addTestDeviceHashedId("73FE9E17FE5C9726AA99DA7009AFA421").build();

        consentInformation = UserMessagingPlatform.getConsentInformation(mCompat);
        consentInformation.requestConsentInfoUpdate( mCompat, params, () -> {
                    // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    if (consentInformation.isConsentFormAvailable()) {
                        loadForm();
                    }
                },
                formError -> {
                    // Handle the error.
                    // Handle the error.
                });

        btn_moonPhase.setOnClickListener(v1 -> {
            openfragment(mCompat, new fragment_launcher(mCompat, 3), R.id.fcv_setting, "fragment_launcher");
        });
        btn_birthchart.setOnClickListener(v1 -> {
            openfragment(mCompat, new fragment_input(mCompat), R.id.fcv_setting, "fragment_input");

        });
        btn_eclipses.setOnClickListener(v12 -> {
            openfragment(mCompat, new fragment_launcher(mCompat, 1), R.id.fcv_setting, "fragment_launcher");

        });
        btn_moonCal.setOnClickListener(v13 -> {
            openfragment(mCompat, new fragment_launcher(mCompat, 2), R.id.fcv_setting, "fragment_launcher");

        });

        return view;
    }
    public void loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(mCompat, consentForm -> {
            consentform = consentForm;
            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                consentform.show(mCompat, formError -> {
                                    if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                        // App can start requesting ads.
                                    }

                                    // Handle dismissal by reloading form.
                                    loadForm();
                                });
                    }
                }, formError -> {
                    // Handle Error.
                }
        );
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {

        } else {

        }
    });

}
