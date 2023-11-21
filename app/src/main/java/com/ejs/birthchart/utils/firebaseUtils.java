package com.ejs.birthchart.utils;

import static com.ejs.birthchart.utils.DateTimeUtils.dateTimeFormatter12;
import static com.ejs.birthchart.utils.msg.log;
import static com.ejs.birthchart.utils.msg.toast;
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

import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.classes.ProgressAlert;
import com.ejs.birthchart.interfaces.adsCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class firebaseUtils {
    private static boolean postSend = false;
    public static final String ADCLICKED = "onAdClicked";
    public static final String ADIMPRESSION = "onAdImpression";
    public static final String ADSHOWEDFULL = "onAdShowedFullScreenContent";
    public static final String ADDISMISSED = "onAdDismissedFullScreenContent";
    public static final String ADDFAILTOSHOW = "onAdFailedToShowFullScreenContent";
    public static final String ADFAILTOLOAD = "onAdFailedToLoad";

    public static FirebaseAnalytics initAnalytics(AppCompatActivity mCompat) {
       return FirebaseAnalytics.getInstance(mCompat);
    }
    public static void logEventAnalytics(AppCompatActivity mCompat, String name, String value) {
        Bundle bundle14 = new Bundle();
        bundle14.putString(name, (String) value);
        initAnalytics(mCompat).logEvent(name, bundle14);
    }

    public static FirebaseRemoteConfig initRemoteConfig(AppCompatActivity mCompat) {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put("admob", true);
        remoteConfigDefaults.put("newUpdate", 1);
        remoteConfigDefaults.put("test", 1);
        //...any other defaults here

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1900)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String HourStart = getPreferString(mCompat, PREF_UPDATE, "HourStart");
                if (HourStart == null || HourStart.equals("")) {
                    savePreferString(mCompat, PREF_UPDATE, "HourStart", ZonedDateTime.now().toString());
                }
                int versionCode = BuildConfig.VERSION_CODE;
                int newVer = (int) firebaseRemoteConfig.getLong("newUpdate");
                boolean mandatoryUpdate = firebaseRemoteConfig.getBoolean("mandatoryUpdate");
                savePreferBool(mCompat, PREF_UPDATE, "updateMandatory", mandatoryUpdate);
                savePreferInt(mCompat, PREF_UPDATE, "updateVersion", newVer);

                log("e", "frc", "version " + versionCode);
                log("e", "frc", "newVer: " + newVer);
                log("e", "frc", "mandatoryUpdate: " + mandatoryUpdate);
                log("e", "frc", "admob: " + firebaseRemoteConfig.getBoolean("admob"));
                log("e", "frc", ZonedDateTime.now().format(dateTimeFormatter12) + " test: " + firebaseRemoteConfig.getLong("newUpdate"));
                //toast(context,"e", "Fetch and activate succeeded");

            } else {
                log("e", "frc", "Fetch failed");
                //toast(context,"e", "Fetch failed");
            }
        });

        return firebaseRemoteConfig;
    }
    public static void fetchFrc(AppCompatActivity mCompat, FirebaseRemoteConfig frc, ActivityResultLauncher<String> requestPermissionLauncher) {
        frc.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String HourStart = getPreferString(mCompat, PREF_UPDATE, "HourStart");
                if (HourStart == null || HourStart.equals("")) {
                    savePreferString(mCompat, PREF_UPDATE, "HourStart", ZonedDateTime.now().toString());
                }
                int versionCode = BuildConfig.VERSION_CODE;
                int newVer = (int) frc.getLong("newUpdate");
                boolean mandatoryUpdate = frc.getBoolean("mandatoryUpdate");
                savePreferBool(mCompat, PREF_UPDATE, "updateMandatory", mandatoryUpdate);
                savePreferInt(mCompat, PREF_UPDATE, "updateVersion", newVer);

                log("e", "frc", "version " + versionCode);
                log("e", "frc", "newVer: " + newVer);
                log("e", "frc", "mandatoryUpdate: " + mandatoryUpdate);
                log("e", "frc", "admob: " + frc.getBoolean("admob"));
                log("e", "frc", ZonedDateTime.now().format(dateTimeFormatter12) + " test: " + frc.getLong("newUpdate"));
                //toast(context,"e", "Fetch and activate succeeded");
                checkUpdates(mCompat, requestPermissionLauncher);
            } else {
                log("e", "frc", "Fetch failed");
                checkUpdates(mCompat, requestPermissionLauncher);
                //toast(context,"e", "Fetch failed");
            }
            checkUpdates(mCompat, requestPermissionLauncher);
        });
    }
    public static void checkUpdates(AppCompatActivity mCompat, ActivityResultLauncher<String> requestPermissionLauncher) {
        int realVersion = BuildConfig.VERSION_CODE;
        int updateVersion = getPreferInt(mCompat, PREF_UPDATE, "updateVersion");
        boolean updateMandatory = getPreferBool(mCompat, PREF_UPDATE, "updateMandatory");
        if (updateVersion > realVersion) {
            if (!postSend) {
                if (updateMandatory) {
                    alertUpdate(mCompat, mCompat.getString(R.string.str_mandatory_update), mCompat.getString(R.string.update_mandatory_content),
                            true).show();
                    if (requestPermissionLauncher != null) {
                        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());
                        notificationUpdate(mCompat, mCompat.getString(R.string.str_mandatory_update),
                                mCompat.getString(R.string.update_mandatory_content));
                    }
                } else {
                    alertUpdate(mCompat, mCompat.getString(R.string.str_normal_update), mCompat.getString(R.string.update_normal_content),
                            false).show();
                    if (requestPermissionLauncher != null) {
                        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());
                        notificationUpdate(mCompat, mCompat.getString(R.string.str_normal_update),
                                mCompat.getString(R.string.update_normal_content));
                    }
                }
                postSend = true;
            }
        }
    }

    public static void initAdView (AppCompatActivity mCompat, AdView mAdView) {

        mAdView.setVisibility(View.VISIBLE);
        MobileAds.initialize(mCompat, initializationStatus ->
                log("e", "ads","initializationStatus " ));
        AdRequest adRequest = new AdRequest.Builder().build();

        /*if (BuildConfig.DEBUG) {
            mAdView.setAdUnitId("/6499/example/banner");
        }*/
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
                log("e", "ads","adError " + adError);
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
        mAdView.loadAd(adRequest);
    }

    public static OnUserEarnedRewardListener onUserEarnedRewardListener(){
        return rewardItem -> {
            // Handle the reward.
            int rewardAmount = rewardItem.getAmount();
            String rewardType = rewardItem.getType();
            log("e","firebaseUtils", "onUserEarnedRewardListener");
        };
    }
    public static void loadVideoRewarded(AppCompatActivity mCompat, adsCallback callback, boolean debugApp) {
        ProgressAlert progress;
        progress = ProgressAlert.getInstance(mCompat, true);
        progress.setMessage("Please Wait!!");
        progress.setCancelable(false);
        progress.show();
        //String key = mCompat.getString(R.string.ad_Videos_recompensado_release);
        String key = mCompat.getString(R.string.ad_Videos_recompensado_release);
        if (debugApp) {
            //It's not a release version.
            key = mCompat.getString(R.string.ad_Videos_recompensado_debug);
        }
        RewardedAd.load( mCompat, key, new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                progress.dismiss();
                rewardedAd.setFullScreenContentCallback( new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        callback.onadsResult(ADCLICKED);
                        log("e","firebaseUtils", "loadVideoRewarded ADCLICKED");
                        progress.dismiss();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        progress.dismiss();
                        callback.onadsResult(ADDISMISSED);
                        log("e","firebaseUtils", "loadVideoRewarded ADDISMISSED");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        log("e" , "firebaseUtils", adError.getCode() + " " + adError.getCause() + " " +
                                adError.getMessage() + " " + adError.getDomain());
                        toast(mCompat, "e", mCompat.getString(R.string.str_admob_fail) + " Code: " + adError.getCode());
                        callback.onadsResult(ADDFAILTOSHOW);
                        progress.dismiss();
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        callback.onadsResult(ADIMPRESSION);
                        log("e","firebaseUtils", "loadVideoRewarded ADIMPRESSION");
                        progress.dismiss();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        callback.onadsResult(ADSHOWEDFULL);
                        log("e","firebaseUtils", "loadVideoRewarded ADSHOWEDFULL");
                        progress.dismiss();
                    }
                });
                rewardedAd.show(mCompat, onUserEarnedRewardListener());
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Handle the error.
                log("e" , "firebaseUtils", adError.toString());
                toast(mCompat, "e", mCompat.getString(R.string.str_admob_fail) + " Code: " + adError.getCode());
                progress.dismiss();
                loadInterstitialRewarded(mCompat, result -> {
                    log("e","firebaseUtils", "loadInterstitialRewarded " + result);
                    if (result.equals(ADDISMISSED)) {
                        callback.onadsResult(result);
                    }
                }, debugApp);
            }

        });
    }
    public static void loadInterstitialRewarded(AppCompatActivity mCompat, adsCallback callback, boolean debugApp) {
        ProgressAlert progress;
        progress = ProgressAlert.getInstance(mCompat, true);
        progress.setMessage("Please Wait!!");
        progress.setCancelable(false);
        progress.show();
        // Use the test ad unit ID to load an ad.
        String key = mCompat.getString(R.string.ad_interstitial_recompensado_release);
        if (debugApp) {
            //It's not a release version.
            key = mCompat.getString(R.string.ad_interstitial_recompensado_debug);
        }
        RewardedInterstitialAd.load(mCompat, key, new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                progress.dismiss();
                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        callback.onadsResult(ADCLICKED);
                        log("e","firebaseUtils", "loadInterstitialRewarded ADCLICKED" );
                        progress.dismiss();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        progress.dismiss();
                        callback.onadsResult(ADDISMISSED);
                        log("e","firebaseUtils", "loadInterstitialRewarded ADDISMISSED" );
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        log("e" , "firebaseUtils", adError.getCode() + " " + adError.getCause() + " " +
                                adError.getMessage() + " " + adError.getDomain());
                        toast(mCompat, "e", mCompat.getString(R.string.str_admob_fail) + " Code: " + adError.getCode());
                        callback.onadsResult(ADDFAILTOSHOW);
                        progress.dismiss();
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        callback.onadsResult(ADIMPRESSION);
                        log("e","firebaseUtils", "loadInterstitialRewarded ADIMPRESSION" );
                        progress.dismiss();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        callback.onadsResult(ADSHOWEDFULL);
                        log("e","firebaseUtils", "loadInterstitialRewarded ADSHOWEDFULL" );
                        progress.dismiss();
                    }

                });
                ad.show(mCompat, onUserEarnedRewardListener());
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                log("e" , "firebaseUtils", adError.toString());
                toast(mCompat, "e", mCompat.getString(R.string.str_admob_fail) + " Code: " + adError.getCode());
                progress.dismiss(); 
                loadInterstitial(mCompat, result -> {
                    log("e","firebaseUtils", "loadInterstitialRewarded " + result); 
                    if (result.equals(ADDISMISSED)) {
                        callback.onadsResult(result);
                    }
                }, debugApp);
            }
        });
    }
    public static void loadInterstitial(AppCompatActivity mCompat, adsCallback callback, boolean debugApp){
        final String[] adsComplete = {""};
        ProgressAlert progress;
        progress = ProgressAlert.getInstance(mCompat, true);
        progress.setMessage("Please Wait!!");
        progress.setCancelable(false);
        progress.show();
        String key = mCompat.getString(R.string.ad_interstitial_release);
        if (debugApp) {
            //It's not a release version.
            key = mCompat.getString(R.string.ad_interstitial_debug);
        }
        InterstitialAd.load(mCompat, key, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                progress.dismiss();
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        callback.onadsResult(ADCLICKED);
                        log("e","firebaseUtils", "loadInterstitial ADCLICKED" );
                        progress.dismiss();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        progress.dismiss();
                        callback.onadsResult(ADDISMISSED);
                        log("e","firebaseUtils", "loadInterstitial ADDISMISSED" );
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        log("e" , "firebaseUtils", adError.getCode() + " " + adError.getCause() + " " +
                                adError.getMessage() + " " + adError.getDomain());
                        toast(mCompat, "e", mCompat.getString(R.string.str_admob_fail) + " Code: " + adError.getCode());
                        callback.onadsResult(ADDFAILTOSHOW);
                        progress.dismiss();
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        callback.onadsResult(ADIMPRESSION);
                        log("e","firebaseUtils", "loadInterstitial ADIMPRESSION" );
                        progress.dismiss();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        callback.onadsResult(ADSHOWEDFULL);
                        log("e","firebaseUtils", "loadInterstitial ADSHOWEDFULL" );
                        progress.dismiss();
                    }

                });
                interstitialAd.show(mCompat);
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Handle the error
                log("e" , "firebaseUtils", adError.toString());
                toast(mCompat, "e", mCompat.getString(R.string.str_admob_fail) + " Code: " + adError.getCode()); 
                callback.onadsResult(ADFAILTOLOAD);
                progress.dismiss();
            }
        });
    }
}
