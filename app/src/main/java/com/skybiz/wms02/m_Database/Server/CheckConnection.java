package com.skybiz.wms02.m_Database.Server;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by 7 on 21/11/2017.
 */

public class CheckConnection extends AsyncTask<Void, String, String> {
    Context c;
    String IPAddress,DBName,UserName,Password,Port,URL,z;
    Boolean isSuccess = false;

    public CheckConnection(Context c, String IPAddress,String UserName,
                           String DBName,String Password, String Port) {
        this.c = c;
        this.IPAddress = IPAddress;
        this.UserName = UserName;
        this.DBName = DBName;
        this.Password = Password;
        this.Port = Port;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName;
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {
                z = "error";
                isSuccess = false;
            } else {
                String del = "delete from companysetup";
                db.exeQuery(del);
                String vCompSetup = "Select CompanyCode, CompanyName, Address," +
                        " ComTown, ComState, ComCountry," +
                        "Tel1, Fax1, CompanyEmail," +
                        "IFNULL(GSTNo,'') as GSTNo, CurCode  from  companysetup";
                Statement stmtCom = conn.createStatement();
                stmtCom.execute(vCompSetup);
                ResultSet rsCom = stmtCom.getResultSet();
                ContentValues cvCom = new ContentValues();
                int i = 1;
                while (rsCom.next()) {
                    cvCom.put("CompanyCode", rsCom.getString("CompanyCode"));
                    cvCom.put("CompanyName", rsCom.getString("CompanyName"));
                    cvCom.put("Address", rsCom.getString("Address"));
                    cvCom.put("ComTown", rsCom.getString("ComTown"));
                    cvCom.put("ComState", rsCom.getString("ComState"));
                    cvCom.put("ComCountry", rsCom.getString("ComCountry"));
                    cvCom.put("Tel1", rsCom.getString("Tel1"));
                    cvCom.put("Fax1", rsCom.getString("Fax1"));
                    cvCom.put("CompanyEmail", rsCom.getString("CompanyEmail"));
                    cvCom.put("GSTNo", rsCom.getString("GSTNo"));
                    cvCom.put("CurCode", rsCom.getString("CurCode"));
                    db.addQuery("companysetup", cvCom);
                    i++;
                }
                z = "success";
                isSuccess = true;
            }
            return z;
        } catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return z;
    }
    @Override
    protected void onPostExecute(String isConnect) {
        super.onPostExecute(isConnect);
        if(isSuccess==false){
            Toast.makeText(c,"Connection Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"You have a successful connection", Toast.LENGTH_SHORT).show();

        }
    }
}
