package com.ejs.birthchart.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class msg {

    /**
     *
     * @param type Type msg
     * @param TAG TAG
     * @param msg message String
     */
    public static void log(String type, String TAG, String msg){
        if (!msg.equals("") || msg != null){
            if (type.equals("e")) Log.e(TAG, msg);
        }
    }

    public static void logE(String TAG, Throwable msg){
        Log.e(TAG, "Error: ", msg);
    }

    /**
     *
     * @param context get app context
     * @param type Type msg
     * @param msg message String
     */
    public static void toast(Context context, String type, final String msg ){

        if (msg != null){
            if (!msg.equals("")){
                if (type.equals("e")){
                    Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show();
                    //((Activity)context).runOnUiThread(() -> Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show());
                }
                if (type.equals("i")){
                    //Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show();
                    //((Activity)context).runOnUiThread(() -> Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show());
                }
                if (type.equals("sql")){
                    //Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show();
                    //((Activity)context).runOnUiThread(() -> Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show());
                }
                if (type.equals("else")){
                    //Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show();
                    //((Activity)context).runOnUiThread(() -> Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show());
                }
                if (type.equals("validate")){
                    //Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show();
                    //((Activity)context).runOnUiThread(() -> Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show());
                }
                if (type.equals("permission")){
                    //Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show();
                    //((Activity)context).runOnUiThread(() -> Toast.makeText(((Activity)context), msg, Toast.LENGTH_SHORT).show());
                }
            }
        }
    }
}
