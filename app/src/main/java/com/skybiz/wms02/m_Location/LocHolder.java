package com.skybiz.wms02.m_Location;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class LocHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vLocationCode, vLocationName;
    ItemClickListener itemClickListener;

    public LocHolder(View itemView) {
        super(itemView);
        vLocationCode      =(TextView) itemView.findViewById(R.id.vLocationCode);
        vLocationName      =(TextView) itemView.findViewById(R.id.vLocationName);
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
