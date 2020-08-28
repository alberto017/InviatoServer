package com.example.inviatoserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Interface.IItemClickListener;
import com.example.inviatoserver.R;

import androidx.recyclerview.widget.RecyclerView;

public class CategoriaViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView lblCategory_title;
    public ImageView lblCategory_image;

    private IItemClickListener iItemClickListener;

    public CategoriaViewHolder(View itemView) {
        super(itemView);

        lblCategory_title = itemView.findViewById(R.id.category_title);
        lblCategory_image = itemView.findViewById(R.id.category_image);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }//CategoriaViewHolder

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.iItemClickListener = itemClickListener;
    }//setItemClickListener

    @Override
    public void onClick(View view) {
        iItemClickListener.onClick(view, getAdapterPosition(), false);
    }//onClick

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Selecciona la operacion");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }//onCreateContextMenu
}
