package com.ejs.birthchart.classes;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class textWatcher implements TextWatcher {
    private boolean isDeleting=false;
    private boolean isRunning=false;
    private boolean isWrongDate=false;
    private boolean isWrongYear=false;
    private boolean isWrongMonth=false;
    private EditText et_Seldate;

    public textWatcher(EditText et_Seldate){
        this.et_Seldate = et_Seldate;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                /*Log.e("beforeTextChanged","-->"+charSequence);
                Log.e("start",""+start);
                Log.e("after",""+after);
                Log.e("count",""+count);*/
        isDeleting = count > after;

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        String text=charSequence.toString();
                /*Log.e("onTextChanged","-->"+charSequence);
                Log.e("start1",""+start);
                Log.e("before1",""+before);
                Log.e("count1",""+count);
                Log.e("isDeleting ",""+isDeleting);*/
        char subChar = 'T';
        if(text.length()>0){
            subChar=text.charAt(text.length()-1);
            //Log.e("LastChar","-->"+subChar);
        }

        if(isDeleting){
            return;
        }
        if(text.length()==1){
            return;
        }
        if(text.length()==4){
            return;
        }

        if(subChar=='/'){
            return;
        }
        if(charSequence.length()==2){
            int date=Integer.parseInt(String.valueOf(charSequence));
            if(date<1 || date >31){
                et_Seldate.setError("Please enter correct date");
                isWrongDate=true;
                return;
            }
            isWrongDate=false;
            isDeleting=false;
            charSequence=charSequence+"/";
            et_Seldate.setText(charSequence);
            isRunning=true;
            et_Seldate.setSelection(et_Seldate.getText().length());
            isDeleting=true;
        }

        if(text.length()==5){
            String month=text.substring(3,5);
            //Log.e("Month","-->"+month);
            int monthVal=Integer.parseInt(month);
            if(monthVal<0 || monthVal>12){
                et_Seldate.setError("Please enter correct month");
                isWrongMonth=true;
                return;
            }
            isWrongMonth=false;
            isDeleting=false;
            charSequence=charSequence+"/";
            et_Seldate.setText(charSequence);
            isRunning=true;
            et_Seldate.setSelection(et_Seldate.getText().length());
            isDeleting=true;
        }

        if(text.length()==10){
            String year=text.substring(6,10);
            //Log.e("year","-->"+year);
            int yearVal=Integer.parseInt(year);
            if(yearVal<1900 || yearVal>2050){
                et_Seldate.setError("Please enter correct year");
                isWrongYear=true;
                return;
            }
        }



        if(isWrongDate){
            //Log.e("isWrongDate","-->"+isWrongDate);
            if(text.length()>2){
                isDeleting=false;
                et_Seldate.setText(text.substring(0, text.length() - 1));
                isDeleting=true;
                et_Seldate.setSelection(et_Seldate.getText().length());

            }

        }


        if(isWrongMonth){
            if(text.length()>2){
                isDeleting=false;
                et_Seldate.setText(text.substring(0, text.length() - 1));
                isDeleting=true;
                et_Seldate.setSelection(et_Seldate.getText().length());

            }

        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
