package com.ejs.birthchart.adapters;

import static com.ejs.birthchart.utils.msg.log;
import static com.ejs.birthchart.utils.utils.loadImageFileMoon;
import static com.ejs.birthchart.utils.utils.moonPhase;
import static com.ejs.birthchart.utils.utils.moonType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ejs.birthchart.R;
import com.ejs.birthchart.data.dataMoonCal;
import com.ejs.birthchart.utils.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class moonAdapter extends RecyclerView.Adapter<moonAdapter.ViewHolder> {
    private final String tag = this.getClass().getSimpleName();
    private final String TAG = this.getClass().getSimpleName();
    private final List<dataMoonCal> items;
    private Context context;
    private utils Utils = new utils();



    public moonAdapter(Context context, ArrayList<dataMoonCal> items ) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.item_day, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        dataMoonCal item = items.get(i);

        String day = context.getString(R.string.day) + " " +item.getDay();
        log("e", "frc", "day " + day);
        log("e", "frc", "getMoonPhase " + item.getMoonPhase());
        String distance =String.format(Locale.getDefault(), "%,d", item.getDistance()) + " km";
        String moonPhase = moonPhase(context, item.getMoonPhase()) + " " + moonType(context, item.getTypeMoon());
        String moonAge = item.getMoonAge() + " " + context.getString(R.string.day);
        viewHolder.iv_moonPhase.setImageBitmap(loadImageFileMoon(context, item.getMoonPhase()));
        viewHolder.tv_day.setText(day);
        viewHolder.tv_distance.setText(distance);
        viewHolder.tv_distance.setSelected(true);
        viewHolder.tv_moonPhase.setText(moonPhase);
        viewHolder.tv_moonPhase.setSelected(true);
        viewHolder.tv_EgoldenHourEnd.setText(item.getEveningbluehour());
        viewHolder.tv_EgoldenHourEnd.setSelected(true);
        viewHolder.tv_EgoldenHourStart.setText(item.getEveninggoldenhour());
        viewHolder.tv_EgoldenHourStart.setSelected(true);
        viewHolder.tv_MgoldenHourStart.setText(item.getMorningbluehour());
        viewHolder.tv_MgoldenHourStart.setSelected(true);
        viewHolder.tv_MgoldenHourEnd.setText(item.getMorninggoldenhour());
        viewHolder.tv_MgoldenHourEnd.setSelected(true);
        viewHolder.tv_evening.setSelected(true);
        viewHolder.tv_morning.setSelected(true);
        viewHolder.tv_moonAge.setSelected(true);
        viewHolder.tv_moonAge.setText(moonAge);
        viewHolder.tv_moonAgePerc.setText(item.getMoonAgePorc() + "%");
        viewHolder.tv_moonAgePerc.setSelected(true);
        viewHolder.tv_ilumination.setText(item.getIllumination() + "%");
        viewHolder.tv_ilumination.setSelected(true);
        viewHolder.tv_sunrise.setText(item.getSunrise());
        viewHolder.tv_sunrise.setSelected(true);
        viewHolder.tv_sunset.setText(item.getSunSet());
        viewHolder.tv_sunset.setSelected(true);
        viewHolder.tv_Timemoonrise.setText(item.getMoonRise());
        viewHolder.tv_Timemoonrise.setSelected(true);
        viewHolder.tv_Timemoonset.setText(item.getMoonSet());
        viewHolder.tv_Timemoonset.setSelected(true);


    }

    @Override
    public int getItemCount() {
        int size;
        if (items != null){
            size= items.size();
        } else {
            size= 0;
        }
        return size;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv_day;
        ImageView iv_moonPhase;
        TextView tv_day, tv_moonPhase, tv_distance, tv_ilumination, tv_moonAge, tv_moonAgePerc, tv_sunrise, tv_sunset, tv_Timemoonrise, tv_Timemoonset,
        tv_morning, tv_evening, tv_MgoldenHourStart, tv_MgoldenHourEnd, tv_EgoldenHourStart, tv_EgoldenHourEnd ;



        ViewHolder(View v) {
            super(v);
            tv_day = v.findViewById(R.id.tv_day);
            cv_day = v.findViewById(R.id.cv_day);
            iv_moonPhase = v.findViewById(R.id.iv_moonPhase);
            tv_moonPhase = v.findViewById(R.id.tv_moonPhase);
            tv_distance = v.findViewById(R.id.tv_distance);
            tv_ilumination = v.findViewById(R.id.tv_ilumination);
            tv_moonAge = v.findViewById(R.id.tv_moonAge);
            tv_moonAgePerc = v.findViewById(R.id.tv_moonAgePerc);
            tv_sunrise = v.findViewById(R.id.tv_sunrise);
            tv_sunset = v.findViewById(R.id.tv_sunset);
            tv_Timemoonrise = v.findViewById(R.id.tv_Timemoonrise);
            tv_Timemoonset = v.findViewById(R.id.tv_Timemoonset);
            tv_morning = v.findViewById(R.id.tv_morning);
            tv_evening = v.findViewById(R.id.tv_evening);
            tv_MgoldenHourStart = v.findViewById(R.id.tv_MgoldenHourStart);
            tv_MgoldenHourEnd = v.findViewById(R.id.tv_MgoldenHourEnd);
            tv_EgoldenHourEnd = v.findViewById(R.id.tv_EgoldenHourEnd);
            tv_EgoldenHourStart = v.findViewById(R.id.tv_EgoldenHourStart);
            //rv = v.findViewById(R.id.cons_rv_list_item);
        }
    }
}
