package com.skybiz.wms02.m_Detail;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.skybiz.wms02.m_DataObject.Spacecraft_Detail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;



/**
 * Created by 7 on 27/10/2017.
 */

public class OrderParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;
    FragmentManager fm;
    ArrayList<Spacecraft_Detail> spacecrafts=new ArrayList<>();

    public OrderParser(Context c, String DocType, String jsonData, RecyclerView rv, FragmentManager fm) {
        this.c = c;
        this.DocType = DocType;
        this.jsonData = jsonData;
        this.rv = rv;
        this.fm=fm;
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
            OrderAdapter adapter=new OrderAdapter(c,DocType,spacecrafts,fm);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Detail spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                int RunNo=jo.getInt("RunNo");
                String ItemCode=jo.getString("ItemCode");
                String Description=jo.getString("Description");
                String UnitPrice=jo.getString("HCUnitCost");
                String HCDiscount=jo.getString("HCDiscount");
                String DisRate1=jo.getString("DisRate1");
                String Qty=jo.getString("QTY");
                String UOM=jo.getString("UOM");
                String FactorQty=jo.getString("FactorQty");
                String BlankLine=jo.getString("BlankLine");
                String DetailTaxCode=jo.getString("DetailTaxCode");
                spacecraft=new Spacecraft_Detail();
                spacecraft.setRunNo(RunNo);
                spacecraft.setItemCode(ItemCode);
                spacecraft.setDescription(Description);
                spacecraft.setHCUnitCost(UnitPrice);
                spacecraft.setHCDiscount(HCDiscount);
                spacecraft.setDisRate1(DisRate1);
                spacecraft.setBtnQty(Qty);
                spacecraft.setUOM(UOM);
                spacecraft.setFactorQty(FactorQty);
                spacecraft.setBlankLine(BlankLine);
                spacecraft.setDetailTaxCode(DetailTaxCode);
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
