package com.skybiz.wms02.m_StockTake.m_List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
            String query="select RunNo, ItemCode, CheckQty, " +
                    "D_ateTime,UOM from dumlist order by RunNo Desc ";
            Cursor rsData=db.getQuery(query);
            JSONArray results = new JSONArray();
            while(rsData.moveToNext()){
                JSONObject row = new JSONObject();
                row.put("RunNo",rsData.getString(0));
                row.put("ItemCode",rsData.getString(1));
                row.put("ChkQty",rsData.getString(2));
                row.put("D_ateTime",rsData.getString(3));
                row.put("UOM",rsData.getString(4));
                results.put(row);
            }
            db.closeDB();
            return results.toString();
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
