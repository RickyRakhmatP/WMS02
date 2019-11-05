package com.skybiz.wms02.m_Location;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Spacecraft_Loc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
/**
 * Created by 7 on 27/10/2017.
 */

public class LocParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;

    ArrayList<Spacecraft_Loc> spacecrafts=new ArrayList<>();
    DialogLoc dialogLoc;

    public LocParser(Context c, String jsonData, RecyclerView rv, DialogLoc dialogLoc) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogLoc=dialogLoc;
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
            LocAdapter adapter=new LocAdapter(c,spacecrafts,dialogLoc);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Loc spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String LocationCode=jo.getString("LocationCode");
                String LocationName=jo.getString("LocationName");
                spacecraft=new Spacecraft_Loc();
                spacecraft.setLocationCode(LocationCode);
                spacecraft.setLocationName(LocationName);
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
