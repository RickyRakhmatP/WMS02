package com.skybiz.wms02.ui_LoadingGoods.m_ListLoading;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;
import com.skybiz.wms02.m_DataObject.Spacecraft_Loading;
import com.skybiz.wms02.m_Interface.ItemClickListener;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class LoadingAdapter extends RecyclerView.Adapter<LoadingHolder> {
    Context c;
    ArrayList<Spacecraft_Loading> spacecrafts;
    DialogLoading dialogLoading;

    public LoadingAdapter(Context c, ArrayList<Spacecraft_Loading> spacecrafts, DialogLoading dialogLoading) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogLoading=dialogLoading;
    }

    @Override
    public LoadingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_loading,parent,false);
        return new LoadingHolder(v);
    }
    @Override
    public void onBindViewHolder(final LoadingHolder holder, int position) {
        final Spacecraft_Loading spacecraft=spacecrafts.get(position);
        holder.vCheckerCode.setText(spacecraft.getCheckerCode());
        holder.vBay.setText(spacecraft.getBay());
        holder.vDateTime.setText(spacecraft.getD_ateTime());
        holder.vDriverCode.setText(spacecraft.getDriverCode());
        holder.vLorryNo.setText(spacecraft.getLorryNo());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String RunNo=spacecraft.getRunNo();
                final String CheckerCode=spacecraft.getCheckerCode();
                final String D_ateTime=spacecraft.getD_ateTime();
                dialogLoading.setHeader(RunNo,CheckerCode,D_ateTime);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
