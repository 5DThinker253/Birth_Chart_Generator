package com.ejs.birthchart.classes;

import static com.ejs.birthchart.utils.msg.log;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;

public class welcomeDialogFragment extends DialogFragment {
    private AppCompatActivity context;
    private static final String tag = "newsDialogFragment";
    private static final String PREF_NAME = "APP_NEWS";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final String DISABLED = "DISABLED";

    public static void show(AppCompatActivity context) {
        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isDISABLED = sharedPreferences.getBoolean(DISABLED, false);
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        int lastPromptTime = sharedPreferences.getInt(LAST_PROMPT, 0);
        if (lastPromptTime != BuildConfig.VERSION_CODE && !isDISABLED) {
            editor.putBoolean(DISABLED, false);
        }

        log("e", tag, "DISABLED " + sharedPreferences.getBoolean(DISABLED, false));
        if (!sharedPreferences.getBoolean(DISABLED, false)) {
            shouldShow = true;
        }

        log("e", tag, "shouldShow " + shouldShow);
        if (shouldShow) {
            new welcomeDialogFragment().show(fragmentManager, "welcomeDialogFragment");
        } else {
            editor.apply();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate( R.layout.layout_welcome, null);
        final Button btn_ok = view.findViewById(R.id.btn_ok);
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(v -> dismiss());
        btn_cancel.setOnClickListener(v -> {
            getSharedPreferences(requireActivity()).edit().putBoolean(DISABLED, true).apply();
            log("e", tag, "DISABLED " + "true");
            dismiss();
        });
        return new AlertDialog.Builder(requireActivity(), R.style.CustomDialog)
                .setView(view).create();
    }
}
