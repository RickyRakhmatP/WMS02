package com.skybiz.wms02.m_Item;

import android.content.Context;
import android.database.Cursor;
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

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderItem extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    String CurCode,DocType;

    public DownloaderItem(Context c, String DocType, String itemGroup, RecyclerView rv) {
        this.c = c;
        this.DocType = DocType;
        this.ItemGroup = itemGroup;
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
            ItemParser p=new ItemParser(c,DocType,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            //CurCode="RM";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String sqlCom = "select CurCode from companysetup ";
            Cursor rsCom = db.getQuery(sqlCom);
            while (rsCom.moveToNext()) {
                CurCode=rsCom.getString(0);
            }
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
            if(DBStatus.equals("1")){
                ///if(ItemConn.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    if (ItemGroup.equals("")) {
                        String sqlGroup = "select ItemGroup from stk_master order by ItemGroup limit 1";
                        Statement stmtGroup = conn.createStatement();
                        stmtGroup.execute(sqlGroup);
                        ResultSet rsGroup = stmtGroup.getResultSet();
                        while (rsGroup.next()) {
                            ItemGroup = rsGroup.getString("ItemGroup");
                        }
                    }
                    String sql = "select '" + CurCode + "' as CurCode, '0' as Qty," +
                                " PhotoFile, FORMAT(UnitPrice,2) as UnitPrice, ItemCode," +
                                " Description, ItemGroup, UOM, RetailTaxCode,PurchaseTaxCode, SalesTaxCode " +
                                " from stk_master where SuspendedYN='0' and ItemGroup='" + ItemGroup + "' Order By ItemCode";
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                            ResultSet resultSet = statement.getResultSet();
                            ResultSetMetaData columns = resultSet.getMetaData();
                            while (resultSet.next()) {
                                JSONObject row = new JSONObject();
                                row.put("CurCode",resultSet.getString(1));
                                row.put("Qty",resultSet.getString(2));
                                row.put("ItemCode",resultSet.getString(5));
                                row.put("ItemGroup",resultSet.getString(7));
                                row.put("Description", Encode.setChar(CharType,resultSet.getString(6)));
                                row.put("UnitPrice",resultSet.getString(4));
                                row.put("PhotoFile",resultSet.getString(3));
                                row.put("UOM", Encode.setChar(CharType,resultSet.getString(8)));
                                row.put("RetailTaxCode",resultSet.getString(9));
                                row.put("PurchaseTaxCode",resultSet.getString(10));
                                row.put("SalesTaxCode",resultSet.getString(11));
                                results.put(row);
                            }
                            resultSet.close();
                        }
                        statement.close();
                        Log.d("JSON",results.toString());
                        return results.toString();
                    }
               /*}else{
                    if (ItemGroup.equals("")) {
                        String vQueryG = "select ItemGroup from stk_master order by ItemGroup limit 1 ";
                        Cursor cGroup = db.getQuery(vQueryG);
                        while (cGroup.moveToNext()) {
                            ItemGroup = cGroup.getString(0);
                        }
                    }
                    String vQuery="select '"+CurCode+"' as CurCode, '0' as Qty, ItemCode, ItemGroup, Description," +
                            "UnitPrice, '' as PhotoFile, UOM, RetailTaxCode,PurchaseTaxCode, SalesTaxCode " +
                            "from stk_master where ItemGroup='"+ItemGroup+"' Order By ItemCode ";
                    Cursor rsData=db.getQuery(vQuery);
                    Log.d("TOTAL ROWS",vQuery+ String.valueOf(rsData.getCount()));
                    JSONArray results2=new JSONArray();
                    while(rsData.moveToNext()){
                        JSONObject row2=new JSONObject();
                        row2.put("CurCode",rsData.getString(0));
                        row2.put("Qty",rsData.getString(1));
                        row2.put("ItemCode",rsData.getString(2));
                        row2.put("ItemGroup",rsData.getString(3));
                        row2.put("Description",rsData.getString(4));
                        row2.put("UnitPrice",rsData.getString(5));
                        row2.put("PhotoFile",rsData.getString(6));
                        row2.put("UOM",rsData.getString(7));
                        row2.put("RetailTaxCode",rsData.getString(8));
                        row2.put("PurchaseTaxCode",rsData.getString(9));
                        row2.put("SalesTaxCode",rsData.getString(10));
                        results2.put(row2);
                    }
                    db.closeDB();
                    Log.d("RESULT JSON",results2.toString());
                    return results2.toString();
                }*/
            }else if(DBStatus.equals("0")) {
                if (ItemGroup.equals("")) {
                    String vQueryG = "select ItemGroup from stk_master order by ItemGroup limit 1 ";
                    Cursor cGroup = db.getQuery(vQueryG);
                    while (cGroup.moveToNext()) {
                        ItemGroup = cGroup.getString(0);
                    }
                }
                String vQuery = "select '" + CurCode + "' as CurCode, '0' as Qty, ItemCode, ItemGroup, Description," +
                        "UnitPrice, '' as PhotoFile, UOM, RetailTaxCode,PurchaseTaxCode, SalesTaxCode  " +
                        "from stk_master where ItemGroup='" + ItemGroup + "' Order By ItemCode";
                Cursor rsData = db.getQuery(vQuery);
                Log.d("TOTAL ROWS", vQuery + String.valueOf(rsData.getCount()));
                JSONArray results2 = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row2 = new JSONObject();
                    row2.put("CurCode", rsData.getString(0));
                    row2.put("Qty", rsData.getString(1));
                    row2.put("ItemCode", rsData.getString(2));
                    row2.put("ItemGroup", rsData.getString(3));
                    row2.put("Description", rsData.getString(4));
                    row2.put("UnitPrice", rsData.getString(5));
                    row2.put("PhotoFile", rsData.getString(6));
                    row2.put("UOM", rsData.getString(7));
                    row2.put("RetailTaxCode", rsData.getString(8));
                    row2.put("PurchaseTaxCode", rsData.getString(9));
                    row2.put("SalesTaxCode", rsData.getString(10));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON", results2.toString());
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
