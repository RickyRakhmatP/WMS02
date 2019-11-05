package com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail;

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
import com.skybiz.wms02.ui_PrintPrepackLabel.PrintPrepackLabel;
import com.skybiz.wms02.ui_PrintPrepackLabel.PrintPrepackLabel2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderListCS2 extends AsyncTask<Void,Void,String> {
    Context c;
    String Doc1No;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    String CusName,Address,D_ate;

    public DownloaderListCS2(Context c, String Doc1No, RecyclerView rv) {
        this.c = c;
        this.Doc1No = Doc1No;
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
            ((PrintPrepackLabel2)c).setCusName(CusName,Address,D_ate);
            ListCSParser p=new ListCSParser(c,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        Cursor curSet=null;
        DBAdapter db=null;
        try {
            db = new DBAdapter(c);
            db.openDB();
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
            String sql = "select H.Doc1No, C.CusName, D.ItemCode, " +
                    " D.Description, D.Qty, D.FactorQty," +
                    " D.UOM, C.Address, H.D_ate, substr(C.CusName,1,26) as CusName1," +
                    " substr(C.CusName,27,26) as CusName2, substr(C.CusName,53,26) as CusName3, " +
                    " substr(C.Address,1,26) as Address1, substr(C.Address,27,26) as Address2, " +
                    " substr(C.Address,53,26) as Address3, substr(C.Address,79,26) as Address4 " +
                    " from stk_cus_inv_hd H inner join" +
                    " stk_cus_inv_dt D ON H.Doc1No=D.Doc1No " +
                    " inner join customer C ON H.CusCode=C.CusCode " +
                    " where H.Doc1No='"+Doc1No+"'  ";
            String qDelDum="delete from tb_dumprint2";
            db.exeQuery(qDelDum);
            String CusName1="",CusName2="",CusName3="",Address1="",Address2="",Address3="",Address4="";
            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet rsData = statement.getResultSet();

                        while (rsData.next()) {
                            JSONObject row = new JSONObject();
                            CusName=rsData.getString(2);
                            Address=rsData.getString(8);
                            D_ate=rsData.getString(9);
                            CusName1=rsData.getString(10).trim();
                            CusName2=rsData.getString(11).trim();
                            CusName3=rsData.getString(12).trim();
                            Address1=rsData.getString(13).trim();
                            Address2=rsData.getString(14).trim();
                            Address3=rsData.getString(15).trim();
                            Address4=rsData.getString(16).trim();
                            row.put("ItemCode", rsData.getString(3));
                            row.put("Description", Encode.setChar(CharType, rsData.getString(4)));
                            row.put("Qty", twoDecimal(rsData.getDouble(5)));
                            row.put("FactorQty", twoDecimal(rsData.getDouble(6)));
                            row.put("UOM",  Encode.setChar(CharType,rsData.getString(7)));
                            results.put(row);
                        }
                        String qInsertDum="insert into tb_dumprint2(CusName1,CusName2,CusName3," +
                                "Address1,Address2,Address3," +
                                "Address4)values('"+CusName1+"' ,'"+CusName2+"' ,'"+CusName3+"'," +
                                "'"+Address1+"', '"+Address2+"', '"+Address3+"'," +
                                "'"+Address4+"')";
                        Log.d("QUERY DUM",qInsertDum);
                        db.exeQuery(qInsertDum);
                        return results.toString();
                    }
                }
            }else {
                Cursor rsData = db.getQuery(sql);
                JSONArray results2 = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row = new JSONObject();
                    CusName=rsData.getString(1);
                    Address=rsData.getString(7);
                    D_ate=rsData.getString(8);
                    CusName1=rsData.getString(9);
                    CusName2=rsData.getString(10);
                    CusName3=rsData.getString(11);
                    Address1=rsData.getString(12);
                    Address2=rsData.getString(13);
                    Address3=rsData.getString(14);
                    Address4=rsData.getString(15);
                    row.put("ItemCode", rsData.getString(2));
                    row.put("Description", rsData.getString(3));
                    row.put("Qty", twoDecimal(rsData.getDouble(4)));
                    row.put("FactorQty", twoDecimal(rsData.getDouble(5)));
                    row.put("UOM", rsData.getString(6));
                    results2.put(row);
                }
                String qInsertDum="insert into tb_dumprint2(CusName1,CusName2,CusName3," +
                        "Address1,Address2,Address3," +
                        "Address4)values('"+CusName1+"' ,'"+CusName2+"' ,'"+CusName3+"'," +
                        "'"+Address1+"', '"+Address2+"', '"+Address3+"'," +
                        "'"+Address4+"')";
                db.exeQuery(qInsertDum);

                Log.d("RESULT JSON", results2.toString());
                return results2.toString();
            }
            //curSet.close();
            //db.closeDB();
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            curSet.close();
            db.closeDB();
        }
        return null;
    }

    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
    }
}
