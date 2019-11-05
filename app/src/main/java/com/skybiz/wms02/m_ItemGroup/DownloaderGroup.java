package com.skybiz.wms02.m_ItemGroup;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Created by 7 on 14/11/2017.
 */

public class DownloaderGroup extends AsyncTask<Void, Void, String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType,DocType;
    JSONObject jsonReq,jsonRes;
    public DownloaderGroup(Context c, String DocType, RecyclerView rv) {
        this.c = c;
        this.DocType = DocType;
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
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        if(jsonData==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            GroupParser p=new GroupParser(c,DocType,jsonData,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    "Port,DBName,DBStatus," +
                    "CharSetType from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                Port = curSet.getString(3);
                DBName = curSet.getString(4);
                DBStatus=curSet.getString(5);
                CharType=curSet.getString(6);
            }
            if(DBStatus.equals("1")) {
                //if(ItemConn.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                db.closeDB();
                if (conn != null) {
                    String sql = "select ItemGroup, Description  from stk_group Order By Description asc";
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet resultSet = statement.getResultSet();
                        ResultSetMetaData columns = resultSet.getMetaData();
                        while (resultSet.next()) {
                            JSONObject row = new JSONObject();
                            row.put("ItemGroup",resultSet.getString(1));
                            row.put("Description", Encode.setChar(CharType,resultSet.getString(2)));
                            results.put(row);
                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
               /* }else{
                    JSONArray results=new JSONArray();
                    String sql = "select ItemGroup, Description  from stk_group Order By Description asc";
                    Cursor rsData=db.getQuery(sql);
                    while (rsData.moveToNext()) {
                        JSONObject row=new JSONObject();
                        row.put("ItemGroup",rsData.getString(0));
                        row.put("Description",rsData.getString(1));
                        results.put(row);
                    }
                    db.closeDB();
                    return results.toString();
                }*/
            }else if(DBStatus.equals("0")){
                JSONArray results=new JSONArray();
                String sql = "select ItemGroup, Description  from stk_group ";
                Cursor rsData=db.getQuery(sql);
                while (rsData.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("ItemGroup",rsData.getString(0));
                    row.put("Description",rsData.getString(1));
                    results.put(row);
                }
                db.closeDB();
                return results.toString();
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
