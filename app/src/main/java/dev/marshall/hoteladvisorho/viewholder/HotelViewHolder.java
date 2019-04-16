package dev.marshall.hoteladvisorho.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.R;

/**
 * Created by Marshall on 26/03/2018.
 */

public class HotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView Hotelname;
    private ItemClickListener itemClickListener;


    public HotelViewHolder(View itemView) {
        super(itemView);

        Hotelname= itemView.findViewById(R.id.hotelname);

        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
