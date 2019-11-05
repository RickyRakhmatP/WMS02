package com.skybiz.wms02.m_Listing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.SetDefaultTax;
import com.skybiz.wms02.m_DataObject.Spacecarft_Trn;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import com.skybiz.wms02.m_Detail.AddItem;
import com.skybiz.wms02.m_Detail.EditItem;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    String IPAddress,UserName,Password,DBName,Qty,DocType;
    Context c;
    ArrayList<Spacecarft_Trn> spacecrafts;
    int clickcount=0;

    public ListAdapter(Context c, String DocType, ArrayList<Spacecarft_Trn> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_listing,parent,false);
        return new ListHolder(v);

    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecarft_Trn spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vD_ate.setText(spacecraft.getD_ate());
        holder.vTotalAmt.setText(spacecraft.getHCNetAmt());
        holder.vQty.setText(spacecraft.getTotalQty());
        holder.vCusName.setText(spacecraft.getCusName());
        final String SyncYN=spacecraft.getSyncYN();
        if(SyncYN.equals("0")){
            holder.vSyncYN.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.sync_disabled, 0);
        }else{
            holder.vSyncYN.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sync_success, 0);
        }
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
