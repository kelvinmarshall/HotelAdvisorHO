package dev.marshall.hoteladvisorho.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.R;

/**
 * Created by Marshall on 02/04/2018.
 */

public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView dinner,lunch,breakfast,bath,tv,hot,book,cancel,roomtype,roomprice,capacity;
    public ImageView imageroom;


    public RoomViewHolder(View itemView) {
        super(itemView);

        imageroom= itemView.findViewById(R.id.room_image);
        roomprice= itemView.findViewById(R.id.room_pr);
        roomtype= itemView.findViewById(R.id.rtype);
        capacity= itemView.findViewById(R.id.occupant);

        dinner= itemView.findViewById(R.id.dinnerincluded);
        lunch= itemView.findViewById(R.id.lunchincluded);
        breakfast= itemView.findViewById(R.id.breakfastincluded);
        bath= itemView.findViewById(R.id.bathincluded);
        tv= itemView.findViewById(R.id.tvincluded);
        hot= itemView.findViewById(R.id.hotincluded);
        book= itemView.findViewById(R.id.bookpay);
        cancel= itemView.findViewById(R.id.cancellation);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        //  this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        // itemClickListener.onClick(v,getAdapterPosition(),false);
    }

}
