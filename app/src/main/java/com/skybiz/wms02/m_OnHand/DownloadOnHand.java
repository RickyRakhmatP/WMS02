package com.skybiz.wms02.m_OnHand;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Supplier.DialogSupplier;

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

public class DownloadOnHand extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemCode,TotalQty;
    RecyclerView rv;
    String IPAddress,UserName,Password,
            DBName,Port,URL,z,
            DBStatus,LocationCode;
    DialogOnHandL dialogOnHandL;

    public DownloadOnHand(Context c, String ItemCode, RecyclerView rv, DialogOnHandL dialogOnHandL) {
        this.c = c;
        this.ItemCode = ItemCode;
        this.rv = rv;
        this.dialogOnHandL=dialogOnHandL;
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
            OnHandParser p=new OnHandParser(c,result,rv,dialogOnHandL);
            p.execute();
            dialogOnHandL.setTotal(TotalQty);
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password," +
                    "DBName, Port, DBStatus, " +
                    "LocationCode from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        = curSet.getString(5);
                LocationCode    = curSet.getString(6);
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sqlLoc="select LocationCode " +
                            "from stk_itemlocation where LocationCode<>'' ";
                    JSONArray results = new JSONArray();
                    Statement stmtLoc = conn.createStatement();
                    if (stmtLoc.execute(sqlLoc)) {
                        ResultSet rsLoc = stmtLoc.getResultSet();
                        Double dTotalQty=0.00;
                        while (rsLoc.next()) {
                            String LocationCode=rsLoc.getString(1);
                            JSONObject row = new JSONObject();
                            String QtyIN="SELECT SUM(QtyIN*FactorQty) AS QtyIN " +
                                    "FROM stk_detail_trn_in " +
                                    "WHERE ItemCode='"+ItemCode+"' and LocationCode='"+LocationCode+"' ";
                            Statement stmtIN = conn.createStatement();
                            Double dQtyIN=0.00;
                            if (stmtIN.execute(QtyIN)) {
                                ResultSet rsQtyIN = stmtIN.getResultSet();
                                while (rsQtyIN.next()) {
                                    dQtyIN=rsQtyIN.getDouble(1);
                                }
                            }

                            String QtyOut="SELECT SUM(QtyOUT*FactorQty) AS QtyOUT " +
                                    "FROM stk_detail_trn_out " +
                                    "WHERE ItemCode='"+ItemCode+"' " +
                                    "AND LocationCode='"+LocationCode+"' AND VoidYN='0' ";
                            Log.d("QtyOUT",QtyOut);
                            Statement stmtOut = conn.createStatement();
                            Double dQtyOut=0.00;
                            if (stmtOut.execute(QtyOut)) {
                                ResultSet rsQtyOut= stmtOut.getResultSet();
                                while (rsQtyOut.next()) {
                                    dQtyOut=rsQtyOut.getDouble(1);
                                }
                            }
                            Double dQty=dQtyIN-dQtyOut;
                            dTotalQty+=dQty;
                            TotalQty=twoDecimal(dTotalQty);
                            row.put("LocationCode",LocationCode);
                            row.put("Qty",twoDecimal(dQty));
                            row.put("TotalQty","0.00");
                            results.put(row);
                        }
                    }
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                /*String vQuery="Select CusCode,CusName,TermCode,D_ay, SalesPersonCode " +
                        "from customer where CusName like '%"+Query+"%' ";
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
                return results2.toString();*/
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
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
