package com.skybiz.wms02.ui_PrintInvLabel.m_ListUser;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skybiz.wms02.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vUserName;
    ItemClickListener itemClickListener;

    public UserHolder(View itemView) {
        super(itemView);
        vUserName     =(TextView) itemView.findViewById(R.id.vUserName);
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
