package com.skybiz.wms02.ui_LoadingGoods.m_ListLorry;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Interface.ItemClickListener;


/**
 * Created by 7 on 27/10/2017.
 */

public class LorryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vBay;
    ItemClickListener itemClickListener;

    public LorryHolder(View itemView) {
        super(itemView);
        vBay     =(TextView) itemView.findViewById(R.id.vBay);
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
