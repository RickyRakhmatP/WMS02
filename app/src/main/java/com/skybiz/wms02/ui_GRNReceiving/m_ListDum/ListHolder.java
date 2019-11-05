package com.skybiz.wms02.ui_GRNReceiving.m_ListDum;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vNo, vDelete,vItemCode,vQty;
    ItemClickListener itemClickListener;

    public ListHolder(View itemView) {
        super(itemView);
        //vLocationCode    =(TextView) itemView.findViewById(R.id.vLocationCode);
        vNo             =(TextView) itemView.findViewById(R.id.vNo);
        vItemCode       =(TextView) itemView.findViewById(R.id.vItemCode);
        vDelete         =(TextView) itemView.findViewById(R.id.vDelete);
        vQty            =(TextView) itemView.findViewById(R.id.vQty);
       // itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }

}
