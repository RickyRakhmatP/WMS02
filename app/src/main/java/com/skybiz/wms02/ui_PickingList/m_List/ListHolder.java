package com.skybiz.wms02.ui_PickingList.m_List;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skybiz.wms02.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vN_o, vDoc1No,vDelete,vQty;
    ItemClickListener itemClickListener;


    public ListHolder(View itemView) {
        super(itemView);
        vN_o     =(TextView) itemView.findViewById(R.id.vN_o);
        vDoc1No  =(TextView) itemView.findViewById(R.id.vDoc1No);
        vDelete  =(TextView) itemView.findViewById(R.id.vDelete);
        vQty    =(TextView) itemView.findViewById(R.id.vQty);
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
