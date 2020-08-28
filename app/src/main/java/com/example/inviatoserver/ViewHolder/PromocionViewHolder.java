package com.example.inviatoserver.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inviatoserver.Interface.IItemClickListener;
import com.example.inviatoserver.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PromocionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView lblPromotion_Title;
    public ImageView ImgPromotion_image;
    public TextView lblPromotion_RestaurantName;
    public TextView lblPromotion_price;
    public TextView lblPromotion_DateEnd;

    private IItemClickListener iItemClickListener;


    public PromocionViewHolder(@NonNull View itemView) {
        super(itemView);

        lblPromotion_Title = itemView.findViewById(R.id.lblPromotionTitle);
        ImgPromotion_image = itemView.findViewById(R.id.imgPromotionImage);
        lblPromotion_RestaurantName = itemView.findViewById(R.id.lblPromotionRestaurantName);
        lblPromotion_price = itemView.findViewById(R.id.lblPromotionPrice);
        lblPromotion_DateEnd = itemView.findViewById(R.id.lblPromotionDateEnd);

        itemView.setOnClickListener(this);
    }//PromocionViewHolder

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.iItemClickListener = itemClickListener;
    }//setItemClickListener

    @Override
    public void onClick(View view) {
        iItemClickListener.onClick(view, getAdapterPosition(), false);
    }//onClick
}//PromocionViewHolder
