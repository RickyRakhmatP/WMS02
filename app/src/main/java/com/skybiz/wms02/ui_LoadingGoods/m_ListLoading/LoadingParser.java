package com.skybiz.wms02.ui_LoadingGoods.m_ListLoading;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;
import com.skybiz.wms02.m_DataObject.Spacecraft_Loading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class LoadingParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Loading> spacecrafts=new ArrayList<>();
    DialogLoading dialogLoading;

    public LoadingParser(Context c, String jsonData, RecyclerView rv, DialogLoading dialogLoading) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogLoading=dialogLoading;
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
            LoadingAdapter adapter=new LoadingAdapter(c,spacecrafts,dialogLoading);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Loading spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_Loading();
                spacecraft.setRunNo(jo.getString("RunNo"));
                spacecraft.setBay(jo.getString("Bay"));
                spacecraft.setCheckerCode(jo.getString("CheckerCode"));
                spacecraft.setDriverCode(jo.getString("DriverCode"));
                spacecraft.setLorryNo(jo.getString("LorryNo"));
                spacecraft.setD_ateTime(jo.getString("D_ateTimeOUT"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
