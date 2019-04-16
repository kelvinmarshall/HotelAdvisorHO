package dev.marshall.hoteladvisorho.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.R;
import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.R;

/**
 * Created by Marshall on 24/03/2018.
 */

public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
   public CircleImageView Reviewerimages;
   public TextView Review,Reviewername;
    private ItemClickListener itemClickListener;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        Reviewerimages= itemView.findViewById(R.id.reviewerimage);
        Review= itemView.findViewById(R.id.review);
        Reviewername= itemView.findViewById(R.id.reviewername);

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
