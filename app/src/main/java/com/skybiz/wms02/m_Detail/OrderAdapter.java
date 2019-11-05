package com.skybiz.wms02.m_Detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.skybiz.wms02.m_DataObject.Spacecraft_Detail;
import java.util.ArrayList;
import java.util.Locale;

import com.skybiz.wms02.R;



/**
 * Created by 7 on 13/11/2017.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderHolder> {
    Context c;
    ArrayList<Spacecraft_Detail> spacecrafts;
    FragmentManager fm;
    String DocType;
    public OrderAdapter(Context c, String DocType, ArrayList<Spacecraft_Detail> spacecrafts, FragmentManager fm) {
        this.c = c;
        this.DocType=DocType;
        this.spacecrafts = spacecrafts;
        this.fm=fm;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_detail,parent,false);
        return new OrderHolder(v);
    }

    @Override
    public void onBindViewHolder(final OrderHolder holder, int position) {
        final Spacecraft_Detail spacecraft=spacecrafts.get(position);
        final Double dQty= Double.parseDouble(spacecraft.getBtnQty());
        final String Qty= String.format(Locale.US, "%,.2f",dQty);
        final Double dUnitPrice= Double.parseDouble(spacecraft.getHCUnitCost());
        final String UnitPrice= String.format(Locale.US, "%,.2f",dUnitPrice);
        final Double dHCDiscount= Double.parseDouble(spacecraft.getHCDiscount());
        final String HCDiscount= String.format(Locale.US,"%,.2f",dHCDiscount);
        final String UOM=spacecraft.getUOM();
        final String FactorQty=spacecraft.getFactorQty();
        final String BlankLine=spacecraft.getBlankLine();
        final String DetailTaxCode=spacecraft.getDetailTaxCode();
        final int RunNo=spacecraft.getRunNo();
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vQty.setText(Qty);
        if(HCDiscount.equals("0.00")){
            holder.vUnitPrice.setText(UnitPrice);
        }else {
            holder.vUnitPrice.setText(UnitPrice+"\n ("+HCDiscount+")");
        }

        holder.vDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),spacecraft.getItemGroup(),
                        "1", spacecraft.getDescription2(),UOM,FactorQty,BlankLine,DetailTaxCode);
            }
        });
        holder.vQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),spacecraft.getItemGroup(),
                        "0",spacecraft.getDescription2(),UOM,FactorQty,BlankLine,DetailTaxCode);
            }
        });
        holder.vUnitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),spacecraft.getItemGroup(),
                        "0",spacecraft.getDescription2(),UOM,FactorQty,BlankLine,DetailTaxCode);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    private void openDialogFragment(int RunNo, String ItemCode, String Description, String Qty, String UnitPrice,
                                    String HCDiscount, String DisRate1, String ItemGroup, String ModifierYN,
                                    String Description2, String UOM, String FactorQty, String BlankLine,
                                    String DetailTaxCode){
        Bundle b=new Bundle();
        b.putString("RUNNO_KEY", String.valueOf(RunNo));
        b.putString("GROUP_KEY",ItemGroup);
        b.putString("CODE_KEY",ItemCode);
        b.putString("DESC_KEY",Description);
        b.putString("DESC2_KEY",Description2);
        b.putString("QTY_KEY",Qty);
        b.putString("UNITPRICE_KEY",UnitPrice);
        b.putString("DISAMT_KEY",HCDiscount);
        b.putString("DISRATE_KEY",DisRate1);
        b.putString("YN_KEY",ModifierYN);
        b.putString("UOM_KEY",UOM);
        b.putString("FACTORQTY_KEY",FactorQty);
        b.putString("BLANKLINE_KEY",BlankLine);
        b.putString("DETAILTAXCODE_KEY",DetailTaxCode);
        b.putString("DOCTYPE_KEY",DocType);
        Dialog_Edit dialog_edit=new Dialog_Edit();
        dialog_edit.setArguments(b);
        dialog_edit.show(fm,"EDIT");
    }

}
