package com.ejs.birthchart.fragments;

import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_CHART_TYPE;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_CIUDADPAIS;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_FECHA1;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_FECHA2;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_HOUSE;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_ID;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_LOCATION;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_LOCATION_TYPE;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_NOMBRE;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_TIMEZONE1;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_TIMEZONE2;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.COLUMN_ZODIACAL;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.TABLE_CHARTS;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.checkIfEntryExists;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.delData;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.loadData;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.saveData;
import static com.ejs.birthchart.db.ChartsDatabaseHelper.updateData;
import static com.ejs.birthchart.utils.DateTimeUtils.dateTimeFormatter;
import static com.ejs.birthchart.utils.DateTimeUtils.getSQLdate;
import static com.ejs.birthchart.utils.DateTimeUtils.outputFormatter;
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
import static com.ejs.birthchart.utils.utils.getPositionForItem;
import static com.ejs.birthchart.utils.utils.getTimeFromTimeZone;
import static com.ejs.birthchart.utils.utils.isdataValid;
import static com.ejs.birthchart.utils.utils.openfragment;
import static com.ejs.birthchart.utils.utils.permissionsLocation;
import static com.ejs.birthchart.utils.utils.permissionsPost;
import static com.ejs.birthchart.utils.utils.permissionsStorage;
import static com.ejs.birthchart.utils.utils.showConfirmationDialog;
import static com.ejs.birthchart.utils.utils.showNotFoundDialog;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.ejs.birthchart.R;
import com.ejs.birthchart.classes.NumberPickerDialog;
import com.ejs.birthchart.classes.TimezoneMapper;
import com.ejs.birthchart.data.ChartEntry;
import com.ejs.birthchart.data.Option;
import com.ejs.birthchart.db.ChartsDatabaseHelper;
import com.ejs.birthchart.utils.MyLocation;
import com.google.android.gms.ads.AdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

public class fragment_input extends Fragment {
    private final AppCompatActivity mCompat;

    private LinearLayout gpsLayout, cityCountryLayout, manualLayout, siderealLayout, ll_second_date, ll_second_tmz;
    private TextView coordinatesTextView, timezoneTextView, timezoneTextView2;
    private CheckBox chb_auto_tmz, chb_auto_tmz2;
    private EditText dateOfBirthEditText2, timezoneEditText2, timezoneEditText, nameSurnameEditText, dateOfBirthEditText, cityEditText, latitudeEditText, longitudeEditText;
    private Spinner houseSystemSpinner, siderealModeSpinner, sp_saved_data, sp_geolocation, sp_type_chart;
    private String cityName="", countryName="", nameSurname="", house="",  zodiac = "";
    private double latitude, longitude;
    private int selectedGeolocationMethod = 0;
    private int selectedChartMethod = 0;

    private LocalDate selectedDate, selectedDate2;
    private LocalTime selectedTime, selectedTime2;
    private LocalDateTime birthDate, birthDate2, birthDateLocal, birthDateLocal2;
    private final String[] timeZones = {
            "GMT-12:00", "GMT-11:30", "GMT-11:00", "GMT-10:30", "GMT-10:00", "GMT-09:30",
            "GMT-09:00", "GMT-08:30", "GMT-08:00", "GMT-07:30", "GMT-07:00", "GMT-06:30",
            "GMT-06:00", "GMT-05:30", "GMT-05:00", "GMT-04:30", "GMT-04:00", "GMT-03:30",
            "GMT-03:00", "GMT-02:30", "GMT-02:00", "GMT-01:30", "GMT-01:00", "GMT-00:30",
            "GMT+00:00", "GMT+00:30", "GMT+01:00", "GMT+01:30", "GMT+02:00", "GMT+02:30",
            "GMT+03:00", "GMT+03:30", "GMT+04:00", "GMT+04:30", "GMT+05:00", "GMT+05:30",
            "GMT+05:45", "GMT+06:00", "GMT+06:30", "GMT+07:00", "GMT+07:30", "GMT+08:00",
            "GMT+08:30", "GMT+08:45", "GMT+09:00", "GMT+09:30", "GMT+10:00", "GMT+10:30",
            "GMT+11:00", "GMT+11:30", "GMT+12:00", "GMT+12:30", "GMT+13:00", "GMT+13:30", "GMT+14:00"
    };

    private FirebaseRemoteConfig frc;
    private AdView mAdView;

    private final boolean debugApp = const_debugApp;
    private final boolean debugAds = const_debugAds;

    public fragment_input(AppCompatActivity mCompat) {
        // Required empty public constructor
        this.mCompat = mCompat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        // Inflate the layout for this fragment

        Toolbar toolbar = mCompat.findViewById(R.id.toolbar);
        toolbar.setTitle(mCompat.getString(R.string.birthchart_generator));
        toolbar.setTitleTextAppearance(mCompat, R.style.ToolbarTitleText);
        mCompat.setSupportActionBar(toolbar);
        ActionBar actionBar = mCompat.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            mCompat.onBackPressed();
        });
        setHasOptionsMenu(true);

        sp_type_chart = view.findViewById(R.id.sp_type_chart);
        sp_geolocation = view.findViewById(R.id.sp_geolocation);
        sp_saved_data = view.findViewById(R.id.sp_saved_data);

        siderealLayout = view.findViewById(R.id.siderealLayout);
        houseSystemSpinner = view.findViewById(R.id.houseSystemSpinner);
        siderealModeSpinner = view.findViewById(R.id.siderealModeSpinner);
        nameSurnameEditText = view.findViewById(R.id.nameSurnameEditText);
        chb_auto_tmz = view.findViewById(R.id.chb_auto_tmz);
        timezoneTextView = view.findViewById(R.id.timezoneTextView);
        timezoneTextView.setSelected(true);

        ll_second_date = view.findViewById(R.id.ll_second_date);
        ll_second_tmz = view.findViewById(R.id.ll_second_tmz);
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText);
        timezoneEditText = view.findViewById(R.id.timezoneEditText);
        dateOfBirthEditText2 = view.findViewById(R.id.dateOfBirthEditText2);
        timezoneEditText2 = view.findViewById(R.id.timezoneEditText2);
        chb_auto_tmz2 = view.findViewById(R.id.chb_auto_tmz2);
        timezoneTextView2 = view.findViewById(R.id.timezoneTextView2);
        timezoneTextView2.setSelected(true);

        cityCountryLayout = view.findViewById(R.id.cityCountryLayout);
        gpsLayout = view.findViewById(R.id.gpsLayout);
        manualLayout = view.findViewById(R.id.manualLayout);
        cityEditText = view.findViewById(R.id.cityEditText);
        coordinatesTextView = view.findViewById(R.id.coordinatesTextView);
        latitudeEditText = view.findViewById(R.id.latitudeEditText);
        longitudeEditText = view.findViewById(R.id.longitudeEditText);
        Button btn_makeChart = view.findViewById(R.id.btn_makeChart);
        Button btn_getGPSLocation = view.findViewById(R.id.btn_getGPSLocation);
        Button btn_getCityLocation = view.findViewById(R.id.btn_getCityLocation);

        frc = initRemoteConfig(mCompat);
        fetchFrc(mCompat, frc, requestPermissionLauncher);
        mAdView = view.findViewById(R.id.adView);
        initAdView(mCompat, mAdView);

        loadSavedData();

        // Crear las opciones
        List<Option> op_chart = new ArrayList<>();
        op_chart.add(new Option(1, "Chart"));
        op_chart.add(new Option(2, "Transit"));
        /*op_chart.add(new Option(3, "2 chart"));
        op_chart.add(new Option(4, "Opción 4"));*/
        // Crear las opciones
        List<Option> op_geolocation = new ArrayList<>();
        op_geolocation.add(new Option(0, "Select"));
        op_geolocation.add(new Option(1, getString(R.string.radio_city_country)));
        op_geolocation.add(new Option(2, getString(R.string.radio_gps)));
        op_geolocation.add(new Option(3, getString(R.string.radio_manual)));
        // Crear un ArrayAdapter personalizado
        ArrayAdapter<Option> adapter_chart = new ArrayAdapter<>(mCompat, android.R.layout.simple_spinner_item, op_chart);
        adapter_chart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<Option> adapter_geolocation = new ArrayAdapter<>(mCompat, android.R.layout.simple_spinner_item, op_geolocation);
        adapter_geolocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type_chart.setAdapter(adapter_chart);
        sp_geolocation.setAdapter(adapter_geolocation);
        sp_type_chart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Option selectedOption = (Option) parent.getItemAtPosition(position);
                int selectedId = selectedOption.getId();
                // Aquí puedes usar el ID seleccionado como desees
                handleChartSelection(selectedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método requerido pero no utilizado en este caso
            }
        });
        if (chb_auto_tmz.isChecked()) {
            timezoneEditText.setVisibility(View.VISIBLE);
            timezoneTextView.setVisibility(View.GONE);
        } else {
            timezoneEditText.setVisibility(View.GONE);
            timezoneTextView.setVisibility(View.VISIBLE);
        }
        if (chb_auto_tmz2.isChecked()) {
            timezoneEditText2.setVisibility(View.VISIBLE);
            timezoneTextView2.setVisibility(View.GONE);
        } else {
            timezoneEditText2.setVisibility(View.GONE);
            timezoneTextView2.setVisibility(View.VISIBLE);
        }
        chb_auto_tmz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                timezoneEditText.setVisibility(View.VISIBLE);
                timezoneTextView.setVisibility(View.GONE);
            } else {
                timezoneEditText.setVisibility(View.GONE);
                timezoneTextView.setVisibility(View.VISIBLE);
            }
        });
        chb_auto_tmz2.setOnCheckedChangeListener((buttonView2, isChecked2) -> {
            if (isChecked2) {
                timezoneEditText2.setVisibility(View.VISIBLE);
                timezoneTextView2.setVisibility(View.GONE);
            } else {
                timezoneEditText2.setVisibility(View.GONE);
                timezoneTextView2.setVisibility(View.VISIBLE);
            }
        });
        sp_geolocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Option selectedOption = (Option) parent.getItemAtPosition(position);
                int selectedId = selectedOption.getId();
                // Aquí puedes usar el ID seleccionado como desees
                selectedGeolocationMethod = selectedId;
                handleGeolocationSelection(selectedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método requerido pero no utilizado en este caso
            }
        });

        sp_saved_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ChartEntry selectedEntry = (ChartEntry) parent.getItemAtPosition(position);

                if (selectedEntry.getId() > 0) {
                    int selectedChartType = selectedEntry.getChartType();
                    nameSurnameEditText.setText(selectedEntry.getNombre());
                    nameSurname = selectedEntry.getNombre();
                    handleChartSelection(selectedEntry.getChartType());
                    if (selectedEntry.getChartType() == 1) {
                        if (selectedEntry.getFecha1() != null) {
                            dateOfBirthEditText.setText(LocalDateTime.parse(selectedEntry.getFecha1(),outputFormatter).format(dateTimeFormatter));
                            birthDate = LocalDateTime.parse(LocalDateTime.parse(selectedEntry.getFecha1(),outputFormatter).format(dateTimeFormatter),dateTimeFormatter);
                            log("e", "data", "birthDateLocal " + birthDate);
                        }
                        timezoneEditText.setEnabled(true);
                        timezoneEditText.setText(selectedEntry.getTimezone1());
                    }
                    if (selectedEntry.getChartType() == 2) {
                        log("e", "data", "selectedEntry.getFecha1() " + selectedEntry.getFecha1());
                        log("e", "data", "selectedEntry.getFecha2() " + selectedEntry.getFecha2());
                        if (selectedEntry.getFecha1() != null) {
                            dateOfBirthEditText.setText(LocalDateTime.parse(selectedEntry.getFecha1(),outputFormatter).format(dateTimeFormatter));
                            birthDate = LocalDateTime.parse(LocalDateTime.parse(selectedEntry.getFecha1(),outputFormatter).format(dateTimeFormatter),dateTimeFormatter);
                            log("e", "data", "birthDate " + birthDate);
                        }
                        if (selectedEntry.getFecha2() != null) {
                            dateOfBirthEditText2.setText(LocalDateTime.parse(selectedEntry.getFecha2(),outputFormatter).format(dateTimeFormatter));
                            birthDate2 = LocalDateTime.parse(LocalDateTime.parse(selectedEntry.getFecha2(),outputFormatter).format(dateTimeFormatter),dateTimeFormatter);
                            log("e", "data", "birthDate2 " + birthDate2);
                        }
                        timezoneEditText.setEnabled(true);
                        timezoneEditText2.setEnabled(true);
                        timezoneEditText.setText(selectedEntry.getTimezone1());
                        timezoneEditText2.setText(selectedEntry.getTimezone2());
                    }
                    handleGeolocationSelection(selectedEntry.getLocationType());
                    sp_geolocation.setSelection(selectedEntry.getLocationType());
                    if (selectedEntry.getLocationType() == 1) {
                        if (!selectedEntry.getCiudadPais().isEmpty() && selectedEntry.getCiudadPais().contains(",")) {
                            cityEditText.setText(selectedEntry.getCiudadPais());
                            // Separar la ciudad y el país
                            String[] parts = selectedEntry.getCiudadPais().split(",");
                            cityName = parts[0].trim();
                            countryName = parts[1].trim();

                            // Utilizar el Geocoder para obtener las coordenadas
                            List<Address> addresses = getCoordinates(mCompat,cityName, countryName);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                latitude = address.getLatitude();
                                longitude = address.getLongitude();
                                String res = address.getLatitude() + "," + address.getLongitude();
                                coordinatesTextView.setText(res);
                            } else {
                                coordinatesTextView.setText(getString(R.string.location_unable_to_geocode));
                                showNotFoundDialog(mCompat, getString(R.string.location_unable_to_geocode));
                                Toast.makeText(mCompat, getString(R.string.location_unable_to_geocode), Toast.LENGTH_SHORT).show();
                                log("e", "data", getString(R.string.location_unable_to_geocode));
                            }
                            /*if (addresses == null || addresses.isEmpty()) {
                                String message = String.format(getString(R.string.location_not_found), cityName + "," +countryName);
                                showNotFoundDialog(mCompat, message);
                            }*/
                        } else {
                            // No se ingresó una ciudad y país válidos
                            coordinatesTextView.setText(getString(R.string.location_unable_to_geocode));
                        }
                    }
                    if (selectedEntry.getLocationType() == 2) {
                        if (!selectedEntry.getLocation().isEmpty() && selectedEntry.getLocation().contains(",")) {
                            coordinatesTextView.setText(selectedEntry.getLocation());
                            String[] parts = selectedEntry.getLocation().split(",");
                            latitude = Double.parseDouble(parts[0].trim());
                            longitude = Double.parseDouble(parts[1].trim());
                            if (latitude >= -90 && latitude <= 90) {
                            } else {
                                latitude = -100;
                            }
                            if (longitude >= -180 && longitude <= 180) {
                            } else {
                                longitude=-200;
                            }
                        }
                    }
                    if (selectedEntry.getLocationType() == 3) {
                        if (!selectedEntry.getLocation().isEmpty() && selectedEntry.getLocation().contains(",")) {
                            coordinatesTextView.setText(selectedEntry.getLocation());
                            String[] parts = selectedEntry.getLocation().split(",");
                            latitude = Double.parseDouble(parts[0].trim());
                            longitude = Double.parseDouble(parts[1].trim());
                            if (latitude >= -90 && latitude <= 90) {
                                latitudeEditText.setText(String.valueOf(latitude));
                            } else {
                                latitude = -100;
                            }
                            if (longitude >= -180 && longitude <= 180) {
                                longitudeEditText.setText(String.valueOf(longitude));
                            } else {
                                longitude=-200;
                            }
                        }

                    }
                    int positionToSelecthouse = getPositionForItem(houseSystemSpinner, selectedEntry.getHouse());
                    if (positionToSelecthouse != -1) {
                        houseSystemSpinner.setSelection(positionToSelecthouse);
                    }
                    int positionToSelectZodiac = getPositionForItem(siderealModeSpinner, selectedEntry.getZodiacal());
                    if (positionToSelectZodiac != -1) {
                        siderealModeSpinner.setSelection(positionToSelectZodiac);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método requerido pero no utilizado en este caso
            }
        });


        LocalDateTime currentDateTime = LocalDateTime.of(1983,2,27,5,30);
        currentDateTime = LocalDateTime.now();
        selectedDate = currentDateTime.toLocalDate();
        selectedTime = currentDateTime.toLocalTime();
        selectedDate2 = currentDateTime.toLocalDate();
        selectedTime2 = currentDateTime.toLocalTime();

        /*latitudeEditText.setOnFocusChangeListener((v, hasFocus) -> {
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
        });*/
        latitudeEditText.addTextChangedListener(latitudeTextWatcher(coordinatesTextView));
        longitudeEditText.addTextChangedListener(longitudeTextWatcher(coordinatesTextView));

        houseSystemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                house = houseSystemSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        siderealModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zodiac = siderealModeSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        zodiac = siderealModeSpinner.getSelectedItem().toString();
        house = houseSystemSpinner.getSelectedItem().toString();
        log("e", "data", "siderealModeSpinner id " + siderealModeSpinner.getId());
        log("e", "data", "houseSystemSpinner id " + houseSystemSpinner.getId());

        checkPermissions(mCompat, requestPermissionLauncher, permissionsStorage());
        checkPermissions(mCompat, requestPermissionLauncher, permissionsPost());

        dateOfBirthEditText.setOnClickListener(v -> {
            // Mostrar el diálogo de selección de fecha
            DatePickerDialog datePickerDialog = new DatePickerDialog(mCompat, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view12, year, month, dayOfMonth) -> {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                // Mostrar el diálogo de selección de hora
                TimePickerDialog timePickerDialog = new TimePickerDialog(mCompat, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view1, hourOfDay, minute) -> {
                    selectedTime = LocalTime.of(hourOfDay, minute);
                    updateDateTimeText();
                }, selectedTime.getHour(), selectedTime.getMinute(), false);

                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }, selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());

            datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            datePickerDialog.show();
        });
        timezoneEditText.setOnClickListener(v -> {
            NumberPicker.OnValueChangeListener npListener = (picker, oldVal, newVal) -> {
                timezoneEditText.setText(timeZones[newVal]);
            };
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(mCompat);
            numberPickerDialog.setMessage(mCompat.getString(R.string.hint_date_time_birth_timezone));
            numberPickerDialog.setMaxValue(timeZones.length-1);
            numberPickerDialog.setDisplayedValues(timeZones);
            numberPickerDialog.setValue((timeZones.length/2)-3);
            numberPickerDialog.setValueChangeListener(npListener); // Asignar el listener de valor a la instancia del diálogo
            numberPickerDialog.show(mCompat.getSupportFragmentManager(), "number_picker_dialog");

        });
        dateOfBirthEditText2.setOnClickListener(v -> {
            // Mostrar el diálogo de selección de fecha
            DatePickerDialog datePickerDialog = new DatePickerDialog(mCompat, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view13, year, month, dayOfMonth) -> {
                selectedDate2 = LocalDate.of(year, month + 1, dayOfMonth);
                // Mostrar el diálogo de selección de hora
                TimePickerDialog timePickerDialog = new TimePickerDialog(mCompat, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view131, hourOfDay, minute) -> {
                    selectedTime2 = LocalTime.of(hourOfDay, minute);
                    updateDateTimeText2();
                }, selectedTime2.getHour(), selectedTime2.getMinute(), false);

                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }, selectedDate2.getYear(), selectedDate2.getMonthValue() - 1, selectedDate2.getDayOfMonth());

            datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            datePickerDialog.show();
        });
        timezoneEditText2.setOnClickListener(v -> {
            NumberPicker.OnValueChangeListener npListener = (picker, oldVal, newVal) -> {
                timezoneEditText2.setText(timeZones[newVal]);
                /*log("e", "data", "OnValueChangeListener " + timeZones[newVal]);
                int[] time = getTimeFromTimeZone(timeZones[newVal]);
                if (birthDate2 != null ) {
                    ZoneOffset zoneOffset2 = ZoneOffset.ofHoursMinutes(time[0], time[1]);
                    OffsetDateTime offsetDateTime2 = birthDate2.atOffset(zoneOffset2);
                    log("e", "data", "local time " + offsetDateTime2.toString());
                    ZonedDateTime utcZoned2 = offsetDateTime2.atZoneSameInstant(ZoneId.of("UTC"));
                    log("e", "data","utc time " + utcZoned2.toString());
                    birthDateLocal2 = utcZoned2.toLocalDateTime();
                    log("e", "data","birthDateLocal time " + birthDateLocal2.toString());

                    //birthDate = parseDateTime(dateOfBirthEditText.getText().toString().trim());
                } else {
                    timezoneEditText2.setEnabled(false);
                }
*/
            };
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(mCompat);
            numberPickerDialog.setMessage(mCompat.getString(R.string.hint_date_time_birth_timezone));
            numberPickerDialog.setMaxValue(timeZones.length-1);
            numberPickerDialog.setDisplayedValues(timeZones);
            numberPickerDialog.setValue((timeZones.length/2)-3);
            numberPickerDialog.setValueChangeListener(npListener); // Asignar el listener de valor a la instancia del diálogo
            numberPickerDialog.show(mCompat.getSupportFragmentManager(), "number_picker_dialog");

        });
        btn_getCityLocation.setOnClickListener(v -> {

            if (cityEditText != null && !cityEditText.getText().toString().equals("")) {
                // Obtener los valores ingresados por el usuario
                String cityCountry = cityEditText.getText().toString().trim();

                // Verificar si se ingresó una ciudad y país separados por coma
                if (cityCountry.contains(",")) {
                    // Separar la ciudad y el país
                    String[] parts = cityCountry.split(",");
                    cityName = parts[0].trim();
                    countryName = parts[1].trim();

                    // Utilizar el Geocoder para obtener las coordenadas
                    List<Address> addresses = getCoordinates(mCompat,cityName, countryName);
                    if (addresses != null && !addresses.isEmpty()) {
                        log("e", "data",  " addresses:" + addresses.toString() +  "\n") ;
                        Address address = addresses.get(0);
                        if (isValidCoordinateLatitude(String.valueOf(address.getLatitude()))) {
                            latitude = address.getLatitude();
                        }
                        if (isValidCoordinateLongitude(String.valueOf(address.getLongitude()))){
                            longitude = address.getLongitude();
                        }
                        String res = address.getLatitude() + "," + address.getLongitude();
                        String tmz = TimezoneMapper.latLngToTimezoneString(latitude, longitude);
                        ZoneId zoneId = ZoneId.of(tmz);
                        TimeZone tz = TimeZone.getTimeZone(zoneId);
                        coordinatesTextView.setText(res);

                    } else {
                        coordinatesTextView.setText(getString(R.string.location_unable_to_geocode));
                        showNotFoundDialog(mCompat, getString(R.string.location_unable_to_geocode));
                        Toast.makeText(mCompat, getString(R.string.location_unable_to_geocode), Toast.LENGTH_SHORT).show();
                        log("e", "data", getString(R.string.location_unable_to_geocode));
                    }
                    /*assert addresses != null;
                    if (addresses.isEmpty()) {
                        String message = String.format(getString(R.string.location_not_found), cityName + "," +countryName);
                        showNotFoundDialog(mCompat, message);
                    }*/
                } else {
                    // No se ingresó una ciudad y país válidos
                    coordinatesTextView.setText(getString(R.string.location_unable_to_geocode));
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
                            String tmz = TimezoneMapper.latLngToTimezoneString(latitude, longitude);
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

        btn_makeChart.setOnClickListener(v -> {
            if (isValid()) {
                /*showNotFoundDialog(mCompat, getString(R.string.str_ads_title), getString(R.string.str_ads_msg), result1 -> {
                });*/
                if (!debugApp) {
                    if (frc.getBoolean("admob")) {
                        int ranDom = new Random().nextInt(3);
                        //ranDom = 2;
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
                    }
                }
                generateChart();
            } else {
                /*log("e", "data", " Missed Field ");
                String birth = "";
                if (birthDate != null) birth = birthDate.toString();
                log("e", "data", "house " + house);
                log("e", "data", "zodiac " + zodiac);
                log("e", "data", "nameSurname " + nameSurname);
                log("e", "data", "latitude " + latitude);
                log("e", "data", "longitude " + longitude);
                log("e", "data", "birthDate " + birth);*/
                showNotFoundDialog(mCompat, getString(R.string.str_fields_required));
            }

        });

        return view;
    }

    private TextWatcher latitudeTextWatcher(TextView coordinatesTextView){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se necesita implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCoordinateEditText(0, s.toString(), coordinatesTextView);
            }
        };
    }
    private TextWatcher longitudeTextWatcher(TextView coordinatesTextView){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se necesita implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCoordinateEditText(1, s.toString(), coordinatesTextView);
            }
        };
    }

    private void updateCoordinateEditText(int coord, String coordText, TextView coordinatesTextView) {
        String updatedCoordinatesText = coordinatesTextView.getText().toString();
       if (updatedCoordinatesText.contains(",")) {
            String[] parts = updatedCoordinatesText.split(",");
            if (parts.length == 2) {
                String existingLatitude = parts[0];
                String existingLongitude = parts[1];

                if (coord == 0) {
                    if (coordText.length() > 0 && isValidCoordinateLatitude(coordText)) {
                        updatedCoordinatesText = coordText + ", " + existingLongitude;
                    }  else {
                        updatedCoordinatesText = ", " + existingLongitude;
                    }
                }
                if (coord == 1) {
                    if (coordText.length() > 0 && isValidCoordinateLongitude(coordText)) {
                        updatedCoordinatesText = existingLatitude + ", " + coordText;
                    }  else {
                        updatedCoordinatesText = existingLatitude + ", ";
                    }
                }
            }
        } else {
            Log.e("data", " coordText " + coordText + " isValidCoordinateLatitude " + isValidCoordinateLatitude(coordText));
            if (coord == 0) {
                if (coordText.length() > 0 && isValidCoordinateLatitude(coordText)) {
                    updatedCoordinatesText = coordText + ", ";
                }  else {
                    updatedCoordinatesText = ", ";
                }
            }
            if (coord == 1) {
                if (coordText.length() > 0 && isValidCoordinateLongitude(coordText)) {
                    updatedCoordinatesText = ", " + coordText;
                }  else {
                    updatedCoordinatesText = ", ";
                }
            }

        }
        coordinatesTextView.setText(updatedCoordinatesText);
    }
    private boolean isValidCoordinateLatitude(String coordinate) {
        Log.e("data", "isValidCoordinateLatitude " + coordinate +" isValidCoordinateLatitude matches " + coordinate.matches("-?\\d+(\\.\\d*)?"));
        if (coordinate.matches("-?\\d*\\.?\\d*")) {
            if (!coordinate.equals("-")){
                double value = Double.parseDouble(coordinate);
                Log.e("data", "isValidCoordinateLatitude " + (value >= -90.0 && value <= 90.0));
                return value >= -90.0 && value <= 90.0;
            } else {
                return true;
            }
        } else {
            showNotFoundDialog(mCompat, getString(R.string.str_invalid_latitud_format));
            return false;
        }
    }
    private boolean isValidCoordinateLongitude(String coordinate) {
        Log.e("data", "isValidCoordinateLongitude " + coordinate + " isValidCoordinateLongitude matches " + coordinate.matches("-?\\d+(\\.\\d*)?"));
        if (coordinate.matches("-?\\d*\\.?\\d*")) {
            if (!coordinate.equals("-")){
                double value = Double.parseDouble(coordinate);
                Log.e("data", "isValidCoordinateLongitude " + (value >= -180.0 && value <= 180.0));
                //showNotFoundDialog(mCompat, getString(R.string.str_invalid_latitud_range));
                return value >= -180.0 && value <= 180.0;
            } else {
                return true;
            }
        } else {
            showNotFoundDialog(mCompat, getString(R.string.str_invalid_latitud_format));
            return false;
        }
    }
    private boolean isValid() {
        if (isdataValid(nameSurnameEditText,mCompat)) {
            nameSurname = nameSurnameEditText.getText().toString().trim();
        } else {
            log("e", "data", "nameSurnameEditText " + isdataValid(nameSurnameEditText,mCompat));
            log("e", "data", "nameSurname " + nameSurname);
            return false;
        }

        if (selectedChartMethod == 0 ) {
            log("e", "data", "selectedChartMethod " + selectedChartMethod);
            return false;
        }
        if (selectedGeolocationMethod == 0) {
            log("e", "data", "selectedGeolocationMethod " + selectedGeolocationMethod);
            return false;
        }

        if (selectedChartMethod == 1 ) {
            if (!isdataValid(dateOfBirthEditText,mCompat)) {
                log("e", "data", "dateOfBirthEditText " + isdataValid(dateOfBirthEditText,mCompat));
                return false;
            }
            if (!isdataValid(timezoneEditText,mCompat)) {
                log("e", "data", "timezoneEditText " + isdataValid(timezoneEditText,mCompat));
                return false;
            }
        }
        if (selectedChartMethod == 2 ) {
            if (!isdataValid(dateOfBirthEditText,mCompat)) {
                log("e", "data", "dateOfBirthEditText " + isdataValid(dateOfBirthEditText,mCompat));
                return false;
            }
            if (!isdataValid(timezoneEditText,mCompat)) {
                log("e", "data", "timezoneEditText " + isdataValid(timezoneEditText,mCompat));
                return false;
            }
            if (!isdataValid(dateOfBirthEditText2,mCompat)) {
                log("e", "data", "dateOfBirthEditText2 " + isdataValid(dateOfBirthEditText2,mCompat));
                return false;
            }
            if (!isdataValid(timezoneEditText2,mCompat)) {
                log("e", "data", "timezoneEditText2 " + isdataValid(timezoneEditText2,mCompat));
                return false;
            }
        }

        if (selectedGeolocationMethod == 1 ) {
            if (!isdataValid(cityEditText,mCompat)) {
                log("e", "data", "cityEditText " + isdataValid(cityEditText,mCompat));
                return false;
            }

        }
        if (selectedGeolocationMethod == 2 ) {
            if (!isdataValid(coordinatesTextView,mCompat)) {
                log("e", "data", "coordinatesTextView " + isdataValid(coordinatesTextView,mCompat));
                return false;
            }
        }
        if (selectedGeolocationMethod == 3 ) {
            if (!isdataValid(latitudeEditText,mCompat)) {
                log("e", "data", "latitudeEditText " + isdataValid(latitudeEditText,mCompat));
                return false;
            }
            if (!isdataValid(longitudeEditText,mCompat)) {
                log("e", "data", "longitudeEditText " + isdataValid(longitudeEditText,mCompat));
                return false;
            }
            if (!isdataValid(coordinatesTextView,mCompat)) {
                log("e", "data", "coordinatesTextView " + isdataValid(coordinatesTextView,mCompat));
                return false;
            }
        }
        if (!isdataValid(coordinatesTextView,mCompat) && coordinatesTextView.getText().toString().equals(getString(R.string.location_unable_to_geocode))) {
            log("e", "data", "cityCoordinates " + isdataValid(coordinatesTextView,mCompat));
            coordinatesTextView.setError(mCompat.getString(R.string.mandatory_field));
            return false;
        } else {
            if (coordinatesTextView.getText().toString().contains(",")) {
                String[] parts = coordinatesTextView.getText().toString().split(",");
                if (parts.length == 2) {
                    String existingLatitude = parts[0];
                    String existingLongitude = parts[1];

                    if (isValidCoordinateLatitude(existingLatitude)) {
                        latitude = Double.parseDouble(existingLatitude);
                    }
                    if (isValidCoordinateLongitude(existingLongitude)) {
                        longitude = Double.parseDouble(existingLongitude);
                    }
                }
            }
        }

        if (nameSurname.equals("")) {
            log("e", "data", "nameSurname " + nameSurname);
            return false;
        }
        if (!isValidCoordinateLatitude(String.valueOf(latitude))) {
            log("e", "data", "latitude " + latitude);
            return false;
        }
        if (!isValidCoordinateLongitude(String.valueOf(longitude))) {
            log("e", "data", "longitude " + longitude);
            return false;
        }
        if (zodiac.equals("")) {
            log("e", "data", "zodiac " + zodiac);
            return false;
        }
        if (house.equals("")) {
            log("e", "data", "house " + house);
            return false;
        }
        return true;
    }
    private LocalDateTime getNewUTCDate(String tmz, String DateTime){

        int[] time = getTimeFromTimeZone(tmz);
        ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(time[0], time[1]);
        OffsetDateTime offsetDateTime = LocalDateTime.parse(DateTime,dateTimeFormatter).atOffset(zoneOffset);
        log("e", "data", "local time " + offsetDateTime.toString());
        ZonedDateTime utcZoned = offsetDateTime.atZoneSameInstant(ZoneId.of("UTC"));
        log("e", "data","utc time " + utcZoned.toString());
        return utcZoned.toLocalDateTime();
/*
        log("e", "data", "OnValueChangeListener " + tmz);
        int[] time = getTimeFromTimeZone(tmz);
        if (birthDate != null ) {
            ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(time[0], time[1]);
            OffsetDateTime offsetDateTime = birthDate.atOffset(zoneOffset);
            log("e", "data", "local time " + offsetDateTime.toString());
            ZonedDateTime utcZoned = offsetDateTime.atZoneSameInstant(ZoneId.of("UTC"));
            log("e", "data","utc time " + utcZoned.toString());
            birthDateLocal = utcZoned.toLocalDateTime();
            log("e", "data","birthDateLocal time " + birthDateLocal.toString());

            //birthDate = parseDateTime(dateOfBirthEditText.getText().toString().trim());
        } else {
            timezoneEditText.setEnabled(false);
        }*/
    }
    private void generateChart() {

        // Verifica si el nombre existe en selectedEntry
        boolean entryExists = checkIfEntryExists(mCompat, nameSurname);
        if (!entryExists) {
            DialogInterface.OnClickListener dialogOk = (dialog, id) -> {
                DialogInterface.OnClickListener dialogOk1 = (dialog1, id1) -> {
                    loadSavedData();
                    double[] loc= {longitude,latitude, 0.0};
                    if (selectedChartMethod == 1) {
                        openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, null, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                    }
                    if (selectedChartMethod == 2) {
                        openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, birthDateLocal2, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                    }
                    resetValues();
                };
                saveDataToDatabase(dialogOk1);
            };
            DialogInterface.OnClickListener dialogCancel = (dialog, id) -> {
                double[] loc= {longitude,latitude, 0.0};
                if (selectedChartMethod == 1) {
                    openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, null, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                }
                if (selectedChartMethod == 2) {
                    openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, birthDateLocal2, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                }resetValues();
            };
            // Los datos son nuevos, pregunta si se desea guardar
            showConfirmationDialog(mCompat, dialogOk, dialogCancel, mCompat.getString(R.string.btn_ok),mCompat.getString(R.string.btn_cancel),mCompat.getString(R.string.str_save_msg));
        } else {
            DialogInterface.OnClickListener dialogOk = (dialog, id) -> {
                DialogInterface.OnClickListener dialogOk1 = (dialog1, id1) -> {
                    loadSavedData();
                    double[] loc= {longitude,latitude, 0.0};
                    if (selectedChartMethod == 1) {
                        openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, null, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                    }
                    if (selectedChartMethod == 2) {
                        openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, birthDateLocal2, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                    }resetValues();
                };
                updateDataInDatabase(nameSurname, dialogOk1);
            };
            DialogInterface.OnClickListener dialogCancel = (dialog, id) -> {
                double[] loc= {longitude,latitude, 0.0};
                if (selectedChartMethod == 1) {
                    openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, null, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                }
                if (selectedChartMethod == 2) {
                    openfragment(mCompat, new fragment_chart(mCompat, selectedChartMethod, birthDateLocal, birthDateLocal2, house, zodiac, nameSurnameEditText.getText().toString(), loc, timezoneEditText.getText().toString()), R.id.fcv_setting, null);
                }
                resetValues();
            };
            // Los datos ya existen, pregunta si se desea actualizar
            showConfirmationDialog(mCompat, dialogOk, dialogCancel,mCompat.getString(R.string.str_btn_update),mCompat.getString(R.string.btn_cancel),mCompat.getString(R.string.str_update_msg));
        }

        if (selectedChartMethod == 1 ) {
            birthDateLocal = getNewUTCDate(timezoneEditText.getText().toString(), dateOfBirthEditText.getText().toString());
        }
        if (selectedChartMethod == 2 ) {
            birthDateLocal = getNewUTCDate(timezoneEditText.getText().toString(), dateOfBirthEditText.getText().toString());
            birthDateLocal2 = getNewUTCDate(timezoneEditText2.getText().toString(), dateOfBirthEditText2.getText().toString());
        }


    }
    private void resetValues() {
        house = "";
        nameSurname = "";
        zodiac = "";
        longitude = -200;
        latitude = -100;
        cityName = "";
        countryName = "";
        sp_saved_data.setSelection(0);
        houseSystemSpinner.setSelection(0);
        siderealModeSpinner.setSelection(0);
        sp_geolocation.setSelection(0);
        sp_type_chart.setSelection(0);
        handleChartSelection(0);
        handleGeolocationSelection(0);
        coordinatesTextView.setText("");
        cityEditText.setText("");
        nameSurnameEditText.setText("");
        dateOfBirthEditText.setText("");
        dateOfBirthEditText2.setText("");
        timezoneEditText.setText("");
        timezoneEditText2.setText("");
        birthDateLocal= null;
        birthDateLocal2= null;
        birthDate= null;
        birthDate2= null;
        selectedTime= null;
        selectedTime2= null;
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {

        } else {

        }
    });

    private void handleChartSelection(int checkedId) {
        if (checkedId == 2) {
            selectedChartMethod = 2;
            expandView(mCompat, ll_second_date);
            expandView(mCompat, ll_second_tmz);
        } else if (checkedId == 1) {
            selectedChartMethod = 1;
            collapseView(mCompat, ll_second_date);
            collapseView(mCompat, ll_second_tmz);
        } else if (checkedId == 0) {
            selectedChartMethod = 0;
            collapseView(mCompat, ll_second_date);
            collapseView(mCompat, ll_second_tmz);
        }
    }
    private void handleGeolocationSelection(int checkedId) {
        if (checkedId == 2) {
            selectedGeolocationMethod = 2;
            expandView(mCompat, gpsLayout);
            collapseView(mCompat, cityCountryLayout);
            collapseView(mCompat, manualLayout);
        } else if (checkedId == 1) {
            selectedGeolocationMethod = 1;
            collapseView(mCompat, gpsLayout);
            expandView(mCompat, cityCountryLayout);
            collapseView(mCompat, manualLayout);
        } else if (checkedId == 3) {
            selectedGeolocationMethod = 3;
            collapseView(mCompat, gpsLayout);
            collapseView(mCompat, cityCountryLayout);
            expandView(mCompat, manualLayout);
        } else if (checkedId == 0) {
            selectedGeolocationMethod = 0;
            collapseView(mCompat, gpsLayout);
            collapseView(mCompat, cityCountryLayout);
            collapseView(mCompat, manualLayout);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        /*resetValues();*/
        fetchFrc(mCompat, frc, requestPermissionLauncher);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        resetValues();
        if (mAdView!= null) mAdView.destroy();
    }
    private void loadSavedData() {

        List<ChartEntry> chartEntries = loadData(mCompat);

        if (chartEntries.size() > 1){
            ArrayAdapter<ChartEntry> adapter = new ArrayAdapter<>(mCompat, android.R.layout.simple_spinner_item, chartEntries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_saved_data.setAdapter(adapter);
        } else {
            List<ChartEntry> emptyEntries = new ArrayList<>();
            ArrayAdapter<ChartEntry> adapter = new ArrayAdapter<>(mCompat, android.R.layout.simple_spinner_item, emptyEntries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_saved_data.setAdapter(adapter);
        }
    }

    private void saveDataToDatabase(DialogInterface.OnClickListener dialogOk) {
        ContentValues values = new ContentValues();
        // Agrega los nuevos valores a las columnas correspondientes
        values.put(COLUMN_NOMBRE, nameSurnameEditText.getText().toString());
        values.put(COLUMN_CHART_TYPE, selectedChartMethod);
        if (selectedChartMethod == 1 ) {
            log("e", "data", "birthDateLocal " + birthDate.format(outputFormatter));
            values.put(COLUMN_FECHA1, birthDate.format(outputFormatter));
            values.put(COLUMN_TIMEZONE1, timezoneEditText.getText().toString());
            values.put(COLUMN_FECHA2, "");
            values.put(COLUMN_TIMEZONE2, "");
        }
        if (selectedChartMethod == 2 ) {
            log("e", "data", "birthDateLocal " + birthDate.format(outputFormatter));
            log("e", "data", "birthDateLocal " + birthDate2.format(outputFormatter));
            values.put(COLUMN_FECHA1, birthDate.format(outputFormatter));
            values.put(COLUMN_TIMEZONE1, timezoneEditText.getText().toString());
            values.put(COLUMN_FECHA2, birthDate2.format(outputFormatter));
            values.put(COLUMN_TIMEZONE2, timezoneEditText2.getText().toString());
        }
        values.put(COLUMN_LOCATION_TYPE, selectedGeolocationMethod);
        if (selectedGeolocationMethod == 1 ) {
            values.put(COLUMN_CIUDADPAIS, cityEditText.getText().toString());
            values.put(COLUMN_LOCATION, coordinatesTextView.getText().toString());
        }
        if (selectedGeolocationMethod == 2 ) {
            values.put(COLUMN_CIUDADPAIS, "");
            values.put(COLUMN_LOCATION, coordinatesTextView.getText().toString());
        }
        if (selectedGeolocationMethod == 3 ) {
            values.put(COLUMN_CIUDADPAIS, "");
            values.put(COLUMN_LOCATION, coordinatesTextView.getText().toString());
        }
        values.put(COLUMN_HOUSE, house);
        values.put(COLUMN_ZODIACAL, zodiac);

        saveData(mCompat, dialogOk, values);
    }
    private void updateDataInDatabase(String name, DialogInterface.OnClickListener dialogOk) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_CHART_TYPE, selectedChartMethod);
        if (selectedChartMethod == 1 ) {
            log("e", "data", "birthDateLocal " + birthDate.format(outputFormatter));
            values.put(COLUMN_FECHA1, birthDate.format(outputFormatter));
            values.put(COLUMN_TIMEZONE1, timezoneEditText.getText().toString());
            values.put(COLUMN_FECHA2, "");
            values.put(COLUMN_TIMEZONE2, "");
        }
        if (selectedChartMethod == 2 ) {
            log("e", "data", "birthDateLocal " + birthDate.format(outputFormatter));
            log("e", "data", "birthDateLocal " + birthDate2.format(outputFormatter));
            values.put(COLUMN_FECHA1, birthDate.format(outputFormatter));
            values.put(COLUMN_TIMEZONE1, timezoneEditText.getText().toString());
            values.put(COLUMN_FECHA2, birthDate2.format(outputFormatter));
            values.put(COLUMN_TIMEZONE2, timezoneEditText2.getText().toString());
        }
        values.put(COLUMN_LOCATION_TYPE, selectedGeolocationMethod);
        if (selectedGeolocationMethod == 1 ) {
            values.put(COLUMN_CIUDADPAIS, cityEditText.getText().toString());
            values.put(COLUMN_LOCATION, coordinatesTextView.getText().toString());
        }
        if (selectedGeolocationMethod == 2 ) {
            values.put(COLUMN_CIUDADPAIS, "");
            values.put(COLUMN_LOCATION, coordinatesTextView.getText().toString());
        }
        if (selectedGeolocationMethod == 3 ) {
            values.put(COLUMN_CIUDADPAIS, "");
            values.put(COLUMN_LOCATION, coordinatesTextView.getText().toString());
        }
        values.put(COLUMN_HOUSE, house);
        values.put(COLUMN_ZODIACAL, zodiac);
        updateData(mCompat, dialogOk, values, name);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_input_menu, menu); // Reemplaza "my_menu" con el ID de tu archivo de recursos de menú
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.btn_del) {
            DialogInterface.OnClickListener dialogOk = (dialog, id) -> {
                DialogInterface.OnClickListener dialogOkDel = (dialog1, id1) -> {loadSavedData();};
                delData(mCompat, dialogOkDel);
            };
            DialogInterface.OnClickListener dialogCancel = (dialog, id) -> { };
            showConfirmationDialog(mCompat, dialogOk, dialogCancel,mCompat.getString(R.string.btn_ok),mCompat.getString(R.string.btn_cancel),mCompat.getString(R.string.str_delete_msg));

            return true;
        } else if (itemId == R.id.btn_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=" + mCompat.getPackageName();
            String shareSub = "App link";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share App Link Via :"));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void updateDateTimeText() {
        if (selectedDate != null && selectedTime != null) {
            birthDate = LocalDateTime.of(selectedDate, selectedTime);
            dateOfBirthEditText.setText(birthDate.format(dateTimeFormatter));

            timezoneEditText.setEnabled(true);
            //birthDate = parseDateTime(dateOfBirthEditText.getText().toString().trim());
        }
    }
    private void updateDateTimeText2() {
        if (selectedDate2 != null && selectedTime2 != null) {
            birthDate2 = LocalDateTime.of(selectedDate2, selectedTime2);
            dateOfBirthEditText2.setText(birthDate2.format(dateTimeFormatter));
            timezoneEditText2.setEnabled(true);
            //birthDate = parseDateTime(dateOfBirthEditText.getText().toString().trim());
        }
    }


}