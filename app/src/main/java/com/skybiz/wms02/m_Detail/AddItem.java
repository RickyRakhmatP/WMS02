package com.skybiz.wms02.m_Detail;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.skybiz.wms02.m_DataObject.CalculateLineAmt;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by 7 on 16/04/2018.
 */

public class AddItem extends AsyncTask<Void, Void, String> {
    Context c;
    String ItemCode,vQty,vUnitPrice,vHCDiscount,vDisRate1,vUOM,vDetailTaxCode,vDescription,DocType,BlankLine;
    String IPAddress, UserName, Password,DBName,Port,DBStatus,ItemConn,URL,z,CharType,LocationCode,BranchCode;
    String deviceId,RunNo;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty,DisRate1;
    TelephonyManager telephonyManager;

    public AddItem(Context c, String DocType, String itemCode, String vDescription , String vQty, String vUnitPrice, String vUOM, String vDetailTaxCode, String vHCDiscount, String vDisRate1, String BlankLine) {
        this.c = c;
        this.DocType = DocType;
        ItemCode = itemCode;
        this.vDescription = vDescription;
        this.vQty = vQty;
        this.vUnitPrice = vUnitPrice;
        this.vUOM = vUOM;
        this.vDetailTaxCode = vDetailTaxCode;
        this.vHCDiscount = vHCDiscount;
        this.vDisRate1 = vDisRate1;
        this.BlankLine = BlankLine;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnadd();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Add Item Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Add Item Successfull", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnadd(){
        try{
            JSONObject jsonReq,jsonRes;
            deviceId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    "Port,DBName,DBStatus," +
                    "CharSetType,LocationCode,BranchCode" +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                Port = curSet.getString(3);
                DBName = curSet.getString(4);
                DBStatus=curSet.getString(5);
                CharType=curSet.getString(6);
                LocationCode=curSet.getString(7);
                BranchCode=curSet.getString(8);
            }
            dQty=0.00;
            String vCheck="select RunNo,QtyOUT,HCUnitCost from cloud_inventory_dt where" +
                    " ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' ";
           /* if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCheck);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String res = jsonRes.getString("hasil");
                if(!res.equals("0")) {
                    JSONArray jaCheck = new JSONArray(res);
                    JSONObject joCheck = null;
                    for (int i = 0; i < jaCheck.length(); i++) {
                        joCheck = jaCheck.getJSONObject(i);
                        RunNo = joCheck.getString("RunNo");
                        dQty = joCheck.getDouble("QTY");
                        UnitPrice = joCheck.getDouble("HCUnitCost");
                    }
                }
            }else {*/
                Cursor curCheck = db.getQuery(vCheck);
                while (curCheck.moveToNext()) {
                    RunNo = curCheck.getString(0);
                    dQty = curCheck.getDouble(1);
                    UnitPrice = curCheck.getDouble(2);
                }
           // }
            String vData="";
            UnitPrice  = Double.parseDouble(vUnitPrice);
            HCDiscount = Double.parseDouble(vHCDiscount);
            DisRate1   = Double.parseDouble(vDisRate1);
            if(dQty==0.00){
                Qty=1.00;
                vData = addItem(ItemCode, vDescription, Qty,UnitPrice,vDetailTaxCode,vUOM,1.00,HCDiscount,DisRate1);
            }else{
                Qty = dQty+1.00;
                vData = updateItem(RunNo,Qty, UnitPrice,vDetailTaxCode, HCDiscount, DisRate1);
            }
            db.closeDB();
            return vData;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //add item new structure
    private String addItem(String ItemCode, String Description, Double Qty, Double UnitPrice, String DetailTaxCode, String UOM, Double FactorQty, Double HCDiscount, Double DisRate1){
        String vInsert = "";
        Double TaxRate1=0.00;
        JSONObject jsonReq,jsonRes;
        try {
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            DisRate1 = vDataAmt.getvDisRate1();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String datedNow = DateCurr.format(date);
            String vSQLInsert = "INSERT INTO cloud_inventory_dt (Doc1No, N_o, ItemCode, Description," +
                    " QtyOUT, FactorQty, UOM, UOMSingular, " +
                    " HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                    " HCTax, HCLineAmt, BranchCode, DepartmentCode," +
                    " ProjectCode, LocationCode, WarrantyDate, LineNo, " +
                    " BlankLine, DocType, ComputerName, DetailTaxCode, " +
                    " SalesPersonCode)" +
                    "VALUES(" +
                    " '" + datedNow + "', 'a', '" + ItemCode + "', '" + Description + "'," +
                    " '" + Qty + "', '" + FactorQty + "', '" + UOM + "', '" + UOM + "'," +
                    " '" + UnitPrice + "', '" + DisRate1 + "', '" + HCDiscount + "', '" + TaxRate1 + "'," +
                    " '" + HCTax + "','" + HCLineAmt + "', '"+BranchCode+"', 'android', " +
                    " 'android', '"+LocationCode+"', '"+datedNow+"', '0', " +
                    " '"+BlankLine+"', '"+DocType+"', '"+deviceId+"', '"+DetailTaxCode+"'," +
                    " 'android')";
            Log.d("CLOUD DT",vSQLInsert);
           /* if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal conn1 = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLInsert);
                jsonReq.put("action", "insert");
                String response = conn1.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {*/
                db.exeQuery(vSQLInsert);
            //}
            db.closeDB();
            return vInsert;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return vInsert;
    }


    //update item new

    private String updateItem(String RunNo, Double Qty, Double UnitPrice,
                              String DetailTaxCode, Double HCDiscount, Double DisRate1){
        String vUpdate="";
        Double TaxRate1=0.00;
        JSONObject jsonReq,jsonRes;
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            DisRate1 = vDataAmt.getvDisRate1();
            HCDiscount = vDataAmt.getvHCDiscount();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            String vSQLUpdate = "UPDATE cloud_inventory_dt SET QtyOUT='" + Qty + "'," +
                    " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                    " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                    " HCLineAmt='" + HCLineAmt + "' " +
                    " where RunNo ='" + RunNo + "' ";
           /* if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLUpdate);
                jsonReq.put("action", "update");
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {*/
                db.exeQuery(vSQLUpdate);
           // }
            db.closeDB();
            return vUpdate;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return vUpdate;
    }

    public static CalculateLineAmt fncalculate(Context context, Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =DisRate1;
        if(DisRate1>0){
            vDisRate1		=DisRate1/100;
            vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
            }else{
                vHCDiscount	    =0.00;
                vDisRate1	    =0.00;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
            }
        }
        if(DetailTaxCode.isEmpty()) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            vHCLineAmt   = vAmountB4Tax;
            String strSQL = "select R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            DBAdapter db = new DBAdapter(context);
            db.openDB();
            Cursor resultSet2 = db.getQuery(strSQL);
            while (resultSet2.moveToNext()) {
               // vR_ate      = Double.parseDouble(resultSet2.getString(0));
                vR_ate      = 0.00;
                vTaxType    = resultSet2.getString(1);
                if (vTaxType.equals("0") || vTaxType.equals("2")) {
                    vHCTax      =vAmountB4Tax * (vR_ate / (vR_ate + 100));
                    vTempAmt    =vAmountB4Tax - vHCTax;
                    vHCLineAmt  =vTempAmt + vHCTax;
                } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                    vHCTax      = vAmountB4Tax * (vR_ate / 100);
                    vHCLineAmt  = vAmountB4Tax + vHCTax;
                }
            }
            db.closeDB();
        }
        Log.d("RESULT", "TAX: " + vHCTax.toString());
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }

    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
