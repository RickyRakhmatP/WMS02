package com.skybiz.wms02.ui_LoadingGoods.m_ListDum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;
import com.skybiz.wms02.m_DataObject.Spacecraft_Dum;
import com.skybiz.wms02.m_Interface.ItemClickListener;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsIn;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsOut;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDriver.DialogDriver;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class DumAdapter extends RecyclerView.Adapter<DumHolder> {
    Context c;
    ArrayList<Spacecraft_Dum> spacecrafts;
    String uFrom;


    public DumAdapter(Context c,String uFrom, ArrayList<Spacecraft_Dum> spacecrafts) {
        this.c = c;
        this.uFrom=uFrom;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public DumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_dumlist,parent,false);
        return new DumHolder(v);
    }
    @Override
    public void onBindViewHolder(final DumHolder holder, int position) {
        final Spacecraft_Dum spacecraft=spacecrafts.get(position);
        holder.vN_o.setText(spacecraft.getN_o());
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vQty.setText(spacecraft.getQty());
        holder.vRemark.setText(spacecraft.getRemark());
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uFrom.equals("OUT")) {
                    ((LoadingGoodsOut) c).delRow(spacecraft.getRunNo());
                }else if(uFrom.equals("IN")){
                    ((LoadingGoodsIn) c).delRow(spacecraft.getRunNo());
                }
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
