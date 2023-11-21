package com.ejs.birthchart.classes;

import static com.ejs.birthchart.utils.msg.log;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ejs.birthchart.BuildConfig;
import com.ejs.birthchart.R;

public class newsDialogFragment extends DialogFragment {
    private static final String tag = "newsDialogFragment";
    private static final String PREF_NAME = "APP_NEWS";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final String DISABLED = "DISABLED";

    public static void show(Context context, FragmentManager fragmentManager) {
        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isDISABLED = sharedPreferences.getBoolean(DISABLED, false);

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
            new newsDialogFragment().show(fragmentManager, null);
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
        View view = getLayoutInflater().inflate( R.layout.layout_news, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.lbl_Ok, (dialog, which) -> dismiss())
                .setNegativeButton(R.string.never_show, (dialog, which) -> {
                    getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).apply();
                    log("e", tag, "DISABLED " + "true");
                    dismiss();
                }).create();
        return alertDialog;
    }
}
