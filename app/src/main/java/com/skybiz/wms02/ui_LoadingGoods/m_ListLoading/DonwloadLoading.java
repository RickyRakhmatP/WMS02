package com.skybiz.wms02.ui_LoadingGoods.m_ListLoading;

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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by 7 on 16/04/2018.
 */

public class DonwloadLoading extends AsyncTask<Void,Void,String> {
    Context c;
    String D_ate,Keyword;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    DialogLoading dialogLoading;

    public DonwloadLoading(Context c, String D_ate, String Keyword,
                           RecyclerView rv, DialogLoading dialogLoading) {
        this.c = c;
        this.D_ate=D_ate;
        this.Keyword = Keyword;
        this.rv = rv;
        this.dialogLoading=dialogLoading;
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
            LoadingParser p=new LoadingParser(c,result,rv,dialogLoading);
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
            }
            String vClause="";
            if(!Keyword.isEmpty()) {
                vClause="";
            }

            String sql ="Select RunNo, Bay, CheckerCode, DriverCode, " +
                    "LorryNo, DATE_FORMAT(D_ateTimeOUT,'%Y-%m-%d %H:%i:%s') as D_ateTimeOUT " +
                    "from wms_loading_hd where SUBSTR(D_ateTimeOUT,1,10)='"+D_ate+"' "+vClause+" " +
                    "Order By D_ateTimeOUT DESC ";
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Log.d("QUERY",sql);
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    if (statement.execute(sql)) {
                        ResultSet resultSet = statement.getResultSet();
                        ResultSetMetaData columns = resultSet.getMetaData();
                        while (resultSet.next()) {
                            JSONObject row = new JSONObject();
                            for (int i = 1; i <= columns.getColumnCount(); i++) {
                                row.put(columns.getColumnName(i), resultSet.getObject(i));
                            }
                            results.put(row);
                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                Cursor rsData=db.getQuery(sql);
                JSONArray results=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row=new JSONObject();
                    row.put("RunNo",rsData.getString(0));
                    row.put("Bay",rsData.getString(1));
                    row.put("CheckerCode",rsData.getString(2));
                    row.put("DriverCode",rsData.getString(3));
                    row.put("LorryNo",rsData.getString(4));
                    row.put("D_ateTimeOUT",rsData.getString(5));
                    results.put(row);
                }
                db.closeDB();
                Log.d("RESULT JSON",results.toString());
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
