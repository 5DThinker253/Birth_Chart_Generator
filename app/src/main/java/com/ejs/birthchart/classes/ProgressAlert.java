package com.ejs.birthchart.classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ejs.birthchart.R;

public class ProgressAlert {
    public static ProgressAlert customProgress = null;
    private static ProgressBar mProgressBar;
    private static TextView tv_title, tv_msg, tv_start, tv_end, tv_sizes;
    private static Dialog mDialog;


    /**
     *
     * @param context get context
     * @param loading False loading inderterminated layout, true upload layout
     * @return return instance ProgressAlert
     */
    public static ProgressAlert getInstance(Context context, boolean loading) {
        if (customProgress == null) {
            customProgress = new ProgressAlert();
        }
        mDialog = new Dialog(context);
        // no tile for the dialog
        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        if (!loading){
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setContentView(R.layout.layout_dialog_upload);
            tv_title = mDialog.findViewById(R.id.tv_title);
            tv_end = mDialog.findViewById(R.id.tv_end);
            tv_start = mDialog.findViewById(R.id.tv_start);
            tv_sizes = mDialog.findViewById(R.id.tv_size);
            mProgressBar = mDialog.findViewById(R.id.progressBar2);
            tv_msg = mDialog.findViewById(R.id.tv_msg);
            tv_title.setVisibility(View.VISIBLE);
            tv_msg.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setContentView(R.layout.layout_dialog_loading);
            mProgressBar = mDialog.findViewById(R.id.progressBar2);
            tv_msg = mDialog.findViewById(R.id.tv_msg);
            mProgressBar.setIndeterminate(true);
            tv_msg.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        return customProgress;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }
    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void setCancelable(Boolean cancelable){
        mDialog.setCancelable(cancelable);
    }
    public void setCanceledOnTouchOutside(Boolean cancelable){
        mDialog.setCanceledOnTouchOutside(cancelable);
    }
    public void setMessage(String msg){
        tv_msg.setText(msg);
    }
    public void setTitle(String title){
        tv_title.setText(title);
    }
    public void setIndeterminate(Boolean bool){
        mProgressBar.setIndeterminate(bool);
    }

    /**
     * Sets the current progress to the specified value, optionally animating the visual position between the current and target values.
     * Animation does not affect the result of getProgress(), which will return the target value immediately after this method is called.
     * Params:
     * @param progress – the new progress value, between getMin() and getMax()
     */
    public void setProgress(int progress){
        mProgressBar.setProgress(progress);
    }

    /**
     * Sets the current progress to the specified value, optionally animating the visual position between the current and target values.
     * Animation does not affect the result of getProgress(), which will return the target value immediately after this method is called.
     * Params:
     * @param progress – the new progress value, between getMin() and getMax()
     * @param animate – true to animate between the current and target values or false to not animate
     */
    public void setProgress(long progress, boolean animate){
        mProgressBar.setProgress((int)progress, animate);
    }
    public void setMax(int max){
        mProgressBar.setMax(max);
        tv_end.setText(String.valueOf(max));
    }
    public void setMin(int min){
        mProgressBar.setMin(min);
        tv_start.setText(String.valueOf(min));
    }
    public void setSize(String size){
        tv_sizes.setText(size);
    }

    public void setprog(long min){
        tv_start.setText(String.valueOf(min) + "%");
    }
    public void setprog(String min){
        tv_start.setText(min);
    }
}
