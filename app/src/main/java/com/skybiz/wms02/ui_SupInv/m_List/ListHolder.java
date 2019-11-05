package com.skybiz.wms02.ui_SupInv.m_List;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.ui_GRNReceiving.m_ListDum.ItemClickListener;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vDescription, vDelete,vItemCode,vQty;
    ItemClickListener itemClickListener;

    public ListHolder(View itemView) {
        super(itemView);
        vDescription     =(TextView) itemView.findViewById(R.id.vDescription);
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
