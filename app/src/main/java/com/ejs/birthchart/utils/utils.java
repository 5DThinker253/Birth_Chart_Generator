package com.ejs.birthchart.utils;

import static android.os.Build.VERSION.SDK_INT;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_NOMBRE;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.TABLE_CHARTS;
import static com.ejs.birthchart.utils.DateTimeUtils.TimeFormatter24;
import static com.ejs.birthchart.utils.DateTimeUtils.timeZone;
import static com.ejs.birthchart.utils.constValues.*;
import static com.ejs.birthchart.utils.math.roundDecimal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;
import com.ejs.birthchart.data.dataMoonCal;
import com.ejs.birthchart.db.ChartsDatabaseHelper;
import com.ejs.birthchart.prototipe.MoonIllumination;
import com.ejs.birthchart.prototipe.MoonPhase;
import com.ejs.birthchart.prototipe.MoonPosition;
import com.ejs.birthchart.prototipe.MoonTimes;
import com.ejs.birthchart.prototipe.SunTimes;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;

public class utils {

    private static Animation expandAnimation;
    private static Animation collapseAnimation;

    public static void expandView(AppCompatActivity mCompat, View view) {
        expandAnimation = AnimationUtils.loadAnimation(mCompat, R.anim.expand_animation);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(expandAnimation);
    }
    public static void collapseView(AppCompatActivity mCompat,View view) {
        collapseAnimation = AnimationUtils.loadAnimation(mCompat, R.anim.collapse_animation);
        view.setVisibility(View.GONE);
        view.startAnimation(collapseAnimation);
    }
    public static List<Address> getCoordinates(Context context, String city, String country) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            return geocoder.getFromLocationName(city + ", " + country, 1);
        } catch (IOException e) {
            return null;
        }
    }
    public static void checkPermissions(AppCompatActivity mCompat, ActivityResultLauncher<String> requestPermissionLauncher, String[] permissions) {

        if (SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (SDK_INT >= android.os.Build.VERSION_CODES.R) {

                if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    for (int i=0; i < permissions.length; i++) {
                        if (ContextCompat.checkSelfPermission(mCompat, permissions[i]) == PackageManager.PERMISSION_GRANTED) {

                        } else if (shouldShowRequestPermissionRationale(mCompat, permissions[i])) {
                            showPermissionRationale(mCompat,mCompat.getString(R.string.permission_global_title),
                                    mCompat.getString(R.string.permission_global_msg), permissions[i], requestPermissionLauncher);

                        } else {
                            ActivityCompat.requestPermissions(mCompat, permissions, 1001);
                        }
                    }
                }
            } else {
                for (int i=0; i < permissions.length; i++) {
                    if (ContextCompat.checkSelfPermission(mCompat, permissions[i]) == PackageManager.PERMISSION_GRANTED) {

                    } else if (shouldShowRequestPermissionRationale(mCompat, permissions[i])) {
                        showPermissionRationale(mCompat,mCompat.getString(R.string.permission_global_title),
                                mCompat.getString(R.string.permission_global_msg), permissions[i], requestPermissionLauncher);

                    } else {
                        ActivityCompat.requestPermissions(mCompat, permissions, 1001);
                    }
                }

            }
            for (int i=0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mCompat, permissions[i]) == PackageManager.PERMISSION_GRANTED) {

                } else if (shouldShowRequestPermissionRationale(mCompat, permissions[i])) {
                    showPermissionRationale(mCompat,mCompat.getString(R.string.permission_global_title),
                            mCompat.getString(R.string.permission_global_msg), permissions[i], requestPermissionLauncher);

                } else {
                    ActivityCompat.requestPermissions(mCompat, permissions, 1001);
                }
            }
        }
    }
    public static String[] permissionsLocation() {
        return new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }
    public static String[] permissionsStorage() {
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
    public static String[] permissionsPost() {
        String[] p = new String[0];
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = new String[]{
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }
        return p;
    }

    public static void showPermissionRationale(Context context, String title, String msg, String permission, ActivityResultLauncher<String> requestPermissionLauncher) {
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

    public static void showNotFoundDialog(AppCompatActivity mCompat, String place) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCompat)
                .setTitle(R.string.location_not_found_title)
                .setMessage(place)
                .setPositiveButton(android.R.string.ok, (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }
    public static void showNotFoundDialog(AppCompatActivity activity, String title, String msg, final DialogCallback callback) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, (dialog1, which) -> {
                    if (callback != null) {
                        callback.onDialogResult(true);
                    }
                    dialog1.dismiss();
                })
                .setCancelable(false);
        dialog.show();
    }

    public interface DialogCallback {
        void onDialogResult(boolean result);
    }
    public static ArrayList<dataMoonCal> getMoonDetails(Context context, ArrayList<dataMoonCal> arrayMooncal, double[] COLOGNE, int Year, int Month, int Day, int Grade){
        String parcelTime = "yyyy-MM-dd'T'HH:mm";
        String timeZone = TimeZone.getDefault().getID();
        ZonedDateTime date = ZonedDateTime.of(Year, Month, Day, 6,1,1,0, ZoneId.of(timeZone));
        MoonPhase.Parameters paramsMOON = MoonPhase.compute().phase(Grade);
        MoonTimes.Parameters moonParameters = MoonTimes.compute().at(COLOGNE);
        SunTimes.Parameters base = SunTimes.compute().at(COLOGNE);
        MoonIllumination.Parameters Illumparameters = MoonIllumination.compute();


        while (true) {
            MoonPhase moonMOON = paramsMOON.on(date).timezone(timeZone).execute();
            ZonedDateTime nextMOON = moonMOON.getTime();
            MoonTimes moonTime = moonParameters.on(nextMOON).timezone(timeZone).execute();
            SunTimes blue = base.on(nextMOON).timezone(timeZone).copy().twilight(SunTimes.Twilight.BLUE_HOUR).execute();
            SunTimes golden = base.on(nextMOON).timezone(timeZone).copy().twilight(SunTimes.Twilight.GOLDEN_HOUR).execute();
            SunTimes times = base.on(nextMOON).timezone(timeZone).execute();
            MoonIllumination moonIllumination = Illumparameters.on(nextMOON).timezone(timeZone).execute();
            if (nextMOON.getMonthValue() != Month) break;

            int day = nextMOON.getDayOfMonth();
            int month = nextMOON.getMonthValue();
            int year = nextMOON.getYear();

            assert blue.getRise() != null;
            LocalTime bluerise = blue.getRise().toLocalTime();
            assert blue.getSet() != null;
            LocalTime blueset = blue.getSet().toLocalTime();
            assert golden.getRise() != null;
            LocalTime goldenrise = golden.getRise().toLocalTime();
            assert golden.getSet() != null;
            LocalTime goldenset = golden.getSet().toLocalTime();
            assert times.getRise() != null;
            LocalTime dateSunrise = times.getRise().toLocalTime();
            assert times.getSet() != null;
            LocalTime dateSunset = times.getSet().toLocalTime();
            assert moonTime.getRise() != null;
            LocalTime dateMoonrise = moonTime.getRise().toLocalTime();
            assert moonTime.getSet() != null;
            LocalTime dateMoonset = moonTime.getSet().toLocalTime();
            String Eveningbluehour = blueset.format(TimeFormatter24);
            String Eveninggoldenhour = goldenset.format(TimeFormatter24);
            String MorningBlueHour = bluerise.format(TimeFormatter24);
            String Morninggoldenhour = goldenrise.format(TimeFormatter24);
            String Sunrise = dateSunrise.format(TimeFormatter24);
            String SunSet = dateSunset.format(TimeFormatter24);
            String MoonRise = dateMoonrise.format(TimeFormatter24);
            String MoonSet = dateMoonset.format(TimeFormatter24);

            String phase="NEW_MOON";
            int type= 0;

            int distance = (int) moonMOON.getDistance();
            int moonAge = (int) SynodicMonth * Grade / angle;
            int agePercent =  (int) (100 * moonAge / SynodicMonth);
            int Illumination = (int) Math.round(moonIllumination.getFraction() * 100.0);
            phase = moonPhase(context,Grade);
            if (moonMOON.isMicroMoon()) type = 1;
            if (moonMOON.isSuperMoon()) type = 2;

            dataMoonCal dataMooncal = new dataMoonCal();
            dataMooncal.setDay(day);
            dataMooncal.setDistance(distance);
            dataMooncal.setIllumination(Illumination);
            dataMooncal.setMoonAge(moonAge);
            dataMooncal.setMoonAgePorc(agePercent);
            dataMooncal.setMoonPhase(Grade);
            dataMooncal.setTypeMoon(type);
            dataMooncal.setEveningbluehour(Eveningbluehour);
            dataMooncal.setEveninggoldenhour(Eveninggoldenhour);
            dataMooncal.setMorningBlueHour(MorningBlueHour);
            dataMooncal.setMorninggoldenhour(Morninggoldenhour);
            dataMooncal.setSunrise(Sunrise);
            dataMooncal.setSunSet(SunSet);
            dataMooncal.setMoonRise(MoonRise);
            dataMooncal.setMoonSet(MoonSet);
            int pos=0;
            //dataMooncal.setPositionMoon(moonPosition(dateTime));
            ListIterator<dataMoonCal> iterator = arrayMooncal.listIterator();
            while (iterator.hasNext()) {
                dataMoonCal next = iterator.next();
                if (next.getDay() == day) {
                    //Replace element
                    pos = arrayMooncal.indexOf(next);
                }
            }
            arrayMooncal.set(pos, dataMooncal);

            date = nextMOON.plusDays(1);
        }
        return arrayMooncal;
    }

    public static ArrayList<dataMoonCal> getMoonDetails2(Context context, ArrayList<dataMoonCal> arrayMooncal, double[] COLOGNE, int Year, int Month, int Day, int lengthOfMonth){
        String timeZone = TimeZone.getDefault().getID();
        String parcelTime = "yyyy-MM-dd'T'HH:mm";
        arrayMooncal.clear();
        ZonedDateTime date = ZonedDateTime.of(Year, Month, Day, 6,1,1,0, ZoneId.of(timeZone));

        MoonTimes.Parameters moonParameters = MoonTimes.compute().at(COLOGNE);
        SunTimes.Parameters base = SunTimes.compute().at(COLOGNE);
        MoonIllumination.Parameters Illumparameters = MoonIllumination.compute();
        MoonPosition.Parameters moonParam = MoonPosition.compute().sameLocationAs(base);

        for (int i= 1; i<=lengthOfMonth;i++){
            ZonedDateTime dates = ZonedDateTime.of(Year, Month, i, 6,1,1,0, ZoneId.of(timeZone));
            int day = dates.getDayOfMonth();
            int month = dates.getMonthValue();
            int year = dates.getYear();

            MoonPosition moon = moonParam.on(dates).timezone(timeZone).execute();
            SunTimes blue = base.on(dates).timezone(timeZone).copy().twilight(SunTimes.Twilight.BLUE_HOUR).execute();
            SunTimes golden = base.on(dates).timezone(timeZone).copy().twilight(SunTimes.Twilight.GOLDEN_HOUR).execute();
            SunTimes times = base.on(dates).timezone(timeZone).execute();
            MoonTimes moonTime = moonParameters.on(dates).timezone(timeZone).execute();
            int phase = (int) (Illumparameters.on(dates).timezone(timeZone).execute().getPhase() + 180);
            int Illumination = (int) Math.round(Illumparameters.on(dates).timezone(timeZone).execute().getFraction() * 100.0);


            assert blue.getRise() != null;
            LocalTime bluerise = blue.getRise().toLocalTime();
            assert blue.getSet() != null;
            LocalTime blueset = blue.getSet().toLocalTime();
            assert golden.getRise() != null;
            LocalTime goldenrise = golden.getRise().toLocalTime();
            assert golden.getSet() != null;
            LocalTime goldenset = golden.getSet().toLocalTime();
            assert times.getRise() != null;
            LocalTime dateSunrise = times.getRise().toLocalTime();
            assert times.getSet() != null;
            LocalTime dateSunset = times.getSet().toLocalTime();
            assert moonTime.getRise() != null;
            LocalTime dateMoonrise = moonTime.getRise().toLocalTime();
            assert moonTime.getSet() != null;
            LocalTime dateMoonset = moonTime.getSet().toLocalTime();
            String Eveningbluehour = blueset.format(TimeFormatter24);
            String Eveninggoldenhour = goldenset.format(TimeFormatter24);
            String MorningBlueHour = bluerise.format(TimeFormatter24);
            String Morninggoldenhour = goldenrise.format(TimeFormatter24);
            String Sunrise = dateSunrise.format(TimeFormatter24);
            String SunSet = dateSunset.format(TimeFormatter24);
            String MoonRise = dateMoonrise .format(TimeFormatter24);
            String MoonSet = dateMoonset .format(TimeFormatter24);
            int type= 0;

            int distance = (int) moon.getDistance();
            int moonAge = (int) SynodicMonth * phase / angle;
            int agePercent =  (int) (100 * moonAge / SynodicMonth);

            dataMoonCal dataMooncal = new dataMoonCal();
            dataMooncal.setDay(day);
            dataMooncal.setDistance(distance);
            dataMooncal.setIllumination(Illumination);
            dataMooncal.setMoonAge(moonAge);
            dataMooncal.setMoonAgePorc(agePercent);
            dataMooncal.setMoonPhase(phase);
            dataMooncal.setTypeMoon(type);
            dataMooncal.setEveningbluehour(Eveningbluehour);
            dataMooncal.setEveninggoldenhour(Eveninggoldenhour);
            dataMooncal.setMorningBlueHour(MorningBlueHour);
            dataMooncal.setMorninggoldenhour(Morninggoldenhour);
            dataMooncal.setSunrise(Sunrise);
            dataMooncal.setSunSet(SunSet);
            dataMooncal.setMoonRise(MoonRise);
            dataMooncal.setMoonSet(MoonSet);
            //dataMooncal.setPositionMoon(moonPosition(dateTime));
            arrayMooncal.add(dataMooncal);

        }
        return arrayMooncal;
    }

    public static dataMoonCal getMoonSingleDetails(Context context, dataMoonCal dataMooncal, double[] COLOGNE, ZonedDateTime date) {

        MoonTimes.Parameters moonParameters = MoonTimes.compute().at(COLOGNE);
        SunTimes.Parameters base = SunTimes.compute().sameLocationAs(moonParameters);
        MoonIllumination.Parameters Illumparameters = MoonIllumination.compute();
        MoonPosition.Parameters moonParam = MoonPosition.compute().sameLocationAs(moonParameters);

        MoonPosition moon = moonParam.on(date).timezone(timeZone).execute();
        SunTimes blue = base.on(date).timezone(timeZone).copy().twilight(SunTimes.Twilight.BLUE_HOUR).execute();
        SunTimes golden = base.on(date).timezone(timeZone).copy().twilight(SunTimes.Twilight.GOLDEN_HOUR).execute();
        SunTimes times = base.on(date).timezone(timeZone).execute();
        MoonTimes moonTime = moonParameters.on(date).timezone(timeZone).execute();
        MoonIllumination illumination = Illumparameters.on(date).timezone(timeZone).execute();
        double Illumination = roundDecimal(illumination.getFraction() * 100.0, 2);

        LocalTime bluerise = blue.getRise().toLocalTime();
        LocalTime blueset = blue.getSet().toLocalTime();
        LocalTime goldenrise = golden.getRise().toLocalTime();
        LocalTime goldenset = golden.getSet().toLocalTime();
        LocalTime dateSunrise = times.getRise().toLocalTime();
        LocalTime dateSunset = times.getSet().toLocalTime();
        LocalTime dateMoonrise = moonTime.getRise().toLocalTime();
        LocalTime dateMoonset = moonTime.getSet().toLocalTime();

        String Eveningbluehour = blueset.format(TimeFormatter24);
        String Eveninggoldenhour = goldenset.format(TimeFormatter24);
        String MorningBlueHour = bluerise.format(TimeFormatter24);
        String Morninggoldenhour = goldenrise.format(TimeFormatter24);
        String Sunrise = dateSunrise.format(TimeFormatter24);
        String SunSet = dateSunset.format(TimeFormatter24);
        String MoonRise = dateMoonrise.format(TimeFormatter24);
        String MoonSet = dateMoonset.format(TimeFormatter24);
        int type = 0;

        int distance = (int) moon.getDistance();
        /*phase = moonPhase(context,Grade);*/

        int phase = (int) (illumination.getPhase() + 180);
        double moonAge = (29.5305902778 * phase) / 360;
        int agePercent =  (int) (100 * moonAge / SynodicMonth);

        dataMooncal.setDay(date.getDayOfMonth());
        dataMooncal.setDistance(distance);
        dataMooncal.setIllumination(Illumination);
        dataMooncal.setMoonAge( (int) moonAge);
        dataMooncal.setMoonAgePorc(agePercent);
        dataMooncal.setMoonPhasedouble(phase);
        dataMooncal.setTypeMoon(type);
        dataMooncal.setEveningbluehour(Eveningbluehour);
        dataMooncal.setEveninggoldenhour(Eveninggoldenhour);
        dataMooncal.setMorningBlueHour(MorningBlueHour);
        dataMooncal.setMorninggoldenhour(Morninggoldenhour);
        dataMooncal.setSunrise(Sunrise);
        dataMooncal.setSunSet(SunSet);
        dataMooncal.setMoonRise(MoonRise);
        dataMooncal.setMoonSet(MoonSet);
        return dataMooncal;
    }

    public static String moonPhase(Context context, int Grade){
        if (Grade >= 0 && Grade < 5) return context.getString(R.string.str_newMoon);
        if (Grade >= 5 && Grade < 85) return context.getString(R.string.str_waxingCrescent);
        if (Grade >= 85 && Grade < 95) return context.getString(R.string.str_waxingQuarter);
        if (Grade >= 95 && Grade < 175) return context.getString(R.string.str_waxingGibbous);
        if (Grade >= 175 && Grade < 185) return context.getString(R.string.str_fullMoon);
        if (Grade >= 185 && Grade < 265) return context.getString(R.string.str_waningGibbous);
        if (Grade >= 265 && Grade < 275) return context.getString(R.string.str_waningQuarter);
        if (Grade >= 275  && Grade < 360) return context.getString(R.string.str_waningCrescent);
        return "";//context.getString(R.string.str_newMoon);
    }
    public static Bitmap moonPhaseBitmap(Context context, int Grade){
        BitmapFactory.Options options = new BitmapFactory.Options();

        Bitmap phaseString = null;
        if (Grade >= 0 && Grade < 5) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon0, options);
        if (Grade >= 5 && Grade < 85) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon1, options);
        if (Grade >= 85 && Grade < 95) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon2, options);
        if (Grade >= 95 && Grade < 175) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon3, options);
        if (Grade >= 175 && Grade < 185) return BitmapFactory.decodeResource(context.getResources(), R.drawable.fullmoon, options);
        if (Grade >= 185 && Grade < 265) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon5, options);
        if (Grade >= 265 && Grade < 275) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon6, options);
        if (Grade >= 275) return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon7, options);
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.moon0, options);
    }
    public static String moonType(Context context, int Type){
        if (Type == 1) return context.getString(R.string.microMoon);
        if (Type == 2) return context.getString(R.string.superMoon);
        return context.getString(R.string.normal);
    }
    public static Bitmap loadImageFileMoon(Context context, int moonphase) {
        String Path = FileUtils.getExternalAppDir(context, "moonphase/").getAbsolutePath();
        int x = (int)Math.round((moonphase * 2) + 502);
        String z = String.format("%" + 4 + "s", x).replace(' ', '0');
        String filename = Path + "/moon_" + z + ".png";
        File moonFile = new File(filename);
        if (moonFile.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(filename, options);
        } else {
            /*if (!isDownloading){
                isDownloading = moonphaseDownload(context);
            }*/
            return moonPhaseBitmap(context, moonphase);
        }

    }

    /*public static boolean moonphaseDownload (Context context) {

        String urlFile = context.getString(R.string.url_assets) + "moonphase.zip";
        String path = FileUtils.getDataDir(context).getAbsolutePath();
        String pathmoonphase = FileUtils.getExternalAppDir1(context).getAbsolutePath();
        final boolean[] Downloading = {true};
        log("e", "frc", "urlFile " + urlFile);
        log("e", "frc", "path " + path);
        log("e", "frc", "pathmoonphase " + pathmoonphase);
        FileDownloadService.DownloadRequest downloadRequest = new FileDownloadService.DownloadRequest(urlFile, path);
        downloadRequest.setRequiresUnzip(true);
        downloadRequest.setDeleteZipAfterExtract(true);
        downloadRequest.setUnzipAtFilePath(pathmoonphase);
        final ProgressAlert[] pad = {null};

        FileDownloadService.OnDownloadStatusListener listener = new FileDownloadService.OnDownloadStatusListener() {

            @Override
            public void onDownloadStarted(String type) {
                if (type.equals("download")) {
                    pad[0] = ProgressAlert.getInstance(context, false);
                    pad[0].setTitle("Theme Download");
                    pad[0].setMessage("Downloading theme. Please wait...");
                    pad[0].setIndeterminate(false);
                    pad[0].setMin(0);
                    pad[0].setMax(100);
                    pad[0].setCancelable(false);
                    pad[0].setCanceledOnTouchOutside(false);
                    pad[0].show();
                }
                if (type.equals("unzip")) {
                    pad[0] = ProgressAlert.getInstance(context, true);
                    pad[0].setMessage("Setting up theme. Please wait...");
                    pad[0].setCancelable(false);
                    pad[0].setCanceledOnTouchOutside(false);
                    pad[0].setIndeterminate(true);
                    pad[0].show();

                }

            }

            @Override
            public void onDownloadCompleted(String type) {
                pad[0].dismiss();
                pad[0] = null;
                if (type.equals("unzip")){
                    Downloading[0] = false;
                }
            }

            @Override
            public void onDownloadFailed(String type) {
                pad[0].dismiss();
                pad[0] = null;
                Downloading[0] = false;
            }

            @Override
            public void onDownloadProgress(long progress, long lenghtFile, String type) {
                if (type.equals("download")) {
                    if (lenghtFile > 0) {
                        pad[0].setSize(size(lenghtFile));
                        pad[0].setProgress(progress, true);
                        pad[0].setprog(progress);
                    } else {
                        if (progress < 0) progress = progress * -1;
                        pad[0].setSize(size(progress));
                        pad[0].setTitle("Downloading resources");
                        pad[0].setMessage("Please Wait...!!!");
                        pad[0].setprog(0);
                        pad[0].setIndeterminate(true);
                    }

                }
            }
        };
        FileDownloadService.FileDownloader downloader = FileDownloadService.FileDownloader.getInstance(downloadRequest, listener);
        downloader.download(context);
        return Downloading[0];
    }*/

    /**
     * Valid editText if null or empty
     * @param editText to validate
     * @return return true if editText not empty
     */
    public static boolean isdataValid(EditText editText, Context mCompat) {
        if (editText == null)  {
            return false;
        } else if (editText.getText().toString().matches("")) {
            editText.setError(mCompat.getString(R.string.mandatory_field));
            return false;
        } else if (editText.getText().toString().matches(mCompat.getString(R.string.location_unable_to_geocode))) {
            editText.setError(mCompat.getString(R.string.mandatory_field));
            return false;
        } else {
            return true;
        }
    }
    /**
     * Valid editText if null or empty
     * @param editText to validate
     * @return return true if editText not empty
     */
    public static boolean isdataValid(TextView editText, Context mCompat) {
        if (editText == null)  {
            return false;
        } else if (editText.getText().toString().matches("")) {
            editText.setError(mCompat.getString(R.string.mandatory_field));
            return false;
        } else if (editText.getText().toString().matches(mCompat.getString(R.string.location_unable_to_geocode))) {
            editText.setError(mCompat.getString(R.string.mandatory_field));
            return false;
        } else {
            return true;
        }
    }

    /**
     * open fragment
     *
     * @param frag fragment
     * @param layout layout container
     */
    public static void openfragment(AppCompatActivity mCompat, Fragment frag, int layout, String fragmentTag){
        FragmentManager fragmentManager = mCompat.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layout, frag, fragmentTag);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    /**
     * Convert TimeZone to double array with houur and minute
     *
     * @param timeZone in string
     * @return return array double hour and minute
     */
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

    public static void showConfirmationDialog(Context context, DialogInterface.OnClickListener dialogOk, DialogInterface.OnClickListener dialogCancel, String btn_ok, String btn_cancel, String msg) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(btn_ok, dialogOk)
                .setNegativeButton(btn_cancel, dialogCancel);
        builder.create().show();
    }

    public static int getPositionForItem(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(item)) {
                return i;
            }
        }
        return -1; // Elemento no encontrado en el Spinner
    }
}
