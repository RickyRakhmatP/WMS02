package com.skybiz.wms02.m_ItemGroup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.skybiz.wms02.R;

/**
 * Created by 7 on 14/11/2017.
 */

public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    Button btnGroup ;
    ItemClickListener itemClickListener;

    public GroupHolder(View itemView) {
        super(itemView);
        btnGroup=(Button) itemView.findViewById(R.id.btnGroup);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }
}
