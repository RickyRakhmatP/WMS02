package com.skybiz.wms02.ui_GRNReceiving.m_ListDum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.m_DataObject.Spacecraft_GRNR;
import com.skybiz.wms02.m_DataObject.Spacecraft_StockTake;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    Context c;
    ArrayList<Spacecraft_GRNR> spacecrafts;
    public ListAdapter(Context c, ArrayList<Spacecraft_GRNR> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;

    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_grnr,parent,false);
        return new ListHolder(v);
    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecraft_GRNR spacecraft=spacecrafts.get(position);
        holder.vNo.setText(spacecraft.getN_o());
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vQty.setText(spacecraft.getQty());
        final String remark=spacecraft.getRemark();
        if(remark.equals("1")){
            holder.vDelete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
        }else{
            holder.vDelete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_forever, 0);
            holder.vQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GRN_Receiving)c).openQty(spacecraft.getRunNo(),"","");
                }
            });
            holder.vDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GRN_Receiving)c).delRow(spacecraft.getRunNo());
                }
            });
        }

    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }


}
