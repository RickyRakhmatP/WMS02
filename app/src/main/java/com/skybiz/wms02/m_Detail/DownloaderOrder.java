package com.skybiz.wms02.m_Detail;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderOrder extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType,DocType;
    String CurCode;
    FragmentManager fm;
    public DownloaderOrder(Context c, String DocType, RecyclerView rv, FragmentManager fm) {
        this.c = c;
        this.DocType=DocType;
        this.rv = rv;
        this.fm=fm;
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
            OrderParser p=new OrderParser(c,DocType,result,rv,fm);
            p.execute();
        }
    }

    private String downloadData(){
        JSONObject jsonReq,jsonRes;
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
            /*if(DBStatus.equals("2")){
                String vQuery = "select Qty, Doc1No, ItemCode, ItemGroup, Description, " +
                        "IFNULL(HCUnitCost,0) as HCUnitCost,IFNULL(HCDiscount,0)as HCDiscount, " +
                        "IFNULL(DisRate1,0)as DisRate1, RunNo, Description2, UOM, FactorQty, BlankLine, DetailTaxCode " +
                        "from cloud_cus_inv_dt where DocType='"+DocType+"' Order By RunNo desc";
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                Log.d("JSON",result);
                db.closeDB();
                if(!result.equals("0")) {
                    return result;
                }else{
                    return null;
                }
            }else {*/
                String vQuery = "select QtyOUT, Doc1No, ItemCode, Description, " +
                        "IFNULL(HCUnitCost,0) as HCUnitCost,IFNULL(HCDiscount,0)as HCDiscount, " +
                        "IFNULL(DisRate1,0)as DisRate1, RunNo, UOM, FactorQty, BlankLine, DetailTaxCode " +
                        "from cloud_inventory_dt where DocType='"+DocType+"' Order By RunNo desc";
                Cursor rsData=db.getQuery(vQuery);
                JSONArray results = new JSONArray();
                while(rsData.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("QTY",rsData.getString(0));
                    row.put("Doc1No",rsData.getString(1));
                    row.put("ItemCode",rsData.getString(2));
                    row.put("Description",rsData.getString(3));
                    row.put("HCUnitCost",rsData.getString(4));
                    row.put("HCDiscount",rsData.getString(5));
                    row.put("DisRate1",rsData.getString(6));
                    row.put("RunNo",rsData.getString(7));
                    row.put("UOM",rsData.getString(8));
                    row.put("FactorQty",rsData.getString(9));
                    row.put("BlankLine",rsData.getString(10));
                    row.put("DetailTaxCode",rsData.getString(11));
                    results.put(row);
                }
                return results.toString();
            //}
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
