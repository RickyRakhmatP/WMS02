package com.skybiz.wms02.ui_SupInv.m_List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Dt;
import com.skybiz.wms02.m_DataObject.Spacecraft_GRNR;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;
import com.skybiz.wms02.ui_SupInv.SupplierInvoice;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    Context c;
    ArrayList<Spacecraft_Dt> spacecrafts;
    public ListAdapter(Context c, ArrayList<Spacecraft_Dt> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;

    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_dt,parent,false);
        return new ListHolder(v);
    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecraft_Dt spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vQty.setText(spacecraft.getQty() +" *");
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SupplierInvoice) c).delRow(spacecraft.getRunNo());
            }
        });

    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }


}
