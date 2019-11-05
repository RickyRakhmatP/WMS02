package com.skybiz.wms02.ui_LoadingGoods.m_ListLoading;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Interface.ItemClickListener;


/**
 * Created by 7 on 27/10/2017.
 */

public class LoadingHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vCheckerCode,vDriverCode,vBay,vLorryNo,vDateTime;
    ItemClickListener itemClickListener;

    public LoadingHolder(View itemView) {
        super(itemView);
        vCheckerCode    =(TextView) itemView.findViewById(R.id.vCheckerCode);
        vDriverCode     =(TextView) itemView.findViewById(R.id.vDriverCode);
        vBay            =(TextView) itemView.findViewById(R.id.vBay);
        vLorryNo        =(TextView) itemView.findViewById(R.id.vLorryNo);
        vDateTime       =(TextView) itemView.findViewById(R.id.vDateTime);
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
