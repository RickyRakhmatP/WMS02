package com.skybiz.wms02.m_Supplier;

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

public class DownloadSupplier extends AsyncTask<Void,Void,String> {
    Context c;
    String SearchBy,Keyword,vDBStatus;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    DialogSupplier dialogCustomer;

    public DownloadSupplier(Context c, String vDBStatus, String SearchBy,
                            String Keyword, RecyclerView rv, DialogSupplier dialogCustomer) {
        this.c = c;
        this.vDBStatus = vDBStatus;
        this.SearchBy = SearchBy;
        this.Keyword = Keyword;
        this.rv = rv;
        this.dialogCustomer=dialogCustomer;
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
            SupplierParser p=new SupplierParser(c,result,rv,dialogCustomer);
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
            if(vDBStatus.equals("1")){
                DBStatus="1";
            }

            String vClause="";
            if(SearchBy.equals("By CusCode")){
                vClause=" and CusCode like '%"+Keyword+"%' ";
            }else if(SearchBy.equals("By CusName")){
                vClause=" and CusName like '%"+Keyword+"%' ";
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql ="Select CusCode,CusName,TermCode,D_ay,IFNULL(SalesPersonCode,'') as SalesPersonCode " +
                            "from customer where FinCatCode='B70' and StatusBadYN='0' "+vClause+" ";
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
                String vQuery="Select CusCode,CusName,TermCode,D_ay, SalesPersonCode " +
                        "from customer where CusCode<>''  "+vClause+" ";
                Cursor rsData=db.getQuery(vQuery);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("CusCode",rsData.getString(0));
                    row2.put("CusName",rsData.getString(1));
                    row2.put("TermCode",rsData.getString(2));
                    row2.put("D_ay",rsData.getString(3));
                    row2.put("SalesPersonCode",rsData.getString(4));
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
