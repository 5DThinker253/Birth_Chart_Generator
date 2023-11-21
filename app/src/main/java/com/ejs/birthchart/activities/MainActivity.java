package com.ejs.birthchart.activities;

import static android.os.Build.VERSION.SDK_INT;
import static com.ejs.birthchart.utils.firebaseUtils.fetchFrc;
import static com.ejs.birthchart.utils.firebaseUtils.initRemoteConfig;
import static com.ejs.birthchart.utils.firebaseUtils.logEventAnalytics;
import static com.ejs.birthchart.utils.utils.checkPermissions;
import static com.ejs.birthchart.utils.utils.openfragment;
import static com.ejs.birthchart.utils.utils.permissionsPost;
import static com.ejs.birthchart.utils.utils.permissionsStorage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ejs.birthchart.R;
import com.ejs.birthchart.classes.TimezoneMapper;
import com.ejs.birthchart.classes.welcomeDialogFragment;
import com.ejs.birthchart.fragments.fragment_main;
import com.ejs.birthchart.fragments.fragment_support;
import com.ejs.birthchart.interfaces.CodeToExecute;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.ump.ConsentDebugSettings;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import swisseph.SweDate;
import swisseph.SwissData;
import swisseph.SwissEph;
import swisseph.SwissLib;

public class MainActivity extends AppCompatActivity {
    private AppCompatActivity mCompat;
    private static final int REQUEST_PERMISSION = 1;
    private FirebaseRemoteConfig frc;

    @Override
    protected void onResume() {
        super.onResume();
        fetchFrc(mCompat, frc, requestPermissionLauncher);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCompat = this;
        welcomeDialogFragment.show(mCompat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleText);
        setSupportActionBar(toolbar);

        checkPermissions(mCompat, requestPermissionLauncher, permissionsStorage());
        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        logEventAnalytics(mCompat,"MainActivity", mFirebaseAnalytics.toString());
        /*MobileAds.initialize(this, initializationStatus -> {
        });*/
        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);

        /*Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(52.5223423, 13.40423423, 1);

            Log.e("data", "tmz " + addresses.get(0));
            Log.e("data", "tmz " + addresses.size());
            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                Log.e("data", "fetchedAddress " + fetchedAddress.getAddressLine(fetchedAddress.getMaxAddressLineIndex()));
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }

                Log.e("data", "tmz " + strAddress.toString());

            } else {
                Log.e("data", "Searching Current Address");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        double lat = 52.52;
        double lon = 13.40;
        String tmz = TimezoneMapper.latLngToTimezoneString(lat, lon);
        LocalDateTime currentDateTime = LocalDateTime.of(1983,2,27,5,30);
        Log.e("data", "currentDateTime " + currentDateTime);
        Log.e("data", "currentDateTime " + tmz);
        // Iniciar el AsyncTask al hacer clic en el botón
        executeBackgroundTask(toolbar, () -> {
            //TimeZoneEngine engine = TimeZoneEngine.initialize((lat - 10), (lon - 10),(lat + 10), (lon + 10), false);
            ZoneId zoneId = ZoneId.of(tmz);
            TimeZone tz = TimeZone.getTimeZone(zoneId);

            ZonedDateTime zdt = currentDateTime.atZone(zoneId);
            Log.e("data", "zdt " + zdt);
            Date output = Date.from(zdt.toInstant());
            Log.e("data", "output " + output);

            Log.e("data", "tmz " + zoneId);
            Log.e("data", "tmz normalized " + zoneId.normalized().toString());
            Log.e("data", "getDSTSavings " + tz.getDSTSavings());
            Log.e("data", "inDaylightTime " + tz.inDaylightTime(output));
            Log.e("data", "getDisplayName " + tz.getDisplayName(tz.inDaylightTime(output), TimeZone.SHORT));
            Log.e("data", "getDisplayName " + tz.getDisplayName(tz.inDaylightTime(output), TimeZone.LONG));
            Log.e("data", "getID " + tz.getID());
        });
        //TimeZoneEngine engine = TimeZoneEngine.initialize(-90.0, -180.0, 90.0, 180.0, true);
        /**/
        //openfragment(mCompat, new fragment_input(mCompat), R.id.fcv_setting, "fragment_input");
        openfragment(mCompat, new fragment_main(mCompat), R.id.fcv_setting, "fragment_main");
        //openfragment(mCompat, new fragment_chart(mCompat, event, houseSystem, siderealMode, location), R.id.fcv_setting, "fragment_chart");

    }
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private void executeBackgroundTask(Toolbar toolbar, CodeToExecute code) {
        // Mostrar el ProgressDialog o ProgressBar antes de ejecutar la tarea en segundo plano
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        // Ejecutar la tarea en segundo plano utilizando Executor
        executor.execute(() -> {
            // Simular una tarea en segundo plano que toma algún tiempo
            code.execute();

            // Ocultar el ProgressDialog o ProgressBar después de que termine la tarea en segundo plano
            handler.post(() -> {
                progressDialog.dismiss();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=" + mCompat.getPackageName();
                String shareSub = "App link";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share App Link Via :"));

            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + mCompat.getPackageName());
            intent.setType("text/plain");
            startActivity(intent);*/
                return true;
            case R.id.btn_support:
                openfragment(mCompat, new fragment_support(mCompat), R.id.fcv_setting, "fragment_support");
                return true;
            case R.id.btn_more:
                // Acción para el botón de más opciones
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    public static int[] getTimeFromTimeZone(String timeZone) {
        // Obtener el signo, la hora y los minutos de la cadena de la zona horaria
        String sign = timeZone.substring(3, 4);
        int hour = Integer.parseInt(timeZone.substring(4, 6));
        int minute = Integer.parseInt(timeZone.substring(7, 9));

        // Aplicar el signo adecuado a la hora
        if (sign.equals("-")) {
            hour = -hour;
            minute = -minute;
        }

        // Devolver el valor de la hora
        return new int[]{hour, minute};
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            if (SDK_INT >= 33) {
                /*for (int i=0; i < permissions().length; i++) {
                    if (shouldShowRequestPermissionRationale(permissions()[i])) {
                        showPermissionRationale(permissions()[i]);
                    } else {
                        showSettingDialog();
                    }
                }*/
            }
        }
    });

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