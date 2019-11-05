package com.skybiz.wms02.m_ItemList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_DataObject.SetDefaultTax;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import com.skybiz.wms02.m_DataObject.Spacecraft_Item;
import java.util.ArrayList;
import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Detail.AddItem;
import com.skybiz.wms02.ui_StockEnquiry.StockEnquiry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    DialogItem dialogItem;
    String DocType;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts, String DocType, DialogItem dialogItem) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.DocType=DocType;
        this.dialogItem=dialogItem;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemlist,parent,false);
        return new ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vUnitPrice.setText(spacecraft.getUnitPrice());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
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
                if(DocType.equals("ST")) {
                    ((StockTake)c).setItem(spacecraft.getItemCode());
                }else if(DocType.equals("SE")){
                    try {
                        JSONArray results = new JSONArray();
                        JSONObject row = new JSONObject();
                        row.put("ItemCode", spacecraft.getItemCode());
                        row.put("ItemGroup", spacecraft.getItemGroup());
                        row.put("Description", spacecraft.getDescription());
                        row.put("UnitPrice", spacecraft.getUnitPrice());
                        row.put("UOM",spacecraft.getUOM());
                        row.put("UOM1",spacecraft.getUOM1());
                        row.put("UOM2",spacecraft.getUOM2());
                        row.put("UOM3",spacecraft.getUOM3());
                        row.put("UOM4",spacecraft.getUOM4());
                        row.put("UOMFactor1",spacecraft.getUOMFactor1());
                        row.put("UOMFactor2",spacecraft.getUOMFactor2());
                        row.put("UOMFactor3",spacecraft.getUOMFactor3());
                        row.put("UOMFactor4",spacecraft.getUOMFactor4());
                        row.put("UOMPrice1",spacecraft.getUOMPrice1());
                        row.put("UOMPrice2",spacecraft.getUOMPrice2());
                        row.put("UOMPrice3",spacecraft.getUOMPrice3());
                        row.put("UOMPrice4",spacecraft.getUOMPrice4());
                        row.put("PurchaseTaxCode",spacecraft.getPurchaseTaxCode());
                        row.put("SalesTaxCode",spacecraft.getSalesTaxCode());
                        row.put("RetailTaxCode",spacecraft.getRetailTaxCode());
                        row.put("Remark",spacecraft.getRemark());
                        row.put("SuspendedYN",spacecraft.getSuspendedYN());
                        row.put("AlternateItem",spacecraft.getAlternateItem());
                        row.put("GroupDesc",spacecraft.getGroupDesc());
                        results.put(row);
                        ((StockEnquiry)c).setItem(results.toString());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    AddItem addItem = new AddItem(c, DocType, vItemCode, vDescription, "1", UnitPrice, vUOM, vDetailTaxCode, "0", "0", "0");
                    addItem.execute();
                }
                dialogItem.setItem();
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }


}
