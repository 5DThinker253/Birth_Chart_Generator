package com.ejs.birthchart.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ejs.birthchart.R;

public class NumberPickerDialog extends DialogFragment {
    private Context context;
    private NumberPicker.OnValueChangeListener valueChangeListener;
    private String[] DisplayedValues = {""};
    private int maxValue = 0, setValue = 0;
    private String title = "", msg = "";

    public NumberPickerDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_number_picker, null);

        final ImageView iv_tmz_up = view.findViewById(R.id.iv_tmz_up);
        final ImageView iv_tmz_down = view.findViewById(R.id.iv_tmz_down);
        final TextView tv_msg = view.findViewById(R.id.tv_msg);
        final Button btn_ok = view.findViewById(R.id.btn_ok);
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);
        tv_msg.setText(getMessage());
        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);// Obtiene el drawable del divisor
        /*Drawable dividerDrawable = numberPicker.getDividerDrawable();

        // Ajusta el color del divisor
        dividerDrawable.setColorFilter(context.getColor(R.color.dialog_divider_color), PorterDuff.Mode.SRC_IN);

        // Establece el drawable del divisor actualizado en el NumberPicker
        numberPicker.setDividerDrawable(dividerDrawable);*/
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(getMaxValue());
        numberPicker.setDisplayedValues(getDisplayedValues());
        numberPicker.setValue(getValue());
        //numberPicker.setDividerColor(context.getColor(R.color.dialog_divider_color));
        //disable soft keyboard
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //set wrap true or false, try it you will know the difference
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(getValueChangeListener());
        numberPicker.setFocusableInTouchMode(true);
        iv_tmz_up.setOnClickListener(v -> {
            numberPicker.setValue(numberPicker.getValue()-1);
        });
        iv_tmz_down.setOnClickListener(v -> {
            numberPicker.setValue(numberPicker.getValue()+1);
        });
        btn_ok.setOnClickListener(v -> {
            valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            dismiss();
        });
        btn_cancel.setOnClickListener(v -> {
            valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            dismiss();
        });
        return new AlertDialog.Builder(context, R.style.CustomDialog).setView(view).create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }
    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
    public int getMaxValue() {
        return maxValue;
    }

    public void setDisplayedValues(String[] DisplayedValues) {
        this.DisplayedValues = DisplayedValues;
    }
    public String[] getDisplayedValues() {
        return DisplayedValues;
    }

    public void setValue(int setValue) {
        this.setValue = setValue;
    }
    public int getValue() {
        return setValue;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setMessage(String msg) {
        this.msg = msg;
    }
    public String getMessage() {
        return msg;
    }
}