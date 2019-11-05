package com.skybiz.wms02.m_Detail;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import org.json.JSONException;
import org.json.JSONObject;

public class DelItem extends AsyncTask<Void,Void,String> {
    Context c;
    String RunNo;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    JSONObject jsonReq,jsonRes;
    public DelItem(Context c, String runNo) {
        this.c = c;
        RunNo = runNo;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fndelete();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"failed, delete data", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"success, delete data", Toast.LENGTH_SHORT).show();
        }
    }

    private String fndelete(){
        try{
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
            String vDelete="delete from cloud_inventory_dt where RunNo='"+RunNo+"'";
            db.exeQuery(vDelete);
            z="success";
            /*if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal conn1 = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vDelete);
                jsonReq.put("action", "insert");
                String response = conn1.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                z=hasil;
                Log.d("RES", hasil);
            }else{
                db.addQuery(vDelete);
                z="success";
            }*/
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return z;
    }
}
