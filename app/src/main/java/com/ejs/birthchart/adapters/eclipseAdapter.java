package com.ejs.birthchart.adapters;

import static com.ejs.birthchart.utils.msg.log;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ejs.birthchart.R;
import com.ejs.birthchart.data.dataEclipse;
import com.ejs.birthchart.utils.utils;

import java.util.ArrayList;
import java.util.List;

public class eclipseAdapter extends RecyclerView.Adapter<eclipseAdapter.ViewHolder> {
    private final String tag = this.getClass().getSimpleName(); 
    private final List<dataEclipse> items;
    public int Type;
    CardView cv_eclipse;
    ImageView iv_eclipse;
    TextView tv_date_eclipse, tv_kind_eclipse, tv_distance, tv_magnitude, tv_latitude, tv_longitude, tv_total, tv_penumbral,
            tv_partial;
    private Context context;
    private utils Utils = new utils();


    public eclipseAdapter(Context context, ArrayList<dataEclipse> items, int Type) {
        this.items = items;
        this.context = context;
        this.Type = Type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.layout_items_eclipse_local,
                viewGroup, false);
        cv_eclipse = v.findViewById(R.id.cv_eclipse);
        iv_eclipse = v.findViewById(R.id.iv_eclipse);
        tv_date_eclipse = v.findViewById(R.id.tv_date_eclipse);
        tv_kind_eclipse = v.findViewById(R.id.tv_kind_eclipse);
        tv_magnitude = v.findViewById(R.id.tv_magnitude);
        if (Type == 0) {
            v = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.layout_items_eclipse_global, 
                    viewGroup, false);
            tv_distance = v.findViewById(R.id.tv_distance);
            tv_latitude = v.findViewById(R.id.tv_latitude);
            tv_longitude = v.findViewById(R.id.tv_longitude);
        }
        if (Type == 3) {
            v = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.layout_items_eclipse_lunar,
                    viewGroup, false);
            tv_distance = v.findViewById(R.id.tv_distance);
            tv_total = v.findViewById(R.id.tv_total);
            tv_partial = v.findViewById(R.id.tv_partial);
            tv_penumbral = v.findViewById(R.id.tv_penumbral);
        }

        
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        dataEclipse item = items.get(i);
        BitmapFactory.Options options = new BitmapFactory.Options();

        iv_eclipse.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), item.getImage(), options));
        tv_date_eclipse.setText(item.getPeak().toDateTime().toString());
        tv_date_eclipse.setSelected(true);
        tv_kind_eclipse.setText(item.getKind().name());
        tv_kind_eclipse.setSelected(true);
        tv_magnitude.setText(String.valueOf(item.getObscuration()));
        tv_magnitude.setSelected(true);

        if (Type == 0) {
            tv_distance.setText(String.valueOf(item.getDistance()));
            tv_distance.setSelected(true);
            tv_latitude.setText(String.valueOf(item.getLatitude()));
            tv_latitude.setSelected(true);
            tv_longitude.setText(String.valueOf(item.getLongitude()));
            tv_longitude.setSelected(true);

        }
        if (Type == 3) {
            log("e","eclipse", "LunarEclipse getSdTotal " + item.getPeak().toDateTime());
            tv_total.setText(String.valueOf(item.getSdTotal()));
            tv_total.setSelected(true);
            tv_partial.setText(String.valueOf(item.getSdPartial()));
            tv_partial.setSelected(true);
            tv_penumbral.setText(String.valueOf(item.getSdPenum()));
            tv_penumbral.setSelected(true);

        }


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
        
        ViewHolder(View v) {
            super(v); 
             
            //rv = v.findViewById(R.id.cons_rv_list_item);
        }
    }
}
