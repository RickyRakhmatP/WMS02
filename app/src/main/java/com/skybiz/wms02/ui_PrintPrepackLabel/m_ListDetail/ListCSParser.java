package com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Spacecarft_Trn;
import com.skybiz.wms02.m_DataObject.Spacecraft_Detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */
public class ListCSParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;
    ArrayList<Spacecraft_Detail> spacecrafts=new ArrayList<>();

    public ListCSParser(Context c, String jsonData, RecyclerView rv) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
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
            ListCSAdapter adapter=new ListCSAdapter(c,spacecrafts);
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
                spacecraft=new Spacecraft_Detail();
                spacecraft.setItemCode(jo.getString("ItemCode"));
                spacecraft.setDescription(jo.getString("Description"));
                spacecraft.setBtnQty(jo.getString("Qty"));
                spacecraft.setFactorQty(jo.getString("Qty"));
                spacecraft.setUOM(jo.getString("UOM"));
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
