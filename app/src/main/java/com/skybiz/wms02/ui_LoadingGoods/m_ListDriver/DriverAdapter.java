package com.skybiz.wms02.ui_LoadingGoods.m_ListDriver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;
import com.skybiz.wms02.m_Interface.ItemClickListener;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class DriverAdapter extends RecyclerView.Adapter<DriverHolder> {
    Context c;
    ArrayList<Spacecraft_Checker> spacecrafts;
    DialogDriver dialogChecker;

    public DriverAdapter(Context c, ArrayList<Spacecraft_Checker> spacecrafts, DialogDriver dialogChecker) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogChecker=dialogChecker;
    }

    @Override
    public DriverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_checker,parent,false);
        return new DriverHolder(v);
    }
    @Override
    public void onBindViewHolder(final DriverHolder holder, int position) {
        final Spacecraft_Checker spacecraft=spacecrafts.get(position);
        holder.vCheckerCode.setText(spacecraft.getCheckerCode());
        holder.vCheckerName.setText(spacecraft.getCheckerName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String CheckerCode=spacecraft.getCheckerCode();
                final String CheckerName=spacecraft.getCheckerName();
                dialogChecker.setDriver(CheckerCode,CheckerName);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
