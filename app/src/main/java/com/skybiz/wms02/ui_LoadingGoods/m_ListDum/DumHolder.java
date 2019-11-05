package com.skybiz.wms02.ui_LoadingGoods.m_ListDum;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Interface.ItemClickListener;


/**
 * Created by 7 on 27/10/2017.
 */

public class DumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vQty,vDoc1No,vRemark,vDelete,vN_o;
    ItemClickListener itemClickListener;

    public DumHolder(View itemView) {
        super(itemView);
        vN_o        =(TextView) itemView.findViewById(R.id.vN_o);
        vQty        =(TextView) itemView.findViewById(R.id.vQty);
        vDoc1No     =(TextView) itemView.findViewById(R.id.vDoc1No);
        vRemark     =(TextView) itemView.findViewById(R.id.vRemark);
        vDelete     =(TextView) itemView.findViewById(R.id.vDelete);
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
