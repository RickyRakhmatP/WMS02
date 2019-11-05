package com.skybiz.wms02.ui_PickingList.m_List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.ui_PickingList.PickingList;

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

public class ListDownload extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    String CurCode,DocType;
    int i=0;

    public ListDownload(Context c, RecyclerView rv) {
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
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            if(i>0){
                ((PickingList)c).enableSave();
            }else{
                ((PickingList)c).disableSave();
            }
            ListParser p=new ListParser(c,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String sql = "select Doc1No,RunNo,Qty from tb_dumpicking Order By RunNo Desc";
            Cursor rsDum=db.getQuery(sql);
            JSONArray results = new JSONArray();
            i=0;
            while (rsDum.moveToNext()) {
                JSONObject row = new JSONObject();
                row.put("Doc1No", rsDum.getString(0));
                row.put("RunNo", rsDum.getString(1));
                row.put("Qty", rsDum.getString(2));
                results.put(row);
                i++;
            }
            db.closeDB();
            Log.d("RESULT JSON", results.toString());
            return results.toString();
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
