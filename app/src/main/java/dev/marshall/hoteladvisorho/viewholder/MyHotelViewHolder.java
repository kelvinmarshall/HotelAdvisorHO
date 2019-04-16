package dev.marshall.hoteladvisorho.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.R;
import dev.marshall.hoteladvisorho.common.Common;

/**
 * Created by Marshall on 05/03/2018.
 */

public  class MyHotelViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txtMenuName,txtLocation,txtDistance,txtPrice;
    public ImageView imageView;
    private ItemClickListener itemClickListener;


    public MyHotelViewHolder(View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.menu_name);
        txtLocation = itemView.findViewById(R.id.location_name);
        txtDistance = itemView.findViewById(R.id.distance);
        txtPrice = itemView.findViewById(R.id.price);
        imageView = itemView.findViewById(R.id.menu_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}

