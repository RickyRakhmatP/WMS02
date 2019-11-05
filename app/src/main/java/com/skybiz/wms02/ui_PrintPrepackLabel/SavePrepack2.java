package com.skybiz.wms02.ui_PrintPrepackLabel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Print.By_USB.PrintingUSB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavePrepack2 extends AsyncTask<Void,Void,String> {

    Context c;
    String Doc1No,Qty,ComName,CusName,Address,D_ate;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    String jsonData;

    public SavePrepack2(Context c, String doc1No, String qty,
                        String CusName, String Address, String D_ate) {
        this.c = c;
        Doc1No = doc1No;
        Qty = qty;
        this.CusName= CusName;
        this.Address= Address;
        this.D_ate= D_ate;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.savePrepack();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("success")){
            Toast.makeText(c,"Save Prepack Successfull", Toast.LENGTH_SHORT).show();
            ((PrintPrepackLabel2)c).setPrint(c,jsonData);
        }else{
            Toast.makeText(c,"Save Prepack Failure", Toast.LENGTH_SHORT).show();

        }
    }

    private String savePrepack() {
        Cursor curSet=null;
        DBAdapter db=null;
        try {
            z="";
            String MacAddress= Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);
            MacAddress=MacAddress.substring(0,3);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String D_ateTime = sdf.format(date);
            db=new DBAdapter(c);
            db.openDB();
           /* String sqlCom = "select CompanyName from companysetup ";
            Cursor rsCom = db.getQuery(sqlCom);
            while (rsCom.moveToNext()) {
                ComName=rsCom.getString(0);
            }
            db.closeDB();*/

         //   rsCom.close();
            String querySet="select ServerName,UserName,Password," +
                    "Port,DBName,DBStatus," +
                    "CharSetType from tb_setting";
            curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                Port = curSet.getString(3);
                DBName = curSet.getString(4);
                DBStatus=curSet.getString(5);
                CharType=curSet.getString(6);
            }
            //curSet.close();
           // db.closeDB();
            //curSet.close();
            String insertHd = "insert into stk_prepack_hd(Doc1No,D_ateTime,Qty)" +
                    "values('"+Doc1No+"', '"+D_ateTime+"', '"+Qty+"')  ";

            String newPrefix="1"+MacAddress;
            int newNo=1000000000;

            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement stmt=conn.createStatement();
                    stmt.execute(insertHd);
                    int numrows=0;

                    String qCheck="select count(*)as numrows from stk_prepack_dt " +
                            "where SUBSTR(PrepackID,1,4)='"+newPrefix+"'  ";
                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                        numrows=rsCheck.getInt(1);
                    }
                    JSONArray results = new JSONArray();
                    if(numrows>0) {
                        String checkNo = "select SUBSTR(PrepackID,5,9) as PrepackID from stk_prepack_dt " +
                                " where SUBSTR(PrepackID,1,4)='" + newPrefix + "' order by RunNo Desc limit 1";
                        Statement stmtDt = conn.createStatement();
                        stmtDt.execute(checkNo);
                        ResultSet rsDt = stmtDt.getResultSet();
                        while (rsDt.next()) {
                            String PrepackID    ="1"+ rsDt.getString(1);
                            newNo               = (Integer.parseInt(PrepackID));
                            Log.d("QUERY","new no"+newNo);
                        }
                        int newID=newNo;
                        for(int i=0;i<Integer.parseInt(Qty);i++){
                            newID=newID+1;
                            String NewID = String.valueOf(newID);
                            String vNewID = NewID.substring(1,NewID.length());
                            String PrepackID=newPrefix+vNewID;
                            String PrepackFactor=i+1+"/"+Qty;
                            String insertDt="insert into stk_prepack_dt(Doc1No,PrepackID,PrepackFactor)values(" +
                                    "'"+Doc1No+"', '"+PrepackID+"', '"+PrepackFactor+"')";
                            Statement stmtIn2=conn.createStatement();
                            stmtIn2.execute(insertDt);
                            JSONObject row = new JSONObject();
                            row.put("Doc1No",Doc1No);
                            row.put("CusName",CusName);
                            row.put("Address",Address);
                            row.put("PrepackFactor",PrepackFactor);
                            row.put("PrepackID",PrepackID);
                            row.put("D_ate",D_ate);
                            results.put(row);
                            //setPrinting(PrepackFactor,PrepackID);
                        }
                        jsonData=results.toString();
                        z="success";
                    }else{
                        int newID=newNo;
                        for(int i=0;i<Integer.parseInt(Qty);i++){
                            newID=newID+1;
                            String NewID = String.valueOf(newID);
                            String vNewID = NewID.substring(1,NewID.length());
                            String PrepackID=newPrefix+vNewID;
                            String PrepackFactor=i+1+"/"+Qty;
                            String insertDt="insert into stk_prepack_dt(Doc1No,PrepackID,PrepackFactor)values(" +
                                    "'"+Doc1No+"', '"+PrepackID+"', '"+PrepackFactor+"')";
                            Statement stmtIn2=conn.createStatement();
                            stmtIn2.execute(insertDt);
                            JSONObject row = new JSONObject();
                            row.put("Doc1No",Doc1No);
                            row.put("CusName",CusName);
                            row.put("Address",Address);
                            row.put("PrepackFactor",PrepackFactor);
                            row.put("PrepackID",PrepackID);
                            row.put("D_ate",D_ate);
                            results.put(row);
                           // setPrinting(PrepackFactor,PrepackID);
                        }
                        jsonData=results.toString();
                        z="success";
                    }
                }else{
                    z="error";
                }
            }else{
              //  db.openDB();
                db.exeQuery(insertHd);
               // db.closeDB();
            }
            //db.closeDB();
            Log.d("RESULT",jsonData.toString());
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }finally {
            curSet.close();
            db.closeDB();
        }
        return z;
    }

    private boolean setPrinting(String PrepackFactor, String PrepackID){
        try {
            int count = 1;
            while (count < 2) {
                PrintingUSB print = new PrintingUSB();
                Boolean isBT = print.printTSC(c, ComName, CusName, Address, PrepackFactor, PrepackID);
                if (isBT == false) {
                    return false;
                } else {
                    ++count;
                    Thread.sleep(1500);
                    return true;
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return false;
    }
}
