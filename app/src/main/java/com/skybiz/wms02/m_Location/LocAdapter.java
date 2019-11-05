package com.skybiz.wms02.m_Location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.m_DataObject.Spacecraft_Loc;

import java.util.ArrayList;

import com.skybiz.wms02.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class LocAdapter extends RecyclerView.Adapter<LocHolder> {
    Context c;
    ArrayList<Spacecraft_Loc> spacecrafts;
    DialogLoc dialogLoc;
    public LocAdapter(Context c, ArrayList<Spacecraft_Loc> spacecrafts, DialogLoc dialogLoc) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogLoc=dialogLoc;
    }
    @Override
    public LocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_loc,parent,false);
        return new LocHolder(v);
    }
    @Override
    public void onBindViewHolder(final LocHolder holder, int position) {
        final Spacecraft_Loc spacecraft=spacecrafts.get(position);
        holder.vLocationCode.setText(spacecraft.getLocationCode());
        holder.vLocationName.setText(spacecraft.getLocationName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                dialogLoc.setLoc(spacecraft.getLocationCode());
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
