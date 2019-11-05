package com.skybiz.wms02.ui_LoadingGoods.m_ListDum;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;
import com.skybiz.wms02.m_DataObject.Spacecraft_Dum;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDriver.DialogDriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class DumParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,uFrom;
    RecyclerView rv;

    ArrayList<Spacecraft_Dum> spacecrafts=new ArrayList<>();

    public DumParser(Context c,String uFrom, String jsonData, RecyclerView rv) {
        this.c = c;
        this.uFrom=uFrom;
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
            DumAdapter adapter=new DumAdapter(c,uFrom,spacecrafts);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Dum spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_Dum();
                spacecraft.setRunNo(jo.getString("RunNo"));
                spacecraft.setN_o(jo.getString("N_o"));
                spacecraft.setDoc1No(jo.getString("Doc1No"));
                spacecraft.setQty(jo.getString("Qty"));
                spacecraft.setRemark(jo.getString("Remark"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
