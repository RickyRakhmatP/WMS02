package com.skybiz.wms02.ui_LoadingGoods.m_ListDum;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDriver.DialogDriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by 7 on 16/04/2018.
 */

public class DownloadDum extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String uFrom;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;

    public DownloadDum(Context c,String uFrom, RecyclerView rv) {
        this.c = c;
        this.uFrom = uFrom;
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
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            DumParser p=new DumParser(c,uFrom,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String sql="select RunNo, Doc1No,Qty,Remark from tb_dumlist";
            Cursor rsData=db.getQuery(sql);
            JSONArray results=new JSONArray();
            int i=1;
            while(rsData.moveToNext()){
                JSONObject row=new JSONObject();
                row.put("N_o",i);
                row.put("RunNo",rsData.getString(0));
                row.put("Doc1No",rsData.getString(1));
                row.put("Qty",rsData.getString(2));
                row.put("Remark",rsData.getString(3));
                i++;
                results.put(row);

            }
            db.closeDB();
            Log.d("RESULT JSON",results.toString());
            return results.toString();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
