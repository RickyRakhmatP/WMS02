package com.skybiz.wms02.m_UOM;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.skybiz.wms02.m_DataObject.Spacecraft_UOM;
import com.skybiz.wms02.m_Detail.Dialog_Edit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class UOMParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_UOM> spacecrafts=new ArrayList<>();
    Dialog_Edit dialog_edit;

    public UOMParser(Context c, String jsonData, RecyclerView rv, Dialog_Edit dialog_edit) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialog_edit=dialog_edit;
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
            UOMAdapter adapter=new UOMAdapter(c,spacecrafts,dialog_edit);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_UOM spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String UOMType=jo.getString("UOMType");
                String UOM=jo.getString("UOM");
                String UOMPrice=jo.getString("vPrice");
                String UOMFactor=jo.getString("UOMFactor");
                spacecraft=new Spacecraft_UOM();
                spacecraft.setUOMType(UOMType);
                spacecraft.setUOM(UOM);
                spacecraft.setUOMPrice(UOMPrice);
                spacecraft.setUOMFactor(UOMFactor);
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
