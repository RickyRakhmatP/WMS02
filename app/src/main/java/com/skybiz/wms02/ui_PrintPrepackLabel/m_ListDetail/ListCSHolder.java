package com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListCSHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vItemCode, vDescription,vQty, vUOM;
    ItemClickListener itemClickListener;

    public ListCSHolder(View itemView) {
        super(itemView);
        vItemCode     =(TextView) itemView.findViewById(R.id.vItemCode);
        vDescription      =(TextView) itemView.findViewById(R.id.vDescription);
        vQty        =(TextView) itemView.findViewById(R.id.vQty);
        vUOM   =(TextView) itemView.findViewById(R.id.vUOM);
       // itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }

}
