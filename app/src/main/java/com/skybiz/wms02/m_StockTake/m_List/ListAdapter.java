package com.skybiz.wms02.m_StockTake.m_List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.m_DataObject.Spacecraft_StockTake;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    Context c;
    ArrayList<Spacecraft_StockTake> spacecrafts;
    public ListAdapter(Context c, ArrayList<Spacecraft_StockTake> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;

    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_stocktake,parent,false);
        return new ListHolder(v);
    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecraft_StockTake spacecraft=spacecrafts.get(position);
        holder.vNo.setText(spacecraft.getN_o());
        holder.vItemCode.setText(spacecraft.getItemCode()+" ["+spacecraft.getUOM()+"]");
        holder.vQty.setText(spacecraft.getChkQty());
        holder.vQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StockTake)c).openQty(spacecraft.getRunNo(),spacecraft.getItemCode(),spacecraft.getD_ateTime());
            }
        });
        holder.vItemCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StockTake)c).openUOM(spacecraft.getRunNo(),spacecraft.getItemCode(),spacecraft.getD_ateTime());
            }
        });
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StockTake)c).delRow(spacecraft.getRunNo(),spacecraft.getItemCode(),spacecraft.getD_ateTime());
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }


}
