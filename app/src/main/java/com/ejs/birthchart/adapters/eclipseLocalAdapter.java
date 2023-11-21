package com.ejs.birthchart.adapters;

import static com.ejs.birthchart.utils.DateTimeUtils.convertUTCtoLocalTime;
import static com.ejs.birthchart.utils.DateTimeUtils.dateTimeFormatter12;

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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class eclipseLocalAdapter extends RecyclerView.Adapter<eclipseLocalAdapter.ViewHolder> {
    private final String tag = this.getClass().getSimpleName();
    private final List<dataEclipse> items;
    private Context context;


    public eclipseLocalAdapter(Context context, ArrayList<dataEclipse> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.layout_items_eclipse_local,
                viewGroup, false);

        
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        dataEclipse item = items.get(i);
        BitmapFactory.Options options = new BitmapFactory.Options();

        ZonedDateTime zdts = ZonedDateTime.parse(item.getPeak().toDateTime().toString());
        viewHolder.iv_eclipse.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), item.getImage(), options));
        viewHolder.tv_date_eclipse.setText(" " + convertUTCtoLocalTime(item.getPeak().toDateTime().toString(), dateTimeFormatter12));
        viewHolder.tv_date_eclipse.setSelected(true);
        viewHolder.tv_kind_eclipse.setText(" " + item.getKind().name());
        viewHolder.tv_kind_eclipse.setSelected(true);
        viewHolder.tv_magnitude.setText(" " + (int)(item.getObscuration() * 100) + "%");
        viewHolder.tv_magnitude.setSelected(true);



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

        CardView cv_eclipse;
        ImageView iv_eclipse;
        TextView tv_date_eclipse, tv_kind_eclipse, tv_distance, tv_magnitude, tv_latitude, tv_longitude, tv_total, tv_penumbral,
                tv_partial;
        ViewHolder(View v) {
            super(v);

            cv_eclipse = v.findViewById(R.id.cv_eclipse);
            iv_eclipse = v.findViewById(R.id.iv_eclipse);
            tv_date_eclipse = v.findViewById(R.id.tv_date_eclipse);
            tv_kind_eclipse = v.findViewById(R.id.tv_kind_eclipse);
            tv_magnitude = v.findViewById(R.id.tv_magnitude);
            //rv = v.findViewById(R.id.cons_rv_list_item);
        }
    }
}
