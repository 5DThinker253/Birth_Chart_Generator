package com.ejs.birthchart.interfaces;


import static android.os.Build.VERSION.SDK_INT;

import static com.ejs.birthchart.utils.constValues.const_debugAds;
import static com.ejs.birthchart.utils.constValues.const_debugApp;
import static com.ejs.birthchart.utils.firebaseUtils.ADDISMISSED;
import static com.ejs.birthchart.utils.firebaseUtils.fetchFrc;
import static com.ejs.birthchart.utils.firebaseUtils.initRemoteConfig;
import static com.ejs.birthchart.utils.firebaseUtils.loadInterstitial;
import static com.ejs.birthchart.utils.firebaseUtils.loadInterstitialRewarded;
import static com.ejs.birthchart.utils.firebaseUtils.loadVideoRewarded;
import static com.ejs.birthchart.utils.msg.log;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ejs.birthchart.utils.Base64Utils;
import com.google.android.gms.ads.AdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

// Clase para recibir llamadas desde el código JavaScript
public class JavaScriptInterface {
    AppCompatActivity mCompat;
    private FirebaseRemoteConfig frc;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    public JavaScriptInterface(AppCompatActivity mCompat, FirebaseRemoteConfig frc) {
        this.mCompat = mCompat;
        this.frc = frc;
        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, null);
    }

    @android.webkit.JavascriptInterface
    public void data2log(final String data) {
        if (data != null) Log.e("astral",  " data: " + data.toString()  +  "\n") ;
    }

    @android.webkit.JavascriptInterface
    public void saveImage(final String base64Image) {
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
                            }
                        }, debugAds);
                        break;
                    case 1:
                        loadInterstitialRewarded(mCompat, result -> {
                            log("e","adstest", "callback " + result);
                            // Realiza las acciones necesarias en función del resultado obtenido
                            if (result.equals(ADDISMISSED)) {
                            }
                        }, debugAds);
                        break;
                    case 2:
                        loadVideoRewarded(mCompat, result -> {
                            log("e","adstest", "callback " + result);
                            // Realiza las acciones necesarias en función del resultado obtenido
                            if (result.equals(ADDISMISSED)) {
                            }
                        }, debugAds);
                        break;
                }
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
                            }
                        }, debugAds);
                        break;
                    case 1:
                        loadInterstitialRewarded(mCompat, result -> {
                            log("e","adstest", "callback " + result);
                            // Realiza las acciones necesarias en función del resultado obtenido
                            if (result.equals(ADDISMISSED)) {
                            }
                        }, debugAds);
                        break;
                    case 2:
                        loadVideoRewarded(mCompat, result -> {
                            log("e","adstest", "callback " + result);
                            // Realiza las acciones necesarias en función del resultado obtenido
                            if (result.equals(ADDISMISSED)) {
                            }
                        }, debugAds);
                        break;
                }
            }
        }
        // Permiso concedido, guardar la imagen
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                saveImageInAndroidApi29AndAbove(mCompat, base64Image);
            } catch (Exception e) {
                Log.e("astral", "Error ", e);
                throw new RuntimeException(e);
            }
        } else {
            try {
                saveImageInAndroidApi28AndBelow(mCompat, base64Image);
            } catch (IOException e) {
                Log.e("astral", "Error ", e);
                Toast.makeText(mCompat, "Error saving image file", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }
    }

    @NonNull
    private Uri saveImageInAndroidApi29AndAbove(@NonNull final Context context, @NonNull final String base64Image) throws IOException {
        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "birth_char_" + LocalDateTime.now().toString());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        }
        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;
        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);
            if (uri == null) {
                //isSuccess = false;
                throw new IOException("Failed to create new MediaStore record.");
            }
            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null) {
                    //isSuccess = false;
                    throw new IOException("Failed to open output stream.");
                }
                Bitmap bitmap = Base64Utils.decodeBase64ToBitmap(base64Image);
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    //isSuccess = false;
                    throw new IOException("Failed to save bitmap.");
                }
            }
            //isSuccess = true;
            Toast.makeText(mCompat, "Image Saved in pictures ", Toast.LENGTH_SHORT).show();
            return uri;
        } catch (IOException e) {
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            throw e;
        }
    }
    private boolean saveImageInAndroidApi28AndBelow(@NonNull final Context context, @NonNull final String base64Image) throws IOException {
        OutputStream fos;
        String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(imagesDir, "birth_char_"+ LocalDateTime.now().toString() +".png");

        fos = new FileOutputStream(image);
        Bitmap bitmap = Base64Utils.decodeBase64ToBitmap(base64Image);
        bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
        Objects.requireNonNull(fos).close();
        Toast.makeText(mCompat, "Image Saved in pictures ", Toast.LENGTH_SHORT).show();
        //isSuccess = true;
        return true;
    }
}
