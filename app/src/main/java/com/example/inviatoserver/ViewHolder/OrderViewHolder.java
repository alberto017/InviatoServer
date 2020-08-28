package com.example.inviatoserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Interface.IItemClickListener;
import com.example.inviatoserver.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView lblOrderItemName;
    public TextView lblOrderItemStatus;
    public TextView lblOrderItemAddress;
    public TextView lblOrderItemPhone;
    public TextView lblOrderItemHour;
    public TextView lblOrderItemDate;

    private IItemClickListener iItemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        lblOrderItemName = itemView.findViewById(R.id.lblOrderItemName);
        lblOrderItemStatus = itemView.findViewById(R.id.lblOrderItemStatus);
        lblOrderItemAddress = itemView.findViewById(R.id.lblOrderItemAddress);
        lblOrderItemPhone = itemView.findViewById(R.id.lblOrderItemPhone);
        lblOrderItemDate = itemView.findViewById(R.id.lblOrderItemDate);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }//OrderViewHolder

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }//setiItemClickListener

    @Override
    public void onClick(View view) {
        iItemClickListener.onClick(view,getAdapterPosition(),false);
    }//onClick

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Selecciona la operacion");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }//onCreateContextMenu
}//OrderViewHolder
