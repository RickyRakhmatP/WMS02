package com.skybiz.wms02.m_ListUOM;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_UOM;
import com.skybiz.wms02.m_Detail.Dialog_Edit;
import com.skybiz.wms02.m_Interface.ItemClickListener;
import com.skybiz.wms02.m_StockTake.DialogQty;

import java.util.ArrayList;

;

/**
 * Created by 7 on 27/10/2017.
 */

public class UOMAdapter extends RecyclerView.Adapter<UOMHolder> {
    Context c;
    ArrayList<Spacecraft_UOM> spacecrafts;
    DialogQty dialogQty;

    public UOMAdapter(Context c, ArrayList<Spacecraft_UOM> spacecrafts, DialogQty dialogQty) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogQty=dialogQty;
    }

    @Override
    public UOMHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_uom,parent,false);
        return new UOMHolder(v);
    }
    @Override
    public void onBindViewHolder(final UOMHolder holder, int position) {
        final Spacecraft_UOM spacecraft=spacecrafts.get(position);
        holder.vUOM.setText(spacecraft.getUOM());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String vUOM=spacecraft.getUOM();
                final String vUOMFactor=spacecraft.getUOMFactor();
                final String vUOMPrice=spacecraft.getUOMPrice();
                dialogQty.setUOM(vUOM,vUOMPrice,vUOMFactor);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
