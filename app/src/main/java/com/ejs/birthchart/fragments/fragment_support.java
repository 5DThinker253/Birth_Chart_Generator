package com.ejs.birthchart.fragments;

import static com.ejs.birthchart.utils.firebaseUtils.logEventAnalytics;
import static com.ejs.birthchart.utils.msg.logE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.ejs.birthchart.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class fragment_support extends Fragment {
    private final String tag = this.getClass().getSimpleName();
    private final String TAG = this.getClass().getSimpleName();
    private AppCompatActivity mCompat;
    private ImageButton ib_telegram, ib_whatsapp, ib_email;


    public fragment_support(AppCompatActivity mCompat) {
        this.mCompat = mCompat;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCompat.setTitle(mCompat.getString(R.string.str_settigs_advance));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        logEventAnalytics(mCompat, FirebaseAnalytics.Event.APP_OPEN, tag);

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
        ib_whatsapp=view.findViewById(R.id.ib_whatsapp);
        ib_telegram=view.findViewById(R.id.ib_telegram);
        ib_email=view.findViewById(R.id.ib_email);
        ib_whatsapp.setOnClickListener(v1 -> {
            logEventAnalytics(mCompat, "ib_whatsapp", "ib_whatsapp");
            if (isAppInstalled("com.whatsapp")){
                Uri uri = Uri.parse("smsto:" + "+584242920043");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, ""));

            } else {
                Toast.makeText(mCompat, getString(R.string.str_wsInstall), Toast.LENGTH_LONG).show();
            }
        });
        ib_telegram.setOnClickListener(v2 -> {
            logEventAnalytics(mCompat, "ib_telegram", "ib_telegram");
            if (isAppInstalled("org.telegram.messenger")){
                try {
                    Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                    telegramIntent.setData(Uri.parse("https://telegram.me/edmyjose"));
                    startActivity(telegramIntent);
                } catch (Exception e) {
                    // show error message
                }
            } else {
                Toast.makeText(mCompat, getString(R.string.str_tgInstall), Toast.LENGTH_LONG).show();
            }
        });
        ib_email.setOnClickListener(v3 -> {
            logEventAnalytics(mCompat, "Email", "email");
            final Intent emailIntent= new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "endermigue87@gmail.com"}); //Heare you can add the list of email address you have
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mail subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Mail content");
            try{
                startActivity(emailIntent);
            }catch(ActivityNotFoundException e){
                logE(TAG, e);

            }
        });

        return view;
    }
    public boolean isAppInstalled(String packageName) {
        try {
            mCompat.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
