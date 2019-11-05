package com.skybiz.wms02.ui_GRNReceiving.m_ListDum;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderList extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;

    public DownloaderList(Context c, RecyclerView rv) {
        this.c = c;
        this.rv = rv;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Failure, download list", Toast.LENGTH_SHORT).show();
        }else{
            ListParser p=new ListParser(c,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String query="select RunNo, ItemCode, Description, " +
                    " AlternateItem, SupplierItemCode, BaseCode," +
                    " Qty, Remark " +
                    " from tb_dumgrn_receiving" +
                    " order by RunNo Desc ";
            Cursor rsData=db.getQuery(query);
            JSONArray results = new JSONArray();
            while(rsData.moveToNext()){
                JSONObject row = new JSONObject();
                row.put("RunNo",rsData.getString(0));
                row.put("ItemCode",rsData.getString(1));
                row.put("Description",rsData.getString(2));
                row.put("AlternateItem",rsData.getString(3));
                row.put("SupplierItemCode",rsData.getString(4));
                row.put("BaseCode",rsData.getString(5));
                row.put("Qty",rsData.getString(6));
                row.put("Remark",rsData.getString(7));
                results.put(row);
            }
            db.closeDB();
            Log.d("JSON",results.toString());
            return results.toString();
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
