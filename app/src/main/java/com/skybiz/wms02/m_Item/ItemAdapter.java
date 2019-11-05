package com.skybiz.wms02.m_Item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.SetDefaultTax;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import com.skybiz.wms02.m_Detail.AddItem;
import com.skybiz.wms02.m_Detail.EditItem;


/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    String IPAddress,UserName,Password,DBName,Qty,DocType;
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int clickcount=0;

    public ItemAdapter(Context c, String DocType, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item,parent,false);
        return new ItemHolder(v);

    }
    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {

        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.ItemCodetxt.setText(spacecraft.getItemCode());
        holder.Descriptiontxt.setText(spacecraft.getDescription());
        holder.txtUnitPrice.setText(spacecraft.getCurCode()+spacecraft.getUnitPrice());
        Qty=spacecraft.getBtnQty();
        final int dqty= Integer.parseInt(Qty);;
        if(Qty.equals("0")) {
            holder.btnAdd.setText("+");
        }else{
            holder.btnAdd.setText(Qty);
            spacecraft.setId(dqty);
        }
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                spacecraft.setId(spacecraft.getId()+1);
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vDescription=spacecraft.getDescription();
                final String vItemGroup=spacecraft.getItemGroup();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vDetailTaxCode= SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                AddItem addItem =new AddItem(c,DocType,vItemCode,vDescription,"1",UnitPrice,vUOM,vDetailTaxCode,"0","0","0");
                addItem.execute();
                holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final int qty= spacecraft.getId();
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vItemGroup=spacecraft.getItemGroup();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vDetailTaxCode= SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                if(qty!=0){
                    EditItem editItem=new EditItem(c,DocType,vItemCode,"-1",UnitPrice,vUOM,"1",vDetailTaxCode,"","","0");
                    editItem.execute();
                    spacecraft.setId(spacecraft.getId()-1);
                    holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
                }else{
                    holder.btnAdd.setText("+");
                }
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
