package com.skybiz.wms02.m_Summary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Decode;
import com.skybiz.wms02.m_DataObject.FnCalculateHCNetAmt;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveTrn extends AsyncTask<Void,Void,String> {
    Context c;
    String Doc1No,LocationCodeTo,LocationCode,BranchCode;
    String IPAddress,UserName,Password,DBName,Port,URL,DBStatus,z;
    String CurCode,vPostGlobalTaxYN,deviceId,datedTime,CusName,DocType,datedShort;
    String TypePrinter,NamePrinter,IPPrinter,CharType,isDuplicate,GlobalTaxCode,LastNo,NewDoc,stringPrint,CusCode,TaxType;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    Boolean isBT;
    Connection conn;
    public SaveTrn(Context c, String DocType,String doc1No, String LocationCodeTo) {
        this.c = c;
        this.DocType=DocType;
        Doc1No = doc1No;
        this.LocationCodeTo=LocationCodeTo;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        return this.fnsave();
    }

    @Override
    protected void onPostExecute(String vData) {
        super.onPostExecute(vData);
        if(vData.equals("error")){
            Toast.makeText(c,"Failed, data cannot save", Toast.LENGTH_SHORT).show();
        }else{
            fnrefresh();
           // ((CreditNote)c).newRefresh();
        }
    }
    private String fnsave() {
        try {
            CusCode             ="999999";
            CusName             ="Cash Sales";
            z                   ="error";
            GlobalTaxCode       = "";
            vPostGlobalTaxYN    ="0";
            deviceId            = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
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
            String qGen2="select PostGlobalTaxYN from sys_general_setup2";
            Cursor rsGen2 = db.getQuery(qGen2);
            while (rsGen2.moveToNext()) {
                vPostGlobalTaxYN = rsGen2.getString(0);
            }

            String qCom = "select CurCode,GSTNO,CompanyName from companysetup";
            Cursor rsCom=db.getQuery(qCom);
            while (rsCom.moveToNext()) {
                CurCode = rsCom.getString(0);
            }

            String TermCode="1";
            String D_ay="0";
            String SalesPersonCode="";
            String vMember="select CusCode,CusName,TermCode,D_ay,SalesPersonCode from tb_member ";
            Cursor rsMember=db.getQuery(vMember);
            while(rsMember.moveToNext()){
                CusCode=rsMember.getString(0);
                CusName=rsMember.getString(1);
                TermCode=rsMember.getString(2);
                D_ay=rsMember.getString(3);
            }

            if (vPostGlobalTaxYN.equals("1")) {
                String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM " +
                        "sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
                Cursor rsDef = db.getQuery(vDefault);
                while (rsDef.moveToNext()) {
                    GlobalTaxCode = rsDef.getString(0);
                    R_ate = Double.parseDouble(rsDef.getString(1));
                    TaxType = rsDef.getString(2);
                }
            } else {
                TaxType = "0";
                R_ate = 0.00;
            }

            FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, DocType,deviceId, GlobalTaxCode, TaxType, R_ate);
            HCGbTax = vNetAmt.getHCGbTax();
            TotalAmt = vNetAmt.getHCNetAmt();
            GbTaxRate1 = vNetAmt.getGbTaxRate1();
            HCDtTax = vNetAmt.getHCDtTax();
            HCGbDiscount = vNetAmt.getHCGbDiscount();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat DateCurr2 = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            datedShort   = sdf.format(date);
            datedTime           = DateCurr1.format(date);
            String vTime        = DateCurr2.format(date);
            String vDel = "delete from cloud_inventory_dt";
            String vDel2 = "delete from tb_member";
            String vDel3 = "delete from tb_location";

            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                //conn= Connect_db.getConnection();
            }

            if(DocType.equals("IS")){
                saveInventory();
            }else if(DocType.equals("RS")){
                saveRS();
            }else if(DocType.equals("GRNDO")){
                 saveGRNDO();
            }else if(DocType.equals("TS")){
                saveTS();
            }
            db.exeQuery(vDel);
            db.exeQuery(vDel2);
            db.exeQuery(vDel3);
            db.closeDB();
            fnsavelastno();
            z="success";
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return z;
    }

    private void saveInventory() {
        try {
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String vDuplicate = "select count(*) as jumlah from stk_inventory_dt " +
                    "where Doc1No='" + Doc1No + "' and DocType='" + DocType + "' ";
            if(DBStatus.equals("1")){
                if (conn != null) {
                    Statement stmtDup = conn.createStatement();
                    stmtDup.execute(vDuplicate);
                    ResultSet rsDup = stmtDup.getResultSet();
                    while (rsDup.next()) {
                        isDuplicate = rsDup.getString("jumlah");
                    }
                    stmtDup.close();
                }
            }else{
                Cursor rsDup=db.getQuery(vDuplicate);
                while(rsDup.moveToNext()){
                    isDuplicate = rsDup.getString(0);
                }
            }
            if(!isDuplicate.equals("0")){
                fnsavelastno();
                z="error";
            }else {
                if (DBStatus.equals("1")) {
                    String vHeader = "INSERT INTO stk_inventory_hd (Doc1No, Doc2No, Doc3No, " +
                            "D_ate, CusCode, CurCode, " +
                            "CurRate1, CurRate2, CurRate3, " +
                            "Attention, GbDisRate1, GbDisRate2," +
                            "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            "GbTaxRate2, GbTaxRate3, HCGbTax," +
                            "HCNetAmt, DocType, Reason, " +
                            "ApprovedYN, PostedYN, UDRunNo," +
                            " L_ink) VALUES ('" + Doc1No + "', '', '', " +
                            " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                            " '1', 1, 1, " +
                            " 'Attention', '0', 0," +
                            "  0, 0, '" + GbTaxRate1 + "'," +
                            "  0, 0, '" + HCGbTax + "', " +
                            " '" + TotalAmt + "', '" + DocType + "', 'Reason'," +
                            " '1', '1', 0, " +
                            " '1')";
                    Statement stmtHeader = conn.createStatement();
                    stmtHeader.execute(vHeader);

                    String QueryD = "select RunNo, '" + Doc1No + "', N_o, ItemCode, " +
                            " Description, QtyOUT, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, SalesPersonCode, " +
                            " LocationCode, LineNo, BlankLine, " +
                            " WarrantyDate, DetailTaxCode " +
                            " from cloud_inventory_dt " +
                            " where ComputerName='" + deviceId + "' and DocType='" + DocType + "' ";
                    Cursor rsDt = db.getQuery(QueryD);
                    int i = 1;
                    while (rsDt.moveToNext()) {
                        String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                                " Description, QtyOUT, FactorQty," +
                                " UOM, UOMSingular, HCUnitCost," +
                                " DisRate1, HCDiscount, TaxRate1," +
                                " HCTax, HCLineAmt, BranchCode, " +
                                " DepartmentCode, ProjectCode, SalesPersonCode, " +
                                " LocationCode, LineNo, BlankLine, " +
                                " WarrantyDate, DocType )values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(4)) + "', '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(7)) + "', '" + Decode.setChar(CharType, rsDt.getString(8)) + "', '" + rsDt.getString(9) + "'," +
                                " '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "'," +
                                " '" + rsDt.getString(16) + "', '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "'," +
                                " '" + rsDt.getString(19) + "', '" + i + "', '" + rsDt.getString(21) + "', " +
                                " '" + rsDt.getString(22) + "', '" + DocType + "' )";
                        Statement stmtDetail = conn.createStatement();
                        Log.d("SUCCESS DETAIL", vDetail);
                        stmtDetail.execute(vDetail);

                        String vDtOut = "INSERT INTO stk_detail_trn_out (ItemCode, Doc1No, Doc2No, " +
                                "Doc3No, D_ate, BookDate, " +
                                "QtyOUT, FactorQty, UOM, " +
                                "UnitPrice, HCTax, CusCode," +
                                "DocType1, DocType2, DocType3, " +
                                "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                                "LocationCode, ItemBatch, VoidYN, " +
                                "GlobalTaxCode, DetailTaxCode)values(" +
                                " '" + rsDt.getString(3) + "', '', '', " +
                                " '" + rsDt.getString(1) + "', '" + datedShort + "', '" + datedShort + "', " +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + Decode.setChar(CharType, rsDt.getString(7)) + "', " +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(13) + "', '" + CusCode + "', " +
                                " '', '', '" + DocType + "', " +
                                " '0', '0', '0', " +
                                " '"+rsDt.getString(19)+"', '', '0', " +
                                " '', '" + rsDt.getString(23) + "' )";
                        Statement stmDtOut = conn.createStatement();
                        Log.d("DETAIL TRN OUT", vDtOut);
                        stmDtOut.execute(vDtOut);
                        i++;
                    }
                    stmtHeader.close();
                }
                String vHeader = "INSERT INTO stk_inventory_hd (Doc1No, Doc2No, Doc3No, " +
                        "D_ate, CusCode, CurCode, " +
                        "CurRate1, CurRate2, CurRate3, " +
                        "Attention, GbDisRate1, GbDisRate2," +
                        "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                        "GbTaxRate2, GbTaxRate3, HCGbTax," +
                        "HCNetAmt, DocType, Reason, " +
                        "ApprovedYN, PostedYN, UDRunNo," +
                        " L_ink, SyncYN) VALUES ('" + Doc1No + "', '', '', " +
                        " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                        " '1', 1, 1, " +
                        " 'Attention', '0', 0," +
                        "  0, 0, '" + GbTaxRate1 + "'," +
                        "  0, 0, '" + HCGbTax + "', " +
                        " '" + TotalAmt + "', '" + DocType + "', 'Reason'," +
                        " '1', '1', 0, " +
                        " '1', '" + DBStatus + "')";
                db.exeQuery(vHeader);

                String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                        " Description, QtyOUT, FactorQty," +
                        " UOM, UOMSingular, HCUnitCost," +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine," +
                        " WarrantyDate, DocType, SyncYN) " +
                        " SELECT '" + Doc1No + "', N_o, ItemCode, " +
                        " Description, QtyOUT, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine, " +
                        " '" + datedShort + "', '" + DocType + "',  '" + DBStatus + "' " +
                        "FROM cloud_inventory_dt WHERE ComputerName='" + deviceId + "' ";
                long addDetail = db.exeQuery(vDetail);
                if (addDetail > 0) {
                    Log.d("SUCCESS DETAIL", "inserted");
                } else {
                    Log.d("ERROR DETAIL", "failed");
                }
                String vDtOut = "INSERT INTO stk_detail_trn_out (ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyOUT, FactorQty, UOM, " +
                        "UnitPrice, HCTax, CusCode," +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, VoidYN, " +
                        "GlobalTaxCode, DetailTaxCode, SyncYN)" +
                        "Select ItemCode, '', '', " +
                        " '" + Doc1No + "', '" + datedShort + "', '" + datedShort + "', " +
                        " QtyOUT, FactorQty, UOM, " +
                        " HCUnitCost, HCTax, '" + CusCode + "', " +
                        " '', '', '" + DocType + "', " +
                        " '0', '0', '0', " +
                        " LocationCode, '', '0', " +
                        " '" + GlobalTaxCode + "', DetailTaxCode, '" + DBStatus + "'  " +
                        " from cloud_inventory_dt WHERE ComputerName='" + deviceId + "'";
                db.exeQuery(vDtOut);
                //end detailtrnout
            }
        }catch (SQLiteException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveTS() {
        try {
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String vDuplicate = "select count(*) as jumlah from stk_inventory_dt " +
                    "where Doc1No='" + Doc1No + "' and DocType='" + DocType + "' ";
            if(DBStatus.equals("1")){
                if (conn != null) {
                    Statement stmtDup = conn.createStatement();
                    stmtDup.execute(vDuplicate);
                    ResultSet rsDup = stmtDup.getResultSet();
                    while (rsDup.next()) {
                        isDuplicate = rsDup.getString("jumlah");
                    }
                    stmtDup.close();
                }
            }else{
                Cursor rsDup=db.getQuery(vDuplicate);
                while(rsDup.moveToNext()){
                    isDuplicate = rsDup.getString(0);
                }
            }
            if(!isDuplicate.equals("0")){
                fnsavelastno();
                z="error";
            }else {
                if (DBStatus.equals("1")) {
                    String vHeader = "INSERT INTO stk_inventory_hd (Doc1No, Doc2No, Doc3No, " +
                            "D_ate, CusCode, CurCode, " +
                            "CurRate1, CurRate2, CurRate3, " +
                            "Attention, GbDisRate1, GbDisRate2," +
                            "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            "GbTaxRate2, GbTaxRate3, HCGbTax," +
                            "HCNetAmt, DocType, Reason, " +
                            "ApprovedYN, PostedYN, UDRunNo," +
                            " L_ink) VALUES ('" + Doc1No + "', '', '', " +
                            " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                            " '1', 1, 1, " +
                            " 'Attention', '0', 0," +
                            "  0, 0, '" + GbTaxRate1 + "'," +
                            "  0, 0, '" + HCGbTax + "', " +
                            " '" + TotalAmt + "', '" + DocType + "', 'Reason'," +
                            " '1', '1', 0, " +
                            " '1')";
                    Statement stmtHeader = conn.createStatement();
                    stmtHeader.execute(vHeader);

                    String QueryD = "select RunNo, '" + Doc1No + "', N_o, ItemCode, " +
                            " Description, QtyOUT, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, SalesPersonCode, " +
                            " LocationCode, LineNo, BlankLine, " +
                            " WarrantyDate, DetailTaxCode, QtyOUT " +
                            " from cloud_inventory_dt " +
                            " where ComputerName='" + deviceId + "' and DocType='" + DocType + "' ";
                    Cursor rsDt = db.getQuery(QueryD);
                    int i = 1;
                    while (rsDt.moveToNext()) {
                        String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                                " Description, QtyOUT, FactorQty," +
                                " UOM, UOMSingular, HCUnitCost," +
                                " DisRate1, HCDiscount, TaxRate1," +
                                " HCTax, HCLineAmt, BranchCode, " +
                                " DepartmentCode, ProjectCode, SalesPersonCode, " +
                                " LocationCode, LineNo, BlankLine, " +
                                " WarrantyDate, DocType, QtyIN," +
                                " LocationCodeTo)values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(4)) + "', '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(7)) + "', '" + Decode.setChar(CharType, rsDt.getString(8)) + "', '" + rsDt.getString(9) + "'," +
                                " '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "'," +
                                " '" + rsDt.getString(16) + "', '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "'," +
                                " '" + rsDt.getString(19) + "', '" + i + "', '" + rsDt.getString(21) + "', " +
                                " '" + rsDt.getString(22) + "', '" + DocType + "', '"+ rsDt.getString(5)+"'," +
                                " '"+LocationCodeTo+"' )";
                        Statement stmtDetail = conn.createStatement();
                        Log.d("SUCCESS DETAIL", vDetail);
                        stmtDetail.execute(vDetail);

                        String vDtOut = "INSERT INTO stk_detail_trn_out (ItemCode, Doc1No, Doc2No, " +
                                "Doc3No, D_ate, BookDate, " +
                                "QtyOUT, FactorQty, UOM, " +
                                "UnitPrice, HCTax, CusCode," +
                                "DocType1, DocType2, DocType3, " +
                                "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                                "LocationCode, ItemBatch, VoidYN, " +
                                "GlobalTaxCode, DetailTaxCode)values(" +
                                " '" + rsDt.getString(3) + "', '', '', " +
                                " '" + rsDt.getString(1) + "', '" + datedShort + "', '" + datedShort + "', " +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + Decode.setChar(CharType, rsDt.getString(7)) + "', " +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(13) + "', '" + CusCode + "', " +
                                " '', '', '" + DocType + "', " +
                                " '0', '0', '0', " +
                                " '"+rsDt.getString(19)+"', '', '0', " +
                                " '', '" + rsDt.getString(23) + "' )";
                        Statement stmDtOut = conn.createStatement();
                        Log.d("DETAIL TRN OUT", vDtOut);
                        stmDtOut.execute(vDtOut);

                        String vDtIn = "INSERT INTO stk_detail_trn_in (ItemCode, Doc1No, Doc2No, " +
                                "Doc3No, D_ate, BookDate, " +
                                "QtyIN, FactorQty, UOM, " +
                                "UnitCost, HCTax, CusCode," +
                                "DocType1, DocType2, DocType3, " +
                                "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                                "LocationCode, ItemBatch, " +
                                "GlobalTaxCode, DetailTaxCode, MovingAveCost, " +
                                "LandingCost, OCCost)values(" +
                                " '" + rsDt.getString(3) + "', '', '', " +
                                " '" + rsDt.getString(1) + "', '" + datedShort + "', '" + datedShort + "', " +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + Decode.setChar(CharType, rsDt.getString(7)) + "', " +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(13) + "', '" + CusCode + "', " +
                                " '', '', '" + DocType + "', " +
                                " '0', '0', '0', " +
                                " '"+LocationCodeTo+"', '', " +
                                " '', '" + rsDt.getString(23) + "', '0'," +
                                " '0','0' )";
                        Statement stmDtIn = conn.createStatement();
                        Log.d("DETAIL TRN OUT", vDtIn);
                        stmDtIn.execute(vDtIn);
                        i++;
                    }
                    stmtHeader.close();
                }
                String vHeader = "INSERT INTO stk_inventory_hd (Doc1No, Doc2No, Doc3No, " +
                        "D_ate, CusCode, CurCode, " +
                        "CurRate1, CurRate2, CurRate3, " +
                        "Attention, GbDisRate1, GbDisRate2," +
                        "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                        "GbTaxRate2, GbTaxRate3, HCGbTax," +
                        "HCNetAmt, DocType, Reason, " +
                        "ApprovedYN, PostedYN, UDRunNo," +
                        " L_ink, SyncYN) VALUES ('" + Doc1No + "', '', '', " +
                        " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                        " '1', 1, 1, " +
                        " 'Attention', '0', 0," +
                        "  0, 0, '" + GbTaxRate1 + "'," +
                        "  0, 0, '" + HCGbTax + "', " +
                        " '" + TotalAmt + "', '" + DocType + "', 'Reason'," +
                        " '1', '1', 0, " +
                        " '1', '" + DBStatus + "')";
                db.exeQuery(vHeader);

                String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                        " Description, QtyOUT, FactorQty," +
                        " UOM, UOMSingular, HCUnitCost," +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine," +
                        " WarrantyDate, DocType, QtyIN, " +
                        " LocationCodeTo, SyncYN) " +
                        " SELECT '" + Doc1No + "', N_o, ItemCode, " +
                        " Description, QtyOUT, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine, " +
                        " '" + datedShort + "', '" + DocType + "',  QtyOUT, " +
                        " '"+LocationCodeTo+"', '" + DBStatus + "' " +
                        "FROM cloud_inventory_dt WHERE ComputerName='" + deviceId + "' ";
                long addDetail = db.exeQuery(vDetail);
                if (addDetail > 0) {
                    Log.d("SUCCESS DETAIL", "inserted");
                } else {
                    Log.d("ERROR DETAIL", "failed");
                }
                String vDtOut = "INSERT INTO stk_detail_trn_out (ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyOUT, FactorQty, UOM, " +
                        "UnitPrice, HCTax, CusCode," +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, VoidYN, " +
                        "GlobalTaxCode, DetailTaxCode, SyncYN)" +
                        "Select ItemCode, '', '', " +
                        " '" + Doc1No + "', '" + datedShort + "', '" + datedShort + "', " +
                        " QtyOUT, FactorQty, UOM, " +
                        " HCUnitCost, HCTax, '" + CusCode + "', " +
                        " '', '', '" + DocType + "', " +
                        " '0', '0', '0', " +
                        " '', '', '0', " +
                        " '" + GlobalTaxCode + "', DetailTaxCode, '" + DBStatus + "'  " +
                        " from cloud_inventory_dt WHERE ComputerName='" + deviceId + "'";
                db.exeQuery(vDtOut);
                //end detailtrnout

                String vDtIn = "INSERT INTO stk_detail_trn_in (ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyIN, FactorQty, UOM, " +
                        "UnitCost, HCTax, CusCode," +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, " +
                        " MovingAveCost, LandingCost, OCCost, "+
                        "GlobalTaxCode, DetailTaxCode, SyncYN)" +
                        "Select ItemCode, '', '', " +
                        " '" + Doc1No + "', '" + datedShort + "', '" + datedShort + "', " +
                        " QtyOUT, FactorQty, UOM, " +
                        " HCUnitCost, HCTax, '" + CusCode + "', " +
                        " '', '', '" + DocType + "', " +
                        " '0', '0', '0', " +
                        " '"+LocationCodeTo+"', '', " +
                        " '0', '0', '0', " +
                        " '" + GlobalTaxCode + "', DetailTaxCode, '" + DBStatus + "'  " +
                        " from cloud_inventory_dt WHERE ComputerName='" + deviceId + "'";
                db.exeQuery(vDtIn);
            }
        }catch (SQLiteException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveGRNDO() {
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String vDuplicate = "select count(*) as jumlah from stk_inventory_dt " +
                    "where Doc1No='" + Doc1No + "' and DocType='" + DocType + "' ";
            if(DBStatus.equals("1")){
                if (conn != null) {
                    Statement stmtDup = conn.createStatement();
                    stmtDup.execute(vDuplicate);
                    ResultSet rsDup = stmtDup.getResultSet();
                    while (rsDup.next()) {
                        isDuplicate = rsDup.getString("jumlah");
                    }
                    stmtDup.close();
                }
            }else{
                Cursor rsDup=db.getQuery(vDuplicate);
                while(rsDup.moveToNext()){
                    isDuplicate = rsDup.getString(0);
                }
            }
            if(!isDuplicate.equals("0")){
                fnsavelastno();
                z="error";
            }else {
                if (DBStatus.equals("1")) {
                    String vHeader = "INSERT INTO stk_grndo_hd (Doc1No, Doc2No, Doc3No, " +
                            "D_ate, CusCode, CurCode, " +
                            "CurRate1, CurRate2, CurRate3, " +
                            "Attention, GbDisRate1, GbDisRate2," +
                            "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            "GbTaxRate2, GbTaxRate3, HCGbTax," +
                            "HCNetAmt, DocType, GlobalTaxCode, " +
                            "HCDtTax,L_ink) VALUES ('" + Doc1No + "', '', '', " +
                            " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                            " '1', 1, 1, " +
                            " 'Attention', '0', 0," +
                            "  0, 0, '" + GbTaxRate1 + "'," +
                            "  0, 0, '" + HCGbTax + "', " +
                            " '" + TotalAmt + "', '" + DocType + "', '"+GlobalTaxCode+"'," +
                            " '"+HCDtTax+"', '1')";
                    Statement stmtHeader = conn.createStatement();
                    stmtHeader.execute(vHeader);

                    String QueryD = "select RunNo, '" + Doc1No + "', N_o, ItemCode, " +
                            " Description, QtyOUT, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, LocationCode, " +
                            " LineNo, BlankLine, WarrantyDate, " +
                            " DetailTaxCode " +
                            " from cloud_inventory_dt " +
                            " where ComputerName='" + deviceId + "' and DocType='" + DocType + "' ";
                    Cursor rsDt = db.getQuery(QueryD);
                    int i = 1;
                    while (rsDt.moveToNext()) {
                        String vDetail = "INSERT INTO stk_grndo_dt (Doc1No, N_o, ItemCode," +
                                " Description, Qty, FactorQty," +
                                " UOM, UOMSingular, HCUnitCost," +
                                " DisRate1, HCDiscount, TaxRate1," +
                                " HCTax, HCLineAmt, BranchCode, " +
                                " DepartmentCode, ProjectCode, LocationCode, " +
                                " LineNo, BlankLine, WarrantyDate, " +
                                " PORunNo, DetailTaxCode )values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(4)) + "', '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(7)) + "', '" + Decode.setChar(CharType, rsDt.getString(8)) + "', '" + rsDt.getString(9) + "'," +
                                " '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "'," +
                                " '" + rsDt.getString(16) + "', '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "'," +
                                " '" + i + "', '" + rsDt.getString(20) + "', '" + rsDt.getString(21) + "', " +
                                "  '0', '"+rsDt.getString(22)+"' )";
                        Statement stmtDetail = conn.createStatement();
                        Log.d("SUCCESS DETAIL", vDetail);
                        stmtDetail.execute(vDetail);
                        i++;
                    }
                }

                String vHeader1 = "INSERT INTO stk_grndo_hd (Doc1No, Doc2No, Doc3No, " +
                        " D_ate, CusCode, CurCode, " +
                        " CurRate1, CurRate2, CurRate3, " +
                        " Attention, GbDisRate1, GbDisRate2, " +
                        " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                        " GbTaxRate2, GbTaxRate3, HCGbTax," +
                        " HCNetAmt, DocType, GlobalTaxCode, " +
                        " HCDtTax, L_ink, SyncYN) VALUES (" +
                        " '"+Doc1No+ "', '', '', " +
                        " '"+datedShort+"', '" + CusCode + "', '" + CurCode + "', " +
                        " '1', '1', '1', " +
                        " 'Attention', '0', '0', " +
                        " '0', '0', '" + GbTaxRate1 + "', " +
                        " '0', '0', '" + HCGbTax + "', " +
                        " '" + TotalAmt + "', '" + DocType + "', '"+GlobalTaxCode+"'," +
                        " '"+HCDtTax+"', '1', '"+DBStatus+"')";
                db.exeQuery(vHeader1);

                String vDetail1 = "INSERT INTO stk_grndo_dt (Doc1No, N_o, ItemCode," +
                        " Description, Qty, FactorQty," +
                        " UOM, UOMSingular, HCUnitCost," +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, LocationCode, " +
                        " LineNo, BlankLine, WarrantyDate," +
                        " PORunNo, DetailTaxCode, SyncYN) " +
                        " SELECT '" + Doc1No + "', N_o, ItemCode, " +
                        " Description, QtyOUT, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, LocationCode," +
                        " LineNo, BlankLine, '" + datedShort + "', " +
                        " '0',  DetailTaxCode, '" + DBStatus + "' " +
                        " FROM cloud_inventory_dt WHERE ComputerName='" + deviceId + "' and DocType='"+DocType+"' ";
                Log.d("SUCCESS DETAIL LOCAL", vDetail1);
                db.exeQuery(vDetail1);
            }
        }catch (SQLiteException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveRS() {
        try {
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String vDuplicate = "select count(*) as jumlah from stk_inventory_dt " +
                    "where Doc1No='" + Doc1No + "' and DocType='" + DocType + "' ";
            if(DBStatus.equals("1")){
                if (conn != null) {
                    Statement stmtDup = conn.createStatement();
                    stmtDup.execute(vDuplicate);
                    ResultSet rsDup = stmtDup.getResultSet();
                    while (rsDup.next()) {
                        isDuplicate = rsDup.getString("jumlah");
                    }
                    stmtDup.close();
                }
            }else{
                Cursor rsDup=db.getQuery(vDuplicate);
                while(rsDup.moveToNext()){
                    isDuplicate = rsDup.getString(0);
                }
            }
            if(!isDuplicate.equals("0")){
                fnsavelastno();
                z="error";
            }else {
                if (DBStatus.equals("1")) {
                    String vHeader = "INSERT INTO stk_inventory_hd (Doc1No, Doc2No, Doc3No, " +
                            "D_ate, CusCode, CurCode, " +
                            "CurRate1, CurRate2, CurRate3, " +
                            "Attention, GbDisRate1, GbDisRate2," +
                            "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            "GbTaxRate2, GbTaxRate3, HCGbTax," +
                            "HCNetAmt, DocType, Reason, " +
                            "ApprovedYN, PostedYN, UDRunNo," +
                            " L_ink) VALUES ('" + Doc1No + "', '', '', " +
                            " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                            " '1', 1, 1, " +
                            " 'Attention', '0', 0," +
                            "  0, 0, '" + GbTaxRate1 + "'," +
                            "  0, 0, '" + HCGbTax + "', " +
                            " '" + TotalAmt + "', '" + DocType + "', 'Reason'," +
                            " '1', '1', 0, " +
                            " '1')";
                    Statement stmtHeader = conn.createStatement();
                    stmtHeader.execute(vHeader);

                    String QueryD = "select RunNo, '" + Doc1No + "', N_o, ItemCode, " +
                            " Description, QtyOUT, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, SalesPersonCode, " +
                            " LocationCode, LineNo, BlankLine, " +
                            " WarrantyDate, DetailTaxCode " +
                            " from cloud_inventory_dt " +
                            " where ComputerName='" + deviceId + "' and DocType='" + DocType + "' ";
                    Cursor rsDt = db.getQuery(QueryD);
                    int i = 1;
                    while (rsDt.moveToNext()) {
                        String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                                " Description, QtyIN, FactorQty," +
                                " UOM, UOMSingular, HCUnitCost," +
                                " DisRate1, HCDiscount, TaxRate1," +
                                " HCTax, HCLineAmt, BranchCode, " +
                                " DepartmentCode, ProjectCode, SalesPersonCode, " +
                                " LocationCode, LineNo, BlankLine, " +
                                " WarrantyDate, DocType )values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(4)) + "', '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "'," +
                                " '" + Decode.setChar(CharType, rsDt.getString(7)) + "', '" + Decode.setChar(CharType, rsDt.getString(8)) + "', '" + rsDt.getString(9) + "'," +
                                " '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "'," +
                                " '" + rsDt.getString(16) + "', '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "'," +
                                " '" + rsDt.getString(19) + "', '" + i + "', '" + rsDt.getString(21) + "', " +
                                " '" + rsDt.getString(22) + "', '" + DocType + "' )";
                        Statement stmtDetail = conn.createStatement();
                        Log.d("SUCCESS DETAIL", vDetail);
                        stmtDetail.execute(vDetail);

                        String vDtOut = "INSERT INTO stk_detail_trn_in (ItemCode, Doc1No, Doc2No, " +
                                "Doc3No, D_ate, BookDate, " +
                                "QtyIN, FactorQty, UOM, " +
                                "UnitCost, HCTax, CusCode," +
                                "DocType1, DocType2, DocType3, " +
                                "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                                "LocationCode, ItemBatch, " +
                                "GlobalTaxCode, DetailTaxCode, MovingAveCost, " +
                                "LandingCost, OCCost)values(" +
                                " '" + rsDt.getString(3) + "', '', '', " +
                                " '" + rsDt.getString(1) + "', '" + datedShort + "', '" + datedShort + "', " +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + Decode.setChar(CharType, rsDt.getString(7)) + "', " +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(13) + "', '" + CusCode + "', " +
                                " '', '', '" + DocType + "', " +
                                " '0', '0', '0', " +
                                " '"+rsDt.getString(19)+"', '', " +
                                " '', '" + rsDt.getString(23) + "', '0'," +
                                " '0','0' )";
                        Statement stmDtOut = conn.createStatement();
                        Log.d("DETAIL TRN OUT", vDtOut);
                        stmDtOut.execute(vDtOut);
                        i++;
                    }
                    stmtHeader.close();
                }
                String vHeader = "INSERT INTO stk_inventory_hd (Doc1No, Doc2No, Doc3No, " +
                        "D_ate, CusCode, CurCode, " +
                        "CurRate1, CurRate2, CurRate3, " +
                        "Attention, GbDisRate1, GbDisRate2," +
                        "GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                        "GbTaxRate2, GbTaxRate3, HCGbTax," +
                        "HCNetAmt, DocType, Reason, " +
                        "ApprovedYN, PostedYN, UDRunNo," +
                        " L_ink, SyncYN) VALUES ('" + Doc1No + "', '', '', " +
                        " '" + datedShort + "', '" + CusCode + "', '" + CurCode + "', " +
                        " '1', 1, 1, " +
                        " 'Attention', '0', 0," +
                        "  0, 0, '" + GbTaxRate1 + "'," +
                        "  0, 0, '" + HCGbTax + "', " +
                        " '" + TotalAmt + "', '" + DocType + "', 'Reason'," +
                        " '1', '1', 0, " +
                        " '1', '" + DBStatus + "')";
                db.exeQuery(vHeader);

                String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                        " Description, QtyIN, FactorQty," +
                        " UOM, UOMSingular, HCUnitCost," +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine," +
                        " WarrantyDate, DocType, SyncYN) " +
                        " SELECT '" + Doc1No + "', N_o, ItemCode, " +
                        " Description, QtyOUT, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1," +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine, " +
                        " '" + datedShort + "', '" + DocType + "',  '" + DBStatus + "' " +
                        "FROM cloud_inventory_dt WHERE ComputerName='" + deviceId + "' ";
                long addDetail = db.exeQuery(vDetail);
                if (addDetail > 0) {
                    Log.d("SUCCESS DETAIL", "inserted");
                } else {
                    Log.d("ERROR DETAIL", "failed");
                }
                String vDtOut = "INSERT INTO stk_detail_trn_in (ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyIN, FactorQty, UOM, " +
                        "UnitCost, HCTax, CusCode," +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, " +
                       " MovingAveCost, LandingCost, OCCost, "+
                        "GlobalTaxCode, DetailTaxCode, SyncYN)" +
                        "Select ItemCode, '', '', " +
                        " '" + Doc1No + "', '" + datedShort + "', '" + datedShort + "', " +
                        " QtyOUT, FactorQty, UOM, " +
                        " HCUnitCost, HCTax, '" + CusCode + "', " +
                        " '', '', '" + DocType + "', " +
                        " '0', '0', '0', " +
                        " LocationCode, '', " +
                        " '0', '0', '0', " +
                        " '" + GlobalTaxCode + "', DetailTaxCode, '" + DBStatus + "'  " +
                        " from cloud_inventory_dt WHERE ComputerName='" + deviceId + "'";
                db.exeQuery(vDtOut);
                //end detailtrnout
            }
        }catch (SQLiteException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static FnCalculateHCNetAmt fncalculatehcnetamt(Context c, String DocType, String UUID, String vGlobalTaxCode, String TaxType, Double R_ate){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt=0.00;
        vHCTax=0.00;
        HCGbDiscount=0.00;
        HCDtTax=0.00;
        HCGbTax=0.00;
        HCNetAmt=0.00;
        GbTaxRate1=0.00;
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        String strSQL="SELECT  IFNULL(sum(HCTax),0) as vHCTax, IFNULL(sum(HCLineAmt),0) as vHCLineAmt, " +
                "IFNULL(sum(HCDiscount),0) as HCGbDiscount " +
                "FROM cloud_inventory_dt WHERE ComputerName = '"+UUID+"' and DocType='"+DocType+"' GROUP BY '' ";
        Cursor rsNetAmt=db.getQuery(strSQL);
        while (rsNetAmt.moveToNext()) {
            vHCTax          = rsNetAmt.getDouble(0);
            vHCLineAmt      = rsNetAmt.getDouble(1);
            HCGbDiscount    = rsNetAmt.getDouble(2);
        }

        if(vGlobalTaxCode.equals("")){
            HCDtTax 		= vHCTax;
            HCGbTax 		= 0.00;
            HCNetAmt 		= vHCLineAmt;
            GbTaxRate1		= 0.00;
        }else{
            AmountB4Tax 	= vHCLineAmt;
            HCDtTax			= 0.00;
            GbTaxRate1		= R_ate;
            //0,2 is inclusive
            if(TaxType.equals("0") || TaxType.equals("2")){
                HCGbTax	= AmountB4Tax *(GbTaxRate1/(GbTaxRate1 + 100));
                HCNetAmt	= AmountB4Tax;
            }else if(TaxType.equals("1") || TaxType.equals("3")){
                HCGbTax	= AmountB4Tax *(GbTaxRate1/100);
                HCNetAmt	= AmountB4Tax + HCGbTax;
            }
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode);
    }

    private void fnrefresh(){
        if(DocType.equals("IS")) {
            ((IssueStock) c).newRefresh();
        }else if(DocType.equals("GRNDO")){
            ((GRNDO) c).newRefresh();
        }else if(DocType.equals("RS")){
            ((ReceiptStock) c).newRefresh();
        }else if(DocType.equals("TS")){
            ((TransferStock) c).newRefresh();
        }
    }
    public void fnsavelastno(){
        String queryLast="Select LastNo,Prefix from sys_runno_dt where RunNoCode='"+DocType+"' ";
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor rsLast   =db.getQuery(queryLast);
        Integer NewNo=0;
        String Prefix="";
        while (rsLast.moveToNext()) {
            LastNo  = "1"+rsLast.getString(0);
            Prefix  = "1"+rsLast.getString(1);
            NewNo   = (Integer.parseInt(LastNo)) + 1;
        }
        NewDoc = String.valueOf(NewNo);
        String vNewDoc = NewDoc.substring(1,NewDoc.length());
        Doc1No=Prefix+vNewDoc;
        String update="update sys_runno_dt set LastNo='"+vNewDoc+"' where RunNoCode='"+DocType+"' ";
        db.exeQuery(update);
        db.closeDB();
       // ContentValues cv=new ContentValues();
       // cv.put("LastNo",vNewDoc);
       // dbAdapter.UpdateSysRunNo(cv,RunNo);
    }

    /*public void fngenerate58(String Doc1No){
        String vLine1,vLine2,vLine3,vLine4,vLine4_1,vLine5,vLine6,vLine7,vLine8,vLine9,vLine10,vLine11,vLine12,vLine13,vLine14,vLine15,vLine16,vLine17;
        String vLine18,vLine19,vLine20,vLine21,vLine22,vLine23,vLine24,vLine25,vLine4_2,vLine4_3,vLine4_4;
        vLine1="";
        vLine2="";
        vLine3="";
        vLine4="";
        vLine4_1="";
        vLine4_2="";
        vLine4_3="";
        vLine4_4="";
        vLine5="";
        vLine6="";
        vLine7="";
        vLine8="";
        vLine9="";
        vLine10="";
        vLine11="";
        vLine12="";
        vLine13="";
        vLine14="";
        vLine15="";
        vLine16="";
        vLine17="";
        vLine18="";
        vLine19="";
        vLine20="";
        vLine21="";
        vLine22="";
        vLine23="";
        vLine24="";
        vLine25="";

        String strPrint="";
        String CompanyName="";
        String CompanyCode="";
        String GSTNo="";
        String Address="";
        String ComTown="";
        String ComState="";
        String ComCountry="";
        String Tel="";
        String Fax="";
        String CusCode="";
        String CusName="";
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        String vCC1No="";
        DBAdapter db = new DBAdapter(c);
        db.openDB();

        String Company="select CurCode,CompanyName,CompanyCode,GSTNo,Address,Tel1,Fax1," +
                "CompanyEmail,ComTown,ComState,ComCountry" +
                " FROM companysetup ";
        Cursor rsCom=db.getQuery(Company);
        while (rsCom.moveToNext()) {
            CompanyName = rsCom.getString(1);
            CompanyCode = rsCom.getString(2);
            GSTNo       = rsCom.getString(3);
            Address     = rsCom.getString(4);
            Tel         = rsCom.getString(5);
            Fax         = rsCom.getString(6);
            ComTown     = rsCom.getString(8);
            ComState    = rsCom.getString(9);
            ComCountry  = rsCom.getString(10);

        }

        vLine1   =str_pad(CompanyName, 32, " ", "STR_PAD_BOTH");
        vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 32, " ", "STR_PAD_BOTH");
        vLine3   =str_pad("GST Reg.No.: "+GSTNo, 32, " ", "STR_PAD_BOTH");
        String AddressAll = Address.replaceAll("(.{32})", "$1;");
        String[] address=AddressAll.split(";");
        String vAddress="";
        for(String add:address){
            vAddress += str_pad(add,32," ", "STR_PAD_BOTH");
        }
        vLine4=vAddress;
        vLine5   =str_pad(Tel, 32, " ", "STR_PAD_BOTH");
        vLine6   =str_pad(Fax, 32, " ", "STR_PAD_BOTH");
        vLine7 = str_pad("CREDIT NOTE", 32, " ", "STR_PAD_BOTH");

        String qCustomer="select H.CusCode,C.CusName,C.Address from stk_cus_inv_hd H inner join customer C" +
                " on H.CusCode=C.CusCode where H.Doc1No='"+Doc1No+"'";
        Cursor rsCus=db.getQuery(qCustomer);
        while(rsCus.moveToNext()){
            CusCode=rsCus.getString(0);
            CusName=rsCus.getString(1);
            AdddresCus=rsCus.getString(2);
        }

        vLine9  = str_pad("Bill #  : "+Doc1No, 32, " ", "STR_PAD_RIGHT");
        vLine11 = str_pad(datedTime, 32, " ", "STR_PAD_RIGHT");
        vLine12 = str_pad("BILL TO : "+CusName, 32, " ", "STR_PAD_RIGHT");
        vLine13 = str_pad(AdddresCus, 32, " ", "STR_PAD_RIGHT");

        String vLine131=str_pad("Qty", 7, " ", "STR_PAD_RIGHT");
        String vLine132=str_pad("Price", 11, " ", "STR_PAD_BOTH");
        String vLine133=str_pad("Amount ("+CurCode+")", 14, " ", "STR_PAD_LEFT");

        strPrint += vLine1+"\n";
        strPrint += vLine2+"\n";
        strPrint += vLine3+"\n";
        strPrint += vLine4+"\n";

        strPrint += "________________________________\n";
        strPrint += vLine7+"\n";
        strPrint += "________________________________\n";
        //end header

        //customer
        // strPrint += vLine8+"\n";
        strPrint += vLine9+"\n";
        strPrint += vLine11+"\n";
        strPrint += vLine12+"\n";
        if(!CusCode.equals("999999")) {
            strPrint += vLine13 + "\n";
        }
        strPrint += "________________________________\n";
        strPrint += vLine131+vLine132+vLine133+"\n";
        strPrint += "________________________________\n";
        //end customer

        //detail
        String qDetail="select ItemCode,IFNULL(Qty,0) as Qty,UOM,DetailTaxCode,substr(Description,1,26) as Description," +
                "IFNULL(HCUnitCost,0) as HCUnitCost,IFNULL(HCLineAmt,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                "IFNULL(HCDiscount,0) as HCDiscount " +
                "from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
        Cursor rsDt=db.getQuery(qDetail);
        while (rsDt.moveToNext()) {
            String ItemCode         = rsDt.getString(0);
            Double dQty             = rsDt.getDouble(1);
            String Qty              = String.format(Locale.US, "%,.2f",dQty );
            String UOM              = rsDt.getString(2);
            String DetailTaxCode    = rsDt.getString(3);
            String Description      = rsDt.getString(4);
            Double dHCUnitCost      =rsDt.getDouble(5);
            String HCUnitCost       = String.format(Locale.US, "%,.2f",dHCUnitCost);
            Double dHCLineAmt       = rsDt.getDouble(6);
            String HCLineAmt        = String.format(Locale.US, "%,.2f",dHCLineAmt);
            Double dDisRate1        = rsDt.getDouble(7);
            String DisRate1         = String.format(Locale.US, "%,.2f",dDisRate1);
            Double dHCDiscount      = rsDt.getDouble(8);
            String HCDiscount       = String.format(Locale.US, "%,.2f",dHCDiscount);
            vLine14                 =str_pad(Description, 26, " ", "STR_PAD_RIGHT");
            String vLine14_1        =str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
            String vLine15_1        =str_pad(Qty, 4, " ", "STR_PAD_LEFT");
            String vLine15_2        =str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
            //String vLine15_3        =str_pad(Disc2, 6, " ", "STR_PAD_LEFT");
            String vLine15_4        =str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
            strPrint 				+= vLine14_1+vLine14+"\n";
            strPrint 				+= vLine15_1+" x"+vLine15_2+vLine15_4+"\n";
            if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                Disc2		    ="("+HCDiscount+")";
                String vLine15_5        =str_pad(Disc2, 18, " ", "STR_PAD_LEFT");
                String vLine15_6        =str_pad(" ", 13, " ", "STR_PAD_LEFT");
                strPrint 				+= vLine15_5+vLine15_6+"\n";
            }

        }
        //end detail

        //start Total
        // String qTotal="select round(H.HCNetAmt,2)as HCNetAmt,round(H.HCDtTax,2) as HCDtTax,round(R.CashAmt,2) as CashAmt, round(R.ChangeAmt,2) as ChangeAmt,round(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,round(R.BalanceAmount,2) as BalanceAmount from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        String qTotal="select IFNULL(H.HCNetAmt,0)as HCNetAmt,IFNULL(H.HCDtTax,0) as HCDtTax," +
                " IFNULL(sum(D.HCLineAmt)-sum(D.HCTax),0) as AmtExTax, IFNULL(Sum(D.Qty),0) as ItemTender" +
                " from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No " +
                " where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        Cursor rsTot=db.getQuery(qTotal);
        while (rsTot.moveToNext()) {
            Double dTotAmt      =rsTot.getDouble(0);
            String TotAmt       = String.format(Locale.US, "%,.2f",dTotAmt);
            Double dTaxAmt      =rsTot.getDouble(1);
            String TaxAmt       = String.format(Locale.US, "%,.2f",dTaxAmt);
            Double dAmtExtTax   =rsTot.getDouble(2);
            String AmtExtTax    = String.format(Locale.US, "%,.2f",dAmtExtTax);
            Double dItemTender  =rsTot.getDouble(3);
            String ItemTender   = String.format(Locale.US, "%,.2f",dItemTender);
            vLine16             =str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
            vLine17             =str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
            vLine18             =str_pad("0.00", 11, " ", "STR_PAD_LEFT");
            vLine19             =str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
            String vLine22_2    =str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
            strPrint 			+= "________________________________\n\n";
            if(!GSTNo.equals("NO")) {
                strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                strPrint += "Add Total GST Amt : " + vLine17 + "\n";
                strPrint += "Rounding          : " + vLine18 + "\n";
                strPrint += "Total Amount Due  : " + vLine19 + "\n";
                //strPrint += "Paid Amount       : " + vLine20 + "\n";
            }else{
                strPrint += "Total Amt Payable : " + vLine19 + "\n";
                strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";

            }

            if(!GSTNo.equals("NO")) {
                strPrint += "________________________________\n";
                strPrint += "GST Summary \n";
                strPrint += "Code  Rate   Goods Amt  GST Amt\n";
            }
        }
        //end total
        //start GST
        String qGST="select IFNULL(TaxRate1,0)as TaxRate1,DetailTaxCode," +
                "IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                "IFNULL(sum(HCTax),0) as GSTAmt " +
                "from stk_cus_inv_dt  " +
                "Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
        Cursor rsGST=db.getQuery(qGST);
        while (rsGST.moveToNext()) {
            Double dTaxRate      =rsGST.getDouble(0);
            String TaxRate       = String.format(Locale.US, "%,.2f",dTaxRate);
            String TaxCode       =rsGST.getString(1);
            Double dGoodAmt      =rsGST.getDouble(2);
            String GoodAmt       = String.format(Locale.US, "%,.2f",dGoodAmt);
            Double dGSTAmt       =rsGST.getDouble(3);
            String GSTAmt        = String.format(Locale.US, "%,.2f",dGSTAmt);
            String vLine23_1     =str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
            String vLine23_2     =str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
            String vLine23_3     =str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
            String vLine23_4     =str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
            vLine23              =vLine23_1+vLine23_2+vLine23_3+vLine23_4;
            if(!GSTNo.equals("NO")) {
                strPrint += vLine23 + "\n";
            }
        }
        //end GST
        strPrint 				+= "________________________________\n";
        strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
        strPrint 				+= "Thank you, please come again! \n";
        strPrint 				+= "________________________________\n \n\n";

        Log.d("RESULT",strPrint);
        stringPrint=strPrint;
        //return stringPrint;
    }

    public void fngenerate78(String Doc1No){
        String vLine1,vLine2,vLine3,vLine4,vLine4_1,vLine5,vLine6,vLine7,vLine8,vLine9,vLine10,vLine11,vLine12,vLine13,vLine14,vLine15,vLine16,vLine17;
        String vLine18,vLine19,vLine20,vLine21,vLine22,vLine23,vLine24,vLine25;
        vLine1="";
        vLine2="";
        vLine3="";
        vLine4="";
        vLine4_1="";
        vLine5="";
        vLine6="";
        vLine7="";
        vLine8="";
        vLine9="";
        vLine10="";
        vLine11="";
        vLine12="";
        vLine13="";
        vLine14="";
        vLine15="";
        vLine16="";
        vLine17="";
        vLine18="";
        vLine19="";
        vLine20="";
        vLine21="";
        vLine22="";
        vLine23="";
        vLine24="";
        vLine25="";

        String strPrint="";
        String CompanyName="";
        String CompanyCode="";
        String GSTNo="";
        String Address="";
        String ComTown="";
        String ComState="";
        String ComCountry="";
        String Tel="";
        String Fax="";
        String CusCode="";
        String CusName="";
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        String vCC1No="";
        DBAdapter db = new DBAdapter(c);
        db.openDB();


        String Company="select CurCode,CompanyName,CompanyCode,GSTNo," +
                "Address,Tel1,Fax1,CompanyEmail," +
                "ComTown,ComState,ComCountry FROM companysetup ";
        Cursor rsCom=db.getQuery(Company);
        while (rsCom.moveToNext()) {
            CompanyName = rsCom.getString(1);
            CompanyCode = rsCom.getString(2);
            GSTNo       = rsCom.getString(3);
            Address     = rsCom.getString(4);
            Tel         = rsCom.getString(5);
            Fax         = rsCom.getString(6);
            ComTown     = rsCom.getString(8);
            ComState    = rsCom.getString(9);
            ComCountry  = rsCom.getString(10);

        }
        vLine1   =str_pad(CompanyName, 48, " ", "STR_PAD_BOTH");
        vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 48, " ", "STR_PAD_BOTH");
        vLine3   =str_pad("GST Reg.No.: "+GSTNo, 48, " ", "STR_PAD_BOTH");
        String AddressAll = Address.replaceAll("(.{48})", "$1;");
        String[] address=AddressAll.split(";");
        String vAddress="";
        for(String add:address){
            vAddress += str_pad(add,48," ", "STR_PAD_BOTH");
        }
        vLine4=vAddress;
        vLine5   =str_pad(Tel, 48, " ", "STR_PAD_BOTH");
        vLine6   =str_pad(Fax, 48, " ", "STR_PAD_BOTH");
        vLine7 = str_pad("CREDIT NOTE", 48, " ", "STR_PAD_BOTH");

        String qCustomer="select H.CusCode,C.CusName,C.Address from stk_cus_inv_hd H inner join customer C" +
                " on H.CusCode=C.CusCode where H.Doc1No='"+Doc1No+"'";
        Cursor rsCus=db.getQuery(qCustomer);
        while(rsCus.moveToNext()){
            CusCode=rsCus.getString(0);
            CusName=rsCus.getString(1);
            AdddresCus=rsCus.getString(2);
        }


        vLine8  =str_pad("Bill #: "+Doc1No, 48, " ", "STR_PAD_RIGHT");
        vLine11 =str_pad(datedTime, 48, " ", "STR_PAD_RIGHT");
        vLine12 =str_pad("BILL TO : "+CusName , 48, " ", "STR_PAD_RIGHT");
        vLine13 =str_pad(AdddresCus , 48, " ", "STR_PAD_RIGHT");
        String vLine131=str_pad("Quantity", 9, " ", "STR_PAD_RIGHT");
        String vLine132=str_pad("Unit Price", 19, " ", "STR_PAD_BOTH");
        String vLine133=str_pad("Amount ("+CurCode+")", 20, " ", "STR_PAD_LEFT");
        //end query customer

        strPrint += vLine1+"\n";
        strPrint += vLine2+"\n";
        strPrint += vLine3+"\n";
        strPrint += vLine4+"\n";
        //strPrint += vLine4_1+"\n";
        if(!Tel.equals("")) {
            // strPrint += vLine5 + "\n";
        }
        if(!Fax.equals("")){
            // strPrint += vLine6+"\n";
        }
        strPrint += "________________________________________________\n";
        strPrint += vLine7+"\n";
        strPrint += "________________________________________________\n";
        //end header

        //customer
        strPrint += vLine8+"\n";
        strPrint += vLine11+"\n";
        strPrint += vLine12+"\n";
        if(!CusCode.equals("999999")) {
            strPrint += vLine13 + "\n";
        }
        strPrint += "________________________________________________\n";
        strPrint += vLine131+vLine132+vLine133+"\n";
        strPrint += "________________________________________________\n";
        //end customer

        //detail
        String qDetail="select ItemCode,IFNULL(Qty,0) as Qty,UOM,DetailTaxCode,substr(Description,1,42) as Description," +
                "IFNULL(HCUnitCost,0) as HCUnitCost,IFNULL(HCLineAmt,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                "IFNULL(HCDiscount,0) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
        // Log.d("QUERY",qDetail);
        Cursor rsDt=db.getQuery(qDetail);
        // int numrows=rsDt.getCount();
        //Log.d("NUMROWS",String.valueOf(numrows));
        while (rsDt.moveToNext()) {
            String ItemCode         = rsDt.getString(0);
            Double dQty             = rsDt.getDouble(1);
            String Qty              = String.format(Locale.US, "%,.2f",dQty );
            String UOM              = rsDt.getString(2);
            String DetailTaxCode    = rsDt.getString(3);
            String Description      = rsDt.getString(4);
            Double dHCUnitCost      =rsDt.getDouble(5);
            String HCUnitCost       = String.format(Locale.US, "%,.2f",dHCUnitCost);
            Double dHCLineAmt       = rsDt.getDouble(6);
            String HCLineAmt        = String.format(Locale.US, "%,.2f",dHCLineAmt);
            Double dDisRate1        = rsDt.getDouble(7);
            String DisRate1         = String.format(Locale.US, "%,.2f",dDisRate1);
            Double dHCDiscount      = rsDt.getDouble(8);
            String HCDiscount       = String.format(Locale.US, "%,.2f",dHCDiscount);
            if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                Disc2		    ="("+HCDiscount+")";
            }else{
                Disc2			=" ";
            }
            vLine14                 =str_pad(Description, 42, " ", "STR_PAD_RIGHT");
            String vLine14_1        =str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
            String vLine15_1        =str_pad(Qty, 6, " ", "STR_PAD_LEFT");
            String vLine15_2        =str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
            String vLine15_3        =str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
            String vLine15_4        =str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
            strPrint 				+= vLine14_1+vLine14+"\n";
            strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";

        }
        //end detail

        //start Total
        String qTotal="select IFNULL(H.HCNetAmt,0)as HCNetAmt,IFNULL(H.HCDtTax,0) as HCDtTax," +
                " IFNULL(sum(D.HCLineAmt)-sum(D.HCTax),0) as AmtExTax, IFNULL(Sum(D.Qty),0) as ItemTender " +
                "from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No " +
                " where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        Log.d("QUERY",qTotal);
        Cursor rsTot=db.getQuery(qTotal);
        while (rsTot.moveToNext()) {
            Double dTotAmt      =rsTot.getDouble(0);
            String TotAmt       = String.format(Locale.US, "%,.2f",dTotAmt);
            Double dTaxAmt      =rsTot.getDouble(1);
            String TaxAmt       = String.format(Locale.US, "%,.2f",dTaxAmt);
            Double dAmtExtTax   =rsTot.getDouble(2);
            String AmtExtTax    = String.format(Locale.US, "%,.2f",dAmtExtTax);
            Double dItemTender  =rsTot.getDouble(3);
            String ItemTender   = String.format(Locale.US, "%,.2f",dItemTender);
            vLine16             =str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
            vLine17             =str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
            vLine18             =str_pad("0.00", 22, " ", "STR_PAD_LEFT");
            vLine19             =str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
            String vLine22_2    =str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
            String vLine22_4    =str_pad("xxxxxxxxxxx"+vCC1No, 22, " ", "STR_PAD_LEFT");
            strPrint            += "________________________________________________\n\n";
            if(!GSTNo.equals("NO")) {
                strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                strPrint += "Add Total GST Amount   : " + vLine17 + "\n";
                strPrint += "Rounding               : " + vLine18 + "\n";
                strPrint += "Total Amount Due       : " + vLine19 + "\n";
            }else{
                strPrint += "Total Amt Payable      : " + vLine19 + "\n";
                strPrint += "Total Qty Tender       : " + vLine22_2 + "\n";

            }
            if(!GSTNo.equals("NO")) {
                strPrint          += "________________________________________________\n";
                strPrint          += "GST Summary \n";
                strPrint          += "   Code       Rate      Goods Amt      GST Amt  \n";
            }
        }
        //end total
        //start GST
        String qGST="select IFNULL(TaxRate1,0)as TaxRate1,DetailTaxCode," +
                "IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                "IFNULL(sum(HCTax),0) as GSTAmt " +
                "from stk_cus_inv_dt  " +
                "Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
        Cursor rsGST=db.getQuery(qGST);
        while (rsGST.moveToNext()) {
            Double dTaxRate      =rsGST.getDouble(0);
            String TaxRate       = String.format(Locale.US, "%,.2f",dTaxRate);
            String TaxCode       =rsGST.getString(1);
            Double dGoodAmt       =rsGST.getDouble(2);
            String GoodAmt       = String.format(Locale.US, "%,.2f",dGoodAmt);
            Double dGSTAmt        =rsGST.getDouble(3);
            String GSTAmt       = String.format(Locale.US, "%,.2f",dGSTAmt);
            String vLine23_1     =str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
            String vLine23_2     =str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
            String vLine23_3     =str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
            String vLine23_4     =str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
            if(!GSTNo.equals("NO")) {
                vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                strPrint += vLine23 + "\n";
            }
        }
        //end GST
        strPrint                += "________________________________________________\n";
        strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
        strPrint 				+= "Thank you, please come again! \n";
        strPrint                += "________________________________________________\n\n\n";


        //vPostGlobalTaxYN
        Log.d("PostGlobalTaxYN",vPostGlobalTaxYN);
        stringPrint=strPrint;
        //return stringPrint;
    }
    public String str_pad(String input, int length, String pad, String sense)
    {
        int resto_pad = length - input.length();
        String padded = "";

        if (resto_pad <= 0){ return input; }

        if(sense.equals("STR_PAD_RIGHT"))
        {
            padded  = input;
            padded += _fill_string(pad,resto_pad);
        }
        else if(sense.equals("STR_PAD_LEFT"))
        {
            padded  = _fill_string(pad, resto_pad);
            padded += input;
        }
        else // STR_PAD_BOTH
        {
            int pad_left  = (int) Math.ceil(resto_pad/2);
            int pad_right = resto_pad - pad_left;

            padded  = _fill_string(pad, pad_left);
            padded += input;
            padded += _fill_string(pad, pad_right);
        }
        return padded;
    }


    protected String _fill_string(String pad, int resto )
    {
        boolean first = true;
        String padded = "";

        if (resto >= pad.length())
        {
            for (int i = resto; i >= 0; i = i - pad.length())
            {
                if (i  >= pad.length())
                {
                    if (first){ padded = pad; } else { padded += pad; }
                }
                else
                {
                    if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                }
                first = false;
            }
        }
        else
        {
            padded = pad.substring(0,resto);
        }
        return padded;
    }*/
}
