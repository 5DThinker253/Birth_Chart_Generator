package com.ejs.birthchart.fragments;

import static com.ejs.birthchart.utils.DateTimeUtils.checkDateFormat;
import static com.ejs.birthchart.utils.DateTimeUtils.dateFormatter;
import static com.ejs.birthchart.utils.DateTimeUtils.parseDateTime;
import static com.ejs.birthchart.utils.constValues.const_debugAds;
import static com.ejs.birthchart.utils.constValues.const_debugApp;
import static com.ejs.birthchart.utils.firebaseUtils.fetchFrc;
import static com.ejs.birthchart.utils.firebaseUtils.initAdView;
import static com.ejs.birthchart.utils.firebaseUtils.initRemoteConfig;
import static com.ejs.birthchart.utils.msg.toast;
import static com.ejs.birthchart.utils.utils.checkPermissions;
import static com.ejs.birthchart.utils.utils.getMoonSingleDetails;
import static com.ejs.birthchart.utils.utils.loadImageFileMoon;
import static com.ejs.birthchart.utils.utils.moonPhase;
import static com.ejs.birthchart.utils.utils.moonType;
import static com.ejs.birthchart.utils.utils.permissionsPost;
import static com.ejs.birthchart.utils.utils.permissionsStorage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ejs.birthchart.R;
import com.ejs.birthchart.classes.textWatcher;
import com.ejs.birthchart.data.dataMoonCal;
import com.google.android.gms.ads.AdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

public class fragment_moonphase extends Fragment {
    private final AppCompatActivity mCompat;

    private TextView tv_day, tv_moonPhase, tv_distance, tv_ilumination, tv_moonAge, tv_moonAgePerc, tv_sunrise, tv_sunset, tv_Timemoonrise,
            tv_Timemoonset, tv_morning, tv_evening, tv_MgoldenHourStart, tv_MgoldenHourEnd, tv_EgoldenHourStart, tv_EgoldenHourEnd,
            tv_selResult;

    private EditText et_Seldate, et_Seltime;
    private LinearLayout ll_moonPhase;
    private CardView cv_day;
    private ImageView iv_moonPhase;
    private Button btn_getPhase;

    private double lat, lon;

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private LocalDateTime birthDate, birthDateLocal;

    private CardView btn_moonCal, cv_moonPhase, btn_eclipses;

    private FirebaseRemoteConfig frc;
    private AdView mAdView;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    public fragment_moonphase(AppCompatActivity mCompat, double lat, double lon) {
        this.mCompat = mCompat;
        this.lat = lat;
        this.lon = lon;
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
        View view = inflater.inflate(R.layout.fragment_moonphase, container, false);

        cv_day = view.findViewById(R.id.cv_day);
        iv_moonPhase = view.findViewById(R.id.iv_moonPhase);
        tv_moonPhase = view.findViewById(R.id.tv_moonPhase);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_ilumination = view.findViewById(R.id.tv_ilumination);
        tv_moonAge = view.findViewById(R.id.tv_moonAge);
        tv_moonAgePerc = view.findViewById(R.id.tv_moonAgePerc);
        tv_sunrise = view.findViewById(R.id.tv_sunrise);
        tv_sunset = view.findViewById(R.id.tv_sunset);
        tv_Timemoonrise = view.findViewById(R.id.tv_Timemoonrise);
        tv_Timemoonset = view.findViewById(R.id.tv_Timemoonset);
        tv_morning = view.findViewById(R.id.tv_morning);
        tv_evening = view.findViewById(R.id.tv_evening);
        tv_MgoldenHourStart = view.findViewById(R.id.tv_MgoldenHourStart);
        tv_MgoldenHourEnd = view.findViewById(R.id.tv_MgoldenHourEnd);
        tv_EgoldenHourEnd = view.findViewById(R.id.tv_EgoldenHourEnd);
        tv_EgoldenHourStart = view.findViewById(R.id.tv_EgoldenHourStart);
        btn_getPhase = view.findViewById(R.id.btn_getPhase);
        ll_moonPhase = view.findViewById(R.id.ll_moonPhase);
        et_Seldate = view.findViewById(R.id.etxt_selDate);
        et_Seltime = view.findViewById(R.id.etxt_seltime);
        et_Seldate.addTextChangedListener(new textWatcher(et_Seldate));
        tv_distance.setSelected(true);
        tv_moonPhase.setSelected(true);
        tv_EgoldenHourEnd.setSelected(true);
        tv_EgoldenHourStart.setSelected(true);
        tv_MgoldenHourStart.setSelected(true);
        tv_MgoldenHourEnd.setSelected(true);
        tv_evening.setSelected(true);
        tv_morning.setSelected(true);
        tv_moonAge.setSelected(true);
        tv_moonAgePerc.setSelected(true);
        tv_ilumination.setSelected(true);
        tv_sunrise.setSelected(true);
        tv_sunset.setSelected(true);

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
        checkPermissions(mCompat, requestPermissionLauncher, permissionsStorage());
        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());
        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);


        et_Seltime.setInputType(InputType.TYPE_NULL);
        // Obtener la fecha y hora actual
        LocalDateTime currentDateTime = LocalDateTime.of(1980,1,1,1,1);
        currentDateTime = LocalDateTime.now();
        // Obtener la fecha y hora seleccionadas por separado
        selectedDate = currentDateTime.toLocalDate();
        selectedTime = currentDateTime.toLocalTime();

        final ZonedDateTime cldr = ZonedDateTime.now();
        int hour = cldr.getHour();
        int minutes = cldr.getMinute();


        DatePickerDialog.OnDateSetListener myDateListener = (view13, year, month, dayOfMonth) -> {
            et_Seldate.setText(dayOfMonth + "/" + month + 1 + "/" + year);
        };
        TimePickerDialog.OnTimeSetListener myTimeListener = (view12, hourOfDay, minute) -> {
            String hour1 = "";
            if (hourOfDay < 10) {
                hour1 = "0" + hourOfDay;
            } else {
                hour1 = String.valueOf(hourOfDay);
            }
            String minutes1 = "";
            if (minute < 10) {
                minutes1 = "0" + minute;
            } else {
                minutes1 = String.valueOf(minute);
            }
            et_Seltime.setText(hour1 + ":" + minutes1);
        };
        timePicker = new TimePickerDialog(mCompat, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timePick(), hour, minutes, true);
        datePicker = new DatePickerDialog(mCompat, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, datePick(), selectedDate.getYear(), selectedDate.getMonthValue(), selectedDate.getDayOfMonth());

        et_Seldate.setOnClickListener(v1 -> {
            datePicker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            datePicker.show();
        });
        et_Seltime.setOnClickListener(v1 -> {
            timePicker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            timePicker.show();
        });
        btn_getPhase.setOnClickListener(view1 -> {

            if (et_Seldate.length() > 0 && et_Seltime.length() > 0  && lat > -90 && lon > -180 ){
                if (checkDateFormat(et_Seldate.getText().toString())){
                    /*if (faselunaVIP) {
                    getPhase(et_Seldate.getText().toString(), et_Seltime.getText().toString());
                    }else {
                        alertVIP(mCompat, 1, date);
                    }*/
                    getPhase(et_Seldate.getText().toString(), et_Seltime.getText().toString(), lat, lon);
                } else {
                    toast(mCompat, "i" ,getResources().getString(R.string.str_SelDate));
                }

            } else {
                toast(mCompat, "i" ,getResources().getString(R.string.str_SelDate));
            }
        });

        return view;
    }

    private DatePickerDialog.OnDateSetListener datePick() {
        return (view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            String Months = String.valueOf((month + 1));
            if ((month + 1) < 10) {
                Months = 0 + Months;
            }
            et_Seldate.setText(selectedDate.format(dateFormatter));
        };
    }
    private TimePickerDialog.OnTimeSetListener timePick() {
        return (view, hourOfDay, minute) -> {
            selectedTime = LocalTime.of(hourOfDay, minute);
            et_Seltime.setText(selectedTime.toString());
        };
    }

    private void getPhase(String date, String time, double lat, double lon){

        final double[] COLOGNE = new double[] { lat, lon };

        LocalDateTime ldt = parseDateTime(date, time);
        ZonedDateTime zdt = ldt.atZone(TimeZone.getDefault().toZoneId());

        dataMoonCal item = new dataMoonCal();
        item = getMoonSingleDetails(mCompat, item, COLOGNE, zdt);;

        String distance =String.format(Locale.getDefault(), "%,d", item.getDistance()) + " km";
        String moonPhase = moonPhase(mCompat, item.getMoonPhasedouble()) + " " + moonType(mCompat, item.getTypeMoon());
        String moonAge = (int) item.getMoonAge() + " " + mCompat.getString(R.string.day);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap mOriginalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fullmoon, options);
        mOriginalBitmap = loadImageFileMoon(mCompat, item.getMoonPhasedouble());
        //iv_moonPhase.setImageBitmap(moonPhaseBitmap(mCompat, item.getMoonPhasedouble()));
        iv_moonPhase.setImageBitmap(mOriginalBitmap);
        tv_distance.setText(distance);
        tv_moonPhase.setText(moonPhase);
        tv_EgoldenHourEnd.setText(item.getEveningbluehour());
        tv_EgoldenHourStart.setText(item.getEveninggoldenhour());
        tv_MgoldenHourStart.setText(item.getMorningbluehour());
        tv_MgoldenHourEnd.setText(item.getMorninggoldenhour());
        tv_moonAge.setText(moonAge);
        tv_moonAgePerc.setText(item.getMoonAgePorc() + "%");
        tv_ilumination.setText(item.getIllumination() + "%");
        tv_sunrise.setText(item.getSunrise());
        tv_sunset.setText(item.getSunSet());
        tv_Timemoonrise.setText(item.getMoonRise());
        tv_Timemoonset.setText(item.getMoonSet());
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {

        } else {

        }
    });

}
