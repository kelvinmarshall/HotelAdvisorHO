package dev.marshall.hoteladvisorho.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.R;

/**
 * Created by Marshall on 16/03/2018.
 */

public class hotelwebViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView Hotelname,Hotelurl;
    private ItemClickListener itemClickListener;


    public hotelwebViewHolder(View itemView) {
        super(itemView);

        Hotelname= itemView.findViewById(R.id.hotelname);
        Hotelurl= itemView.findViewById(R.id.hotelurl);

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
