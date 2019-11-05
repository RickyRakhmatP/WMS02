package com.skybiz.wms02.m_OnHand;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class OnHandHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vLocationCode, vQty;
    ItemClickListener itemClickListener;

    public OnHandHolder(View itemView) {
        super(itemView);
        vLocationCode     =(TextView) itemView.findViewById(R.id.vLocationCode);
        vQty     =(TextView) itemView.findViewById(R.id.vQty);
        //itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }

}
