package com.skybiz.wms02.m_Customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Customer;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerHolder> {
    Context c;
    ArrayList<Spacecraft_Customer> spacecrafts;
    DialogCustomer dialogSupplier;

    public CustomerAdapter(Context c, ArrayList<Spacecraft_Customer> spacecrafts, DialogCustomer dialogSupplier) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogSupplier=dialogSupplier;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_supplier,parent,false);
        return new CustomerHolder(v);
    }
    @Override
    public void onBindViewHolder(final CustomerHolder holder, int position) {
        final Spacecraft_Customer spacecraft=spacecrafts.get(position);
        holder.vCusCode.setText(spacecraft.getCusCode());
        holder.vCusName.setText(spacecraft.getCusName());
        //holder.vTermCode.setText(spacecraft.getTermCode());
       // holder.vD_ay.setText(spacecraft.getD_ay());
        holder.vSalesPersonCode.setText(spacecraft.getSalesPersonCode());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String CusCode=spacecraft.getCusCode();
                final String CusName=spacecraft.getCusName();
                final String TermCode=spacecraft.getTermCode();
                final String D_ay=spacecraft.getD_ay();
                dialogSupplier.setCustomer(CusCode,CusName,TermCode,D_ay);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
