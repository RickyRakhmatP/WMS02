package com.skybiz.wms02.m_OnHand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Customer;
import com.skybiz.wms02.m_DataObject.Spacecraft_OnHand;
import com.skybiz.wms02.m_Supplier.DialogSupplier;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class OnHandAdapter extends RecyclerView.Adapter<OnHandHolder> {
    Context c;
    ArrayList<Spacecraft_OnHand> spacecrafts;
    DialogOnHandL dialogOnHandL;

    public OnHandAdapter(Context c, ArrayList<Spacecraft_OnHand> spacecrafts, DialogOnHandL dialogOnHandL) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogOnHandL=dialogOnHandL;
    }

    @Override
    public OnHandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_onhand,parent,false);
        return new OnHandHolder(v);
    }
    @Override
    public void onBindViewHolder(final OnHandHolder holder, int position) {
        final Spacecraft_OnHand spacecraft=spacecrafts.get(position);
        holder.vLocationCode.setText(spacecraft.getLocationCode());
        holder.vQty.setText(spacecraft.getQty());
       /* holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String CusCode=spacecraft.getCusCode();
                final String CusName=spacecraft.getCusName();
                final String TermCode=spacecraft.getTermCode();
                final String D_ay=spacecraft.getD_ay();
                //dialogOnHandL.setCustomer(CusCode,CusName,TermCode,D_ay);
            }
        });*/
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
