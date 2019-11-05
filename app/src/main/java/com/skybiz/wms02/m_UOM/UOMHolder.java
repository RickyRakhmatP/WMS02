package com.skybiz.wms02.m_UOM;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class UOMHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vUOM, vRemark;
    ItemClickListener itemClickListener;

    public UOMHolder(View itemView) {
        super(itemView);
        vUOM     =(TextView) itemView.findViewById(R.id.vUOM);
        vRemark       =(TextView) itemView.findViewById(R.id.vRemark);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }

}
