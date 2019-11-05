package com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecarft_Trn;
import com.skybiz.wms02.m_DataObject.Spacecraft_Detail;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListCSAdapter extends RecyclerView.Adapter<ListCSHolder> {
    Context c;
    ArrayList<Spacecraft_Detail> spacecrafts;
    int clickcount=0;

    public ListCSAdapter(Context c, ArrayList<Spacecraft_Detail> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ListCSHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_detailcs,parent,false);
        return new ListCSHolder(v);

    }
    @Override
    public void onBindViewHolder(final ListCSHolder holder, int position) {
        final Spacecraft_Detail spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vQty.setText(spacecraft.getBtnQty());
        holder.vUOM.setText(spacecraft.getFactorQty()+" "+spacecraft.getUOM());

    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    public void clear() {
       /* final int size = spacecrafts.size();
        spacecrafts.clear();
        notifyDataSetChanged();*/
        final int size = spacecrafts.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                spacecrafts.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}
