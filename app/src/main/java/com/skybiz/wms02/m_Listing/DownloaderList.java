package com.skybiz.wms02.m_Listing;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Locale;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderList extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    String CurCode,DocType;

    public DownloaderList(Context c, String DocType,RecyclerView rv) {
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
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            ListParser p=new ListParser(c,DocType,result,rv);
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
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql;
                    if(DocType.equals("GRNDO")) {
                        sql = "select H.Doc1No,H.D_ate,H.HCNetAmt, sum(D.Qty) as Qty, C.CusName, '1' as SyncYN " +
                                "from stk_grndo_hd H inner join stk_grndo_dt D ON H.Doc1No=D.Doc1No " +
                                "left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "'" +
                                "Group By H.Doc1No Order By H.D_ate desc ";
                    }else if(DocType.equals("RS")){
                        sql = "select H.Doc1No,H.D_ate,H.HCNetAmt, sum(D.QtyIN) as Qty, C.CusName, '1' as SyncYN " +
                                "from stk_inventory_hd H inner join stk_inventory_dt D ON H.Doc1No=D.Doc1No " +
                                "left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "'" +
                                "Group By H.Doc1No Order By H.D_ate desc ";
                    }else{
                        sql = "select H.Doc1No,H.D_ate,H.HCNetAmt, sum(D.QtyOUT) as Qty, C.CusName, '1' as SyncYN " +
                                "from stk_inventory_hd H inner join stk_inventory_dt D ON H.Doc1No=D.Doc1No " +
                                "left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "'" +
                                "Group By H.Doc1No Order By H.D_ate desc ";
                    }
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet rsData = statement.getResultSet();
                        while (rsData.next()) {
                            JSONObject row = new JSONObject();
                            row.put("Doc1No", rsData.getString(1));
                            row.put("D_ate", rsData.getString(2));
                            row.put("HCNetAmt", twoDecimal(rsData.getDouble(3)));
                            row.put("TotalQty", twoDecimal(rsData.getDouble(4)));
                            row.put("CusName", rsData.getString(5));
                            row.put("SyncYN", rsData.getString(6));
                            results.put(row);
                        }
                        return results.toString();
                    }
                }
            }else {
                String sql;
                if(DocType.equals("GRNDO")) {
                    sql = "select H.Doc1No,H.D_ate,H.HCNetAmt, sum(D.Qty) as Qty, C.CusName, H.SyncYN " +
                            "from stk_grndo_hd H inner join stk_grndo_dt D ON H.Doc1No=D.Doc1No " +
                            "left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "'" +
                            "Group By H.Doc1No Order By H.RunNo desc ";
                }else if(DocType.equals("RS")){
                    sql = "select H.Doc1No,H.D_ate,H.HCNetAmt, sum(D.QtyIN) as Qty, C.CusName, H.SyncYN " +
                            "from stk_inventory_hd H inner join stk_inventory_dt D ON H.Doc1No=D.Doc1No " +
                            "left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "'" +
                            "Group By H.Doc1No Order By H.RunNo desc ";
                }else{
                    sql = "select H.Doc1No,H.D_ate,H.HCNetAmt, sum(D.QtyOUT) as Qty, C.CusName, H.SyncYN " +
                            "from stk_inventory_hd H inner join stk_inventory_dt D ON H.Doc1No=D.Doc1No " +
                            "left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "'" +
                            "Group By H.Doc1No Order By H.RunNo desc ";
                }

                Cursor rsData = db.getQuery(sql);
                JSONArray results2 = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row2 = new JSONObject();
                    Double dHCNetAmt = rsData.getDouble(2);
                    String HCNetAmt = String.format(Locale.US, "%,.2f", dHCNetAmt);
                    row2.put("Doc1No", rsData.getString(0));
                    row2.put("D_ate", rsData.getString(1));
                    row2.put("HCNetAmt", HCNetAmt);
                    row2.put("TotalQty", rsData.getString(3));
                    row2.put("CusName", rsData.getString(4));
                    row2.put("SyncYN", rsData.getString(5));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON", results2.toString());
                return results2.toString();
            }
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
    }
}
