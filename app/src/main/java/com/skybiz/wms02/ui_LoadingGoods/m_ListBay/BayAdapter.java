package com.skybiz.wms02.ui_LoadingGoods.m_ListBay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Bay;
import com.skybiz.wms02.m_DataObject.Spacecraft_Customer;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class BayAdapter extends RecyclerView.Adapter<BayHolder> {
    Context c;
    ArrayList<Spacecraft_Bay> spacecrafts;
    DialogBay dialogBay;

    public BayAdapter(Context c, ArrayList<Spacecraft_Bay> spacecrafts, DialogBay dialogBay) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogBay=dialogBay;
    }

    @Override
    public BayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_bay,parent,false);
        return new BayHolder(v);
    }
    @Override
    public void onBindViewHolder(final BayHolder holder, int position) {
        final Spacecraft_Bay spacecraft=spacecrafts.get(position);
        holder.vBay.setText(spacecraft.getBay());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String Bay=spacecraft.getBay();
                dialogBay.setBay(Bay);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
