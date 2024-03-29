package com.skybiz.wms02.m_ItemList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ItemParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;

    ArrayList<Spacecraft> spacecrafts=new ArrayList<>();
    DialogItem dialogItem;

    public ItemParser(Context c, String jsonData, String DocType, RecyclerView rv, DialogItem dialogItem) {
        this.c = c;
        this.jsonData = jsonData;
        this.DocType = DocType;
        this.rv = rv;
        this.dialogItem=dialogItem;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return this.parseData();
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(result==0){
            Toast.makeText(c,"Unable to parse", Toast.LENGTH_SHORT).show();
        }else{
            ItemAdapter adapter=new ItemAdapter(c,spacecrafts,DocType,dialogItem);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft();
                spacecraft.setItemCode(jo.getString("ItemCode"));
                spacecraft.setItemGroup(jo.getString("ItemGroup"));
                spacecraft.setDescription(jo.getString("Description"));
                spacecraft.setUnitPrice(jo.getString("UnitPrice"));
                spacecraft.setBtnQty("Qty");
                spacecraft.setCurCode("CurCode");
                spacecraft.setPrinter(jo.getString("Printer"));
                spacecraft.setModifier(jo.getString("Modifier1"));
                spacecraft.setRetailTaxCode(jo.getString("RetailTaxCode"));
                spacecraft.setDefaultUOM(jo.getString("DefaultUOM"));
                spacecraft.setUOM(jo.getString("UOM"));
                spacecraft.setUOM1(jo.getString("UOM1"));
                spacecraft.setUOM2(jo.getString("UOM2"));
                spacecraft.setUOM3(jo.getString("UOM3"));
                spacecraft.setUOM4(jo.getString("UOM4"));
                spacecraft.setUOMFactor1(jo.getString("UOMFactor1"));
                spacecraft.setUOMFactor2(jo.getString("UOMFactor2"));
                spacecraft.setUOMFactor3(jo.getString("UOMFactor3"));
                spacecraft.setUOMFactor4(jo.getString("UOMFactor4"));
                spacecraft.setUOMPrice1(jo.getString("UOMPrice1"));
                spacecraft.setUOMPrice2(jo.getString("UOMPrice2"));
                spacecraft.setUOMPrice3(jo.getString("UOMPrice3"));
                spacecraft.setUOMPrice4(jo.getString("UOMPrice4"));
                spacecraft.setAlternateItem(jo.getString("AlternateItem"));
                spacecraft.setSuspendedYN(jo.getString("SuspendedYN"));
                spacecraft.setGroupDesc(jo.getString("GroupDesc"));
                spacecraft.setPurchaseTaxCode(jo.getString("PurchaseTaxCode"));
                spacecraft.setSalesTaxCode(jo.getString("SalesTaxCode"));
                spacecraft.setRemark(jo.getString("Remark"));
                spacecraft.setBaseCode(jo.getString("BaseCode"));
                spacecraft.setUOMCode1(jo.getString("UOMCode1"));
                spacecraft.setUOMCode2(jo.getString("UOMCode2"));
                spacecraft.setUOMCode3(jo.getString("UOMCode3"));
                spacecraft.setUOMCode4(jo.getString("UOMCode4"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
