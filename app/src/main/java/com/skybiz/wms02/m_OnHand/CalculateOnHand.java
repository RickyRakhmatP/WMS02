package com.skybiz.wms02.m_OnHand;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class CalculateOnHand extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemCode,LocationCode,QtyCur;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;
    String TotalOnHand;

    public CalculateOnHand(Context c, String itemCode, String locationCode, String QtyCur) {
        this.c = c;
        ItemCode = itemCode;
        LocationCode = locationCode;
        this.QtyCur=QtyCur;
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
            Toast.makeText(c,"Error, calculate on hand", Toast.LENGTH_SHORT).show();
        }else{
            //
        }
    }

    private String downloadData() {
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet = "select * from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
                Port = curSet.getString(5);
                DBStatus = curSet.getString(7);
            }
            if (DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
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
                            "WHERE ItemCode='"+ItemCode+"' and LocationCode='"+LocationCode+"' ";
                    Statement stmtOut = conn.createStatement();
                    Double dQtyOut=0.00;
                    if (stmtOut.execute(QtyOut)) {
                        ResultSet rsQtyOut= stmtOut.getResultSet();
                        while (rsQtyOut.next()) {
                            dQtyOut=rsQtyOut.getDouble(1);
                        }
                    }
                    Log.d("QTY", dQtyIN+" - "+dQtyOut);
                    //Double dQtyCur=Double.parseDouble(QtyCur);
                    //Double dTotalOnHand=dQtyIN-dQtyOut-dQtyCur;
                    Double dTotalOnHand=dQtyIN-dQtyOut;
                    TotalOnHand     = String.format(Locale.US, "%,.2f", dTotalOnHand);
                    z=TotalOnHand;
                }
            }
            db.closeDB();
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
}
