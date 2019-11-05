package com.skybiz.wms02.m_Detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.skybiz.wms02.R;
/**
 * Created by 7 on 13/11/2017.
 */

public class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView vDescription,vQty,vUnitPrice;
    ItemClickListener itemClickListener;

    public OrderHolder(View itemView) {
        super(itemView);
        vDescription  =(TextView) itemView.findViewById(R.id.vDescription);
        vQty          =(TextView) itemView.findViewById(R.id.vQty);
        vUnitPrice      =(TextView) itemView.findViewById(R.id.vUnitPrice);
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
