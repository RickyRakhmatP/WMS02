package com.skybiz.wms02.m_Location;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderLoc extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,vDBStatus;
    DialogLoc dialogLoc;

    public DownloaderLoc(Context c,String vDBStatus, RecyclerView rv, DialogLoc dialogLoc) {
        this.c = c;
        this.vDBStatus = vDBStatus;
        this.rv = rv;
        this.dialogLoc=dialogLoc;
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
            LocParser p=new LocParser(c,result,rv,dialogLoc);
            p.execute();
        }
    }

    private String downloadData(){
        try {

            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select * from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
                Port = curSet.getString(5);
                DBStatus=curSet.getString(7);
                ItemConn=curSet.getString(8);
            }
            if(vDBStatus.equals("1")){
                DBStatus="1";
            }else if(vDBStatus.equals("0")){
                DBStatus="0";
            }
            Log.d("DB Status", DBStatus);
            String sql = "select LocationCode,LocationName from stk_itemlocation  Order By LocationName";
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                JSONArray results = new JSONArray();
                Statement statement = conn.createStatement();
                statement.executeQuery("SET NAMES 'LATIN1'");
                statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                if (statement.execute(sql)) {
                    ResultSet rsLoc = statement.getResultSet();
                    while (rsLoc.next()) {
                        JSONObject row = new JSONObject();
                        row.put("LocationCode",rsLoc.getString(1));
                        row.put("LocationName",rsLoc.getString(2));
                        results.put(row);
                    }
                    rsLoc.close();
                    statement.close();
                    Log.d("JSON",results.toString());
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                Cursor rsLoc=db.getQuery(sql);
                JSONArray results2=new JSONArray();
                while(rsLoc.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("LocationCode",rsLoc.getString(0));
                    row2.put("LocationName",rsLoc.getString(1));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
