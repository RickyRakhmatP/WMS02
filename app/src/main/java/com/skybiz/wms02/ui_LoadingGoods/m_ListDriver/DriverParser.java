package com.skybiz.wms02.ui_LoadingGoods.m_ListDriver;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Spacecraft_Checker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class DriverParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Checker> spacecrafts=new ArrayList<>();
    DialogDriver dialogChecker;

    public DriverParser(Context c, String jsonData, RecyclerView rv, DialogDriver dialogChecker) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogChecker=dialogChecker;
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
            DriverAdapter adapter=new DriverAdapter(c,spacecrafts,dialogChecker);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Checker spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_Checker();
                spacecraft.setCheckerCode(jo.getString("DriverCode"));
                spacecraft.setCheckerName(jo.getString("DriverName"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
