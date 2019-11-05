package com.skybiz.wms02.ui_LoadingGoods.m_ListChecker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Bay;
import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;
import com.skybiz.wms02.m_Interface.ItemClickListener;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class CheckerAdapter extends RecyclerView.Adapter<CheckerHolder> {
    Context c;
    ArrayList<Spacecraft_Checker> spacecrafts;
    DialogChecker dialogChecker;

    public CheckerAdapter(Context c, ArrayList<Spacecraft_Checker> spacecrafts, DialogChecker dialogChecker) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogChecker=dialogChecker;
    }

    @Override
    public CheckerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_checker,parent,false);
        return new CheckerHolder(v);
    }
    @Override
    public void onBindViewHolder(final CheckerHolder holder, int position) {
        final Spacecraft_Checker spacecraft=spacecrafts.get(position);
        holder.vCheckerCode.setText(spacecraft.getCheckerCode());
        holder.vCheckerName.setText(spacecraft.getCheckerName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String CheckerCode=spacecraft.getCheckerCode();
                final String CheckerName=spacecraft.getCheckerName();
                dialogChecker.setChecker(CheckerCode,CheckerName);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
