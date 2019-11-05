package com.skybiz.wms02.m_OnHand;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Spacecraft_Customer;
import com.skybiz.wms02.m_DataObject.Spacecraft_OnHand;
import com.skybiz.wms02.m_Supplier.DialogSupplier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class OnHandParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_OnHand> spacecrafts=new ArrayList<>();
    DialogOnHandL dialogOnHandL;

    public OnHandParser(Context c, String jsonData, RecyclerView rv, DialogOnHandL dialogOnHandL) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogOnHandL=dialogOnHandL;
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
            OnHandAdapter adapter=new OnHandAdapter(c,spacecrafts,dialogOnHandL);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_OnHand spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String LocationCode=jo.getString("LocationCode");
                String Qty=jo.getString("Qty");
                String TotalQty=jo.getString("TotalQty");
                spacecraft=new Spacecraft_OnHand();
                spacecraft.setLocationCode(LocationCode);
                spacecraft.setQty(Qty);
                spacecraft.setTotalQty(TotalQty);
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
