package com.skybiz.wms02.ui_LoadingGoods.m_ListLorry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Bay;
import com.skybiz.wms02.m_Interface.ItemClickListener;


import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class LorryAdapter extends RecyclerView.Adapter<LorryHolder> {
    Context c;
    ArrayList<Spacecraft_Bay> spacecrafts;
    DialogLorry dialogBay;

    public LorryAdapter(Context c, ArrayList<Spacecraft_Bay> spacecrafts, DialogLorry dialogBay) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogBay=dialogBay;
    }

    @Override
    public LorryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_bay,parent,false);
        return new LorryHolder(v);
    }
    @Override
    public void onBindViewHolder(final LorryHolder holder, int position) {
        final Spacecraft_Bay spacecraft=spacecrafts.get(position);
        holder.vBay.setText(spacecraft.getBay());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String Bay=spacecraft.getBay();
                dialogBay.setLorry(Bay);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
