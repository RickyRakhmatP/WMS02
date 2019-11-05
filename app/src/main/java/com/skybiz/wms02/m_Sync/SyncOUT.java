package com.skybiz.wms02.m_Sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.skybiz.wms02.Synchronize;
import com.skybiz.wms02.m_DataObject.Decode;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SyncOUT extends AsyncTask<Void,Void,String> {
    Context c;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    Connection conn;
    DBAdapter db;

    public SyncOUT(Context c) {
        this.c = c;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.syncOUT();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Failure, sync out data", Toast.LENGTH_SHORT).show();
        }else if(result.equals("ST success")){
            Toast.makeText(c,"Succesful, sync out data Stock Take", Toast.LENGTH_SHORT).show();
            ((Synchronize)c).afterOUT();
        }else if(result.equals("IS success")) {
            Toast.makeText(c, "Succesful, sync out data Issue Take", Toast.LENGTH_SHORT).show();
            ((Synchronize) c).afterOUT();
        }else if(result.equals("GRNDO success")){
                Toast.makeText(c,"Succesful, sync out data GRNDO", Toast.LENGTH_SHORT).show();
                ((Synchronize)c).afterOUT();
        }else if(result.equals("RS success")){
            Toast.makeText(c,"Succesful, sync out data RS", Toast.LENGTH_SHORT).show();
            ((Synchronize)c).afterOUT();
        }else{
            Toast.makeText(c,"Info, sync out data "+z, Toast.LENGTH_SHORT).show();
            ((Synchronize)c).afterOUT();
        }
    }

    private String syncOUT() {
        try {
            db=new DBAdapter(c);
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
            URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
            conn = Connector.connect(URL, UserName, Password);
            //conn= Connect_db.getConnection();
            if (conn != null) {
                String query="select Doc1No, RunNoHd, ItemCode, " +
                        " ItemDesc, OnHandQty, CheckQty, " +
                        " UnitCost, LineAmt,PostedYN, " +
                        " ProcessYN, D_ate, UOM, " +
                        " FactorQty, LocationCode, UserEmail," +
                        " RunNo " +
                        " from ostk_stkcheck_dt where SyncYN='0' ";
                Cursor rsData=db.getQuery(query);
                while(rsData.moveToNext()){
                    String qInsert="insert into ostk_stkcheck_dt(Doc1No, RunNoHd, ItemCode," +
                            " ItemDesc, OnHandQty, CheckQty," +
                            " UnitCost, LineAmt,PostedYN," +
                            " ProcessYN, D_ate, UOM, " +
                            " FactorQty, LocationCode, UserEmail)values(" +
                            " '', '0', '"+rsData.getString(2)+"'," +
                            " '"+rsData.getString(3)+"', '"+rsData.getString(4)+"', '"+rsData.getString(5)+"'," +
                            " '"+rsData.getString(6)+"', '"+rsData.getString(7)+"', '"+rsData.getString(8)+"'," +
                            " '"+rsData.getString(9)+"', '"+rsData.getString(10)+"', '"+rsData.getString(11)+"'," +
                            " '"+rsData.getString(12)+"', '"+rsData.getString(13)+"', '"+rsData.getString(14)+"')";
                    Statement statement = conn.createStatement();
                    statement.execute(qInsert);
                    String vUpdate="update ostk_stkcheck_dt set SyncYN='1' where RunNo='"+rsData.getString(15)+"' ";
                    Log.d("Update Sync OUT", vUpdate);
                    db.exeQuery(vUpdate);
                }
                syncOUT_GRN();
                syncOUT_IS();
                syncOUT_RS();
                syncOUT_TS();
                z="success ST";
            }
            //db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    private String syncOUT_IS(){
        try{
            String DocType="IS";
            if (conn != null) {
                //header
                String QueryH="select RunNo, Doc1No, Doc2No, Doc3No, "+
                        " D_ate, CusCode, CurCode, "+
                        " CurRate1, CurRate2, CurRate3, "+
                        " Attention, GbDisRate1, GbDisRate2, "+
                        " GbDisRate3, HCGbDiscount, GbTaxRate1, "+
                        " GbTaxRate2, GbTaxRate3, HCGbTax, "+
                        " HCNetAmt, DocType, Reason, "+
                        " ApprovedYN, PostedYN, UDRunNo, "+
                        " L_ink "+
                        " from stk_inventory_hd " +
                        " where  DocType='"+DocType+"' and SyncYN='0' ";
                Cursor rsHeader=db.getQuery(QueryH);
                while(rsHeader.moveToNext()){
                    String chekHd="select count(*)as numrows from stk_inventory_hd where Doc1No='"+rsHeader.getString(1)+"' ";
                    Statement stmChkHd = conn.createStatement();
                    stmChkHd.execute(chekHd);
                    ResultSet rsChkHd = stmChkHd.getResultSet();
                    int numhd=0;
                    while (rsChkHd.next()) {
                        numhd=rsChkHd.getInt(1);
                    }
                    String insertH="insert into stk_inventory_hd(Doc1No, Doc2No, Doc3No, " +
                            " D_ate, CusCode, CurCode, " +
                            " CurRate1, CurRate2, CurRate3, " +
                            " Attention, GbDisRate1, GbDisRate2, " +
                            " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            " GbTaxRate2, GbTaxRate3, HCGbTax, " +
                            " HCNetAmt, DocType, Reason, " +
                            " ApprovedYN, PostedYN, UDRunNo, " +
                            " L_ink)values(" +
                            " '"+rsHeader.getString(1)+"', '"+rsHeader.getString(2)+"', '"+rsHeader.getString(3)+"'," +
                            " '"+rsHeader.getString(4)+"', '"+rsHeader.getString(5)+"', '"+rsHeader.getString(6)+"'," +
                            " '"+rsHeader.getString(7)+"', '"+rsHeader.getString(8)+"', '"+rsHeader.getString(9)+"'," +
                            " '"+rsHeader.getString(10)+"', '"+rsHeader.getString(11)+"', '"+rsHeader.getString(12)+"'," +
                            " '"+rsHeader.getString(13)+"', '"+rsHeader.getString(14)+"', '"+rsHeader.getString(15)+"'," +
                            " '"+rsHeader.getString(16)+"', '"+rsHeader.getString(17)+"', '"+rsHeader.getString(18)+"'," +
                            " '"+rsHeader.getString(19)+"', '"+rsHeader.getString(20)+"', '"+rsHeader.getString(21)+"'," +
                            " '"+rsHeader.getString(22)+"', '"+rsHeader.getString(23)+"', '"+rsHeader.getString(24)+"'," +
                            " '"+rsHeader.getString(25)+"')";
                    Statement stmtHeader = conn.createStatement();
                    Log.d("SUCCESS HEADER", insertH);
                    if(numhd==0) {
                        stmtHeader.execute(insertH);
                        String vUpdate = "update stk_inventory_hd set SyncYN='1' where RunNo='" + rsHeader.getString(0) + "' ";
                        db.exeQuery(vUpdate);
                    }else{
                        z=rsHeader.getString(1)+" duplicate";
                    }
                }
                //end header

                //Detail
                String QueryD="select RunNo, Doc1No, N_o, ItemCode, " +
                        " Description, QtyOUT, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine, " +
                        " WarrantyDate " +
                        " from stk_inventory_dt " +
                        " where  DocType='"+DocType+"' and SyncYN='0' ";
                Cursor rsDt=db.getQuery(QueryD);
                int i=1;
                while(rsDt.moveToNext()){
                    String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                                " Description, QtyOUT, FactorQty," +
                                " UOM, UOMSingular, HCUnitCost," +
                                " DisRate1, HCDiscount, TaxRate1," +
                                " HCTax, HCLineAmt, BranchCode, " +
                                " DepartmentCode, ProjectCode, SalesPersonCode, " +
                                " LocationCode, LineNo, BlankLine, " +
                                " WarrantyDate, DocType )values(" +
                                " '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"', '"+rsDt.getString(3)+"'," +
                                " '"+Decode.setChar(CharType,rsDt.getString(4))+"', '"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"'," +
                                " '"+Decode.setChar(CharType,rsDt.getString(7))+"', '"+Decode.setChar(CharType,rsDt.getString(8))+"', '"+rsDt.getString(9)+"'," +
                                " '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"', '"+rsDt.getString(12)+"'," +
                                " '"+rsDt.getString(13)+"', '"+rsDt.getString(14)+"', '"+rsDt.getString(15)+"'," +
                                " '"+rsDt.getString(16)+"', '"+rsDt.getString(17)+"', '"+rsDt.getString(18)+"'," +
                                " '"+rsDt.getString(19)+"', '"+i+"', '"+rsDt.getString(21)+"', " +
                                " '"+rsDt.getString(22)+"', '"+DocType+"' )";
                    Statement stmtDetail = conn.createStatement();
                    Log.d("SUCCESS DETAIL", vDetail);
                    stmtDetail.execute(vDetail);
                    String vUpdate = "update stk_inventory_dt set SyncYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                    i++;
                }
                //end detail

                //dt trn out
                String queryDtOut="select RunNo, ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyOUT, FactorQty, UOM, " +
                        "UnitPrice, HCTax, CusCode, " +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, VoidYN, " +
                        "GlobalTaxCode, DetailTaxCode " +
                        "from stk_detail_trn_out " +
                        "where SyncYN='0' and DocType3='"+DocType+"' ";
                Cursor rsDtOut=db.getQuery(queryDtOut);
                while(rsDtOut.moveToNext()){
                    String insertout="INSERT INTO stk_detail_trn_out(ItemCode, Doc1No, Doc2No, " +
                            "Doc3No, D_ate, BookDate, " +
                            "QtyOUT, FactorQty, UOM, " +
                            "UnitPrice, HCTax, CusCode," +
                            "DocType1, DocType2, DocType3, " +
                            "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                            "LocationCode, ItemBatch, VoidYN, " +
                            "GlobalTaxCode, DetailTaxCode)values(" +
                            "'"+rsDtOut.getString(1)+"', '"+rsDtOut.getString(2)+"', '"+rsDtOut.getString(3)+"'," +
                            "'"+rsDtOut.getString(4)+"', '"+rsDtOut.getString(5)+"', '"+rsDtOut.getString(6)+"'," +
                            "'"+rsDtOut.getString(7)+"', '"+rsDtOut.getString(8)+"', '"+Decode.setChar(CharType,rsDtOut.getString(9))+"'," +
                            "'"+rsDtOut.getString(10)+"', '"+rsDtOut.getString(11)+"', '"+rsDtOut.getString(12)+"'," +
                            "'"+rsDtOut.getString(13)+"', '"+rsDtOut.getString(14)+"', '"+rsDtOut.getString(15)+"'," +
                            "'"+rsDtOut.getString(16)+"', '"+rsDtOut.getString(17)+"', '"+rsDtOut.getString(18)+"'," +
                            "'"+rsDtOut.getString(19)+"', '"+rsDtOut.getString(20)+"', '"+rsDtOut.getString(21)+"', " +
                            "'"+rsDtOut.getString(22)+"', '"+rsDtOut.getString(23)+"') ";
                    Statement stmtDtOur = conn.createStatement();
                    Log.d("DETAIL TRN OUR", insertout);
                    stmtDtOur.execute(insertout);
                    String vUpdate = "update stk_detail_trn_out set SyncYN='1' where RunNo='" + rsDtOut.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                }

                //end dt trn out
                z="IS success";
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    private String syncOUT_RS(){
        try{
            String DocType="RS";
            if (conn != null) {
                //header
                String QueryH="select RunNo, Doc1No, Doc2No, Doc3No, "+
                        " D_ate, CusCode, CurCode, "+
                        " CurRate1, CurRate2, CurRate3, "+
                        " Attention, GbDisRate1, GbDisRate2, "+
                        " GbDisRate3, HCGbDiscount, GbTaxRate1, "+
                        " GbTaxRate2, GbTaxRate3, HCGbTax, "+
                        " HCNetAmt, DocType, Reason, "+
                        " ApprovedYN, PostedYN, UDRunNo, "+
                        " L_ink "+
                        " from stk_inventory_hd " +
                        " where  DocType='"+DocType+"' and SyncYN='0' ";
                Cursor rsHeader=db.getQuery(QueryH);
                while(rsHeader.moveToNext()){
                    String chekHd="select count(*)as numrows from stk_inventory_hd where Doc1No='"+rsHeader.getString(1)+"' ";
                    Statement stmChkHd = conn.createStatement();
                    stmChkHd.execute(chekHd);
                    ResultSet rsChkHd = stmChkHd.getResultSet();
                    int numhd=0;
                    while (rsChkHd.next()) {
                        numhd=rsChkHd.getInt(1);
                    }
                    String insertH="insert into stk_inventory_hd(Doc1No, Doc2No, Doc3No, " +
                            " D_ate, CusCode, CurCode, " +
                            " CurRate1, CurRate2, CurRate3, " +
                            " Attention, GbDisRate1, GbDisRate2, " +
                            " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            " GbTaxRate2, GbTaxRate3, HCGbTax, " +
                            " HCNetAmt, DocType, Reason, " +
                            " ApprovedYN, PostedYN, UDRunNo, " +
                            " L_ink)values(" +
                            " '"+rsHeader.getString(1)+"', '"+rsHeader.getString(2)+"', '"+rsHeader.getString(3)+"'," +
                            " '"+rsHeader.getString(4)+"', '"+rsHeader.getString(5)+"', '"+rsHeader.getString(6)+"'," +
                            " '"+rsHeader.getString(7)+"', '"+rsHeader.getString(8)+"', '"+rsHeader.getString(9)+"'," +
                            " '"+rsHeader.getString(10)+"', '"+rsHeader.getString(11)+"', '"+rsHeader.getString(12)+"'," +
                            " '"+rsHeader.getString(13)+"', '"+rsHeader.getString(14)+"', '"+rsHeader.getString(15)+"'," +
                            " '"+rsHeader.getString(16)+"', '"+rsHeader.getString(17)+"', '"+rsHeader.getString(18)+"'," +
                            " '"+rsHeader.getString(19)+"', '"+rsHeader.getString(20)+"', '"+rsHeader.getString(21)+"'," +
                            " '"+rsHeader.getString(22)+"', '"+rsHeader.getString(23)+"', '"+rsHeader.getString(24)+"'," +
                            " '"+rsHeader.getString(25)+"')";
                    Statement stmtHeader = conn.createStatement();
                    Log.d("SUCCESS HEADER", insertH);
                    if(numhd==0) {
                        stmtHeader.execute(insertH);
                        String vUpdate = "update stk_inventory_hd set SyncYN='1' where RunNo='" + rsHeader.getString(0) + "' ";
                        db.exeQuery(vUpdate);
                    }else{
                        z=rsHeader.getString(1)+" duplicate";
                    }
                }
                //end header

                //Detail
                String QueryD="select RunNo, Doc1No, N_o, ItemCode, " +
                        " Description, QtyIN, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine, " +
                        " WarrantyDate " +
                        " from stk_inventory_dt " +
                        " where  DocType='"+DocType+"' and SyncYN='0' ";
                Cursor rsDt=db.getQuery(QueryD);
                int i=1;
                while(rsDt.moveToNext()){
                    String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                            " Description, QtyIN, FactorQty," +
                            " UOM, UOMSingular, HCUnitCost," +
                            " DisRate1, HCDiscount, TaxRate1," +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, SalesPersonCode, " +
                            " LocationCode, LineNo, BlankLine, " +
                            " WarrantyDate, DocType )values(" +
                            " '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"', '"+rsDt.getString(3)+"'," +
                            " '"+Decode.setChar(CharType,rsDt.getString(4))+"', '"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"'," +
                            " '"+Decode.setChar(CharType,rsDt.getString(7))+"', '"+Decode.setChar(CharType,rsDt.getString(8))+"', '"+rsDt.getString(9)+"'," +
                            " '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"', '"+rsDt.getString(12)+"'," +
                            " '"+rsDt.getString(13)+"', '"+rsDt.getString(14)+"', '"+rsDt.getString(15)+"'," +
                            " '"+rsDt.getString(16)+"', '"+rsDt.getString(17)+"', '"+rsDt.getString(18)+"'," +
                            " '"+rsDt.getString(19)+"', '"+i+"', '"+rsDt.getString(21)+"', " +
                            " '"+rsDt.getString(22)+"', '"+DocType+"' )";
                    Statement stmtDetail = conn.createStatement();
                    Log.d("SUCCESS DETAIL", vDetail);
                    stmtDetail.execute(vDetail);
                    String vUpdate = "update stk_inventory_dt set SyncYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                    i++;
                }
                //end detail

                //dt trn out
                String queryDtOut="select RunNo, ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyIN, FactorQty, UOM, " +
                        "UnitCost, HCTax, CusCode, " +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, GlobalTaxCode, " +
                        "DetailTaxCode, MovingAveCost, LandingCost, " +
                        "OCCost " +
                        " from stk_detail_trn_in where SyncYN='0' and DocType3='"+DocType+"' ";
                Cursor rsDtOut=db.getQuery(queryDtOut);
                while(rsDtOut.moveToNext()){
                    String insertdt="INSERT INTO stk_detail_trn_in(ItemCode, Doc1No, Doc2No, " +
                            "Doc3No, D_ate, BookDate, " +
                            "QtyIN, FactorQty, UOM, " +
                            "UnitCost, HCTax, CusCode," +
                            "DocType1, DocType2, DocType3, " +
                            "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                            "LocationCode, ItemBatch, GlobalTaxCode, " +
                            "DetailTaxCode, MovingAveCost, LandingCost," +
                            "OCCost)values(" +
                            "'"+rsDtOut.getString(1)+"', '"+rsDtOut.getString(2)+"', '"+rsDtOut.getString(3)+"'," +
                            "'"+rsDtOut.getString(4)+"', '"+rsDtOut.getString(5)+"', '"+rsDtOut.getString(6)+"'," +
                            "'"+rsDtOut.getString(7)+"', '"+rsDtOut.getString(8)+"', '"+Decode.setChar(CharType,rsDtOut.getString(9))+"'," +
                            "'"+rsDtOut.getString(10)+"', '"+rsDtOut.getString(11)+"', '"+rsDtOut.getString(12)+"'," +
                            "'"+rsDtOut.getString(13)+"', '"+rsDtOut.getString(14)+"', '"+rsDtOut.getString(15)+"'," +
                            "'"+rsDtOut.getString(16)+"', '"+rsDtOut.getString(17)+"', '"+rsDtOut.getString(18)+"'," +
                            "'"+rsDtOut.getString(19)+"', '"+rsDtOut.getString(20)+"', '"+rsDtOut.getString(21)+"', " +
                            "'"+rsDtOut.getString(22)+"', '"+rsDtOut.getString(23)+"', '"+rsDtOut.getString(24)+"', " +
                            "'"+rsDtOut.getString(25)+"' ) ";
                    Statement stmtDtIn = conn.createStatement();
                    Log.d("DETAIL TRN IN", insertdt);
                    stmtDtIn.execute(insertdt);
                    String vUpdate = "update stk_detail_trn_in set SyncYN='1' where RunNo='" + rsDtOut.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                }

                //end dt trn out
                z="RS success";
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    private String syncOUT_GRN(){
        try{
            String DocType="GRNDO";
            if (conn != null) {
                //header
                String QueryH="select RunNo, Doc1No, Doc2No, Doc3No, " +
                        " D_ate, CusCode, CurCode, " +
                        " CurRate1, CurRate2, CurRate3, " +
                        " Attention, GbDisRate1, GbDisRate2, " +
                        " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                        " GbTaxRate2, GbTaxRate3, HCGbTax, " +
                        " HCNetAmt, DocType, GlobalTaxCode, " +
                        " HCDtTax, L_ink"+
                        " from stk_grndo_hd " +
                        " where SyncYN='0' ";
                Cursor rsHeader=db.getQuery(QueryH);
                while(rsHeader.moveToNext()){
                    String chekHd="select count(*)as numrows from stk_grndo_hd where Doc1No='"+rsHeader.getString(1)+"' ";
                    Statement stmChkHd = conn.createStatement();
                    stmChkHd.execute(chekHd);
                    ResultSet rsChkHd = stmChkHd.getResultSet();
                    int numhd=0;
                    while (rsChkHd.next()) {
                        numhd=rsChkHd.getInt(1);
                    }
                    String insertH="insert into stk_grndo_hd(Doc1No, Doc2No, Doc3No, " +
                            " D_ate, CusCode, CurCode, " +
                            " CurRate1, CurRate2, CurRate3, " +
                            " Attention, GbDisRate1, GbDisRate2, " +
                            " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            " GbTaxRate2, GbTaxRate3, HCGbTax, " +
                            " HCNetAmt, DocType, GlobalTaxCode, " +
                            " HCDtTax,L_ink)values(" +
                            " '"+rsHeader.getString(1)+"', '"+rsHeader.getString(2)+"', '"+rsHeader.getString(3)+"'," +
                            " '"+rsHeader.getString(4)+"', '"+rsHeader.getString(5)+"', '"+rsHeader.getString(6)+"'," +
                            " '"+rsHeader.getString(7)+"', '"+rsHeader.getString(8)+"', '"+rsHeader.getString(9)+"'," +
                            " '"+rsHeader.getString(10)+"', '"+rsHeader.getString(11)+"', '"+rsHeader.getString(12)+"'," +
                            " '"+rsHeader.getString(13)+"', '"+rsHeader.getString(14)+"', '"+rsHeader.getString(15)+"'," +
                            " '"+rsHeader.getString(16)+"', '"+rsHeader.getString(17)+"', '"+rsHeader.getString(18)+"'," +
                            " '"+rsHeader.getString(19)+"', '"+rsHeader.getString(20)+"', '"+rsHeader.getString(21)+"'," +
                            " '"+rsHeader.getString(22)+"', '"+rsHeader.getString(23)+"' )";
                    Statement stmtHeader = conn.createStatement();
                    Log.d("SUCCESS HEADER", insertH);
                    if(numhd==0) {
                        stmtHeader.execute(insertH);
                        String vUpdate = "update stk_grndo_hd set SyncYN='1' where RunNo='" + rsHeader.getString(0) + "' ";
                        db.exeQuery(vUpdate);
                    }else{
                        z=rsHeader.getString(1)+" duplicate";
                    }
                }
                //end header

                //Detail
                String QueryD="select RunNo, Doc1No, N_o, ItemCode, " +
                        " Description, Qty, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, LocationCode, " +
                        " LineNo, BlankLine, WarrantyDate," +
                        " PORunNo, DetailTaxCode " +
                        " from stk_grndo_dt " +
                        " where SyncYN='0' ";
                Cursor rsDt=db.getQuery(QueryD);
                int i=1;
                while(rsDt.moveToNext()){
                    String vDetail = "INSERT INTO stk_grndo_dt (Doc1No, N_o, ItemCode, " +
                            " Description, Qty, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, LocationCode, " +
                            " LineNo, BlankLine, WarrantyDate, " +
                            " PORunNo, DetailTaxCode)values(" +
                            " '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"', '"+rsDt.getString(3)+"'," +
                            " '"+Decode.setChar(CharType,rsDt.getString(4))+"', '"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"'," +
                            " '"+Decode.setChar(CharType,rsDt.getString(7))+"', '"+Decode.setChar(CharType,rsDt.getString(8))+"', '"+rsDt.getString(9)+"'," +
                            " '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"', '"+rsDt.getString(12)+"'," +
                            " '"+rsDt.getString(13)+"', '"+rsDt.getString(14)+"', '"+rsDt.getString(15)+"'," +
                            " '"+rsDt.getString(16)+"', '"+rsDt.getString(17)+"', '"+rsDt.getString(18)+"'," +
                            " '"+i+"', '"+rsDt.getString(20)+"', '"+rsDt.getString(21)+"', " +
                            " '"+rsDt.getString(22)+"', '"+rsDt.getString(23)+"' )";
                    Statement stmtDetail = conn.createStatement();
                    Log.d("SUCCESS DETAIL", vDetail);
                    stmtDetail.execute(vDetail);
                    String vUpdate = "update stk_grndo_dt set SyncYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                    i++;
                }
                //end detail
                z="GRNDO success";
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    private String syncOUT_TS(){
        try{
            String DocType="TS";
            if (conn != null) {
                //header
                String QueryH="select RunNo, Doc1No, Doc2No, Doc3No, "+
                        " D_ate, CusCode, CurCode, "+
                        " CurRate1, CurRate2, CurRate3, "+
                        " Attention, GbDisRate1, GbDisRate2, "+
                        " GbDisRate3, HCGbDiscount, GbTaxRate1, "+
                        " GbTaxRate2, GbTaxRate3, HCGbTax, "+
                        " HCNetAmt, DocType, Reason, "+
                        " ApprovedYN, PostedYN, UDRunNo, "+
                        " L_ink "+
                        " from stk_inventory_hd " +
                        " where  DocType='"+DocType+"' and SyncYN='0' ";
                Cursor rsHeader=db.getQuery(QueryH);
                while(rsHeader.moveToNext()){
                    String chekHd="select count(*)as numrows from stk_inventory_hd where Doc1No='"+rsHeader.getString(1)+"' ";
                    Statement stmChkHd = conn.createStatement();
                    stmChkHd.execute(chekHd);
                    ResultSet rsChkHd = stmChkHd.getResultSet();
                    int numhd=0;
                    while (rsChkHd.next()) {
                        numhd=rsChkHd.getInt(1);
                    }
                    String insertH="insert into stk_inventory_hd(Doc1No, Doc2No, Doc3No, " +
                            " D_ate, CusCode, CurCode, " +
                            " CurRate1, CurRate2, CurRate3, " +
                            " Attention, GbDisRate1, GbDisRate2, " +
                            " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                            " GbTaxRate2, GbTaxRate3, HCGbTax, " +
                            " HCNetAmt, DocType, Reason, " +
                            " ApprovedYN, PostedYN, UDRunNo, " +
                            " L_ink)values(" +
                            " '"+rsHeader.getString(1)+"', '"+rsHeader.getString(2)+"', '"+rsHeader.getString(3)+"'," +
                            " '"+rsHeader.getString(4)+"', '"+rsHeader.getString(5)+"', '"+rsHeader.getString(6)+"'," +
                            " '"+rsHeader.getString(7)+"', '"+rsHeader.getString(8)+"', '"+rsHeader.getString(9)+"'," +
                            " '"+rsHeader.getString(10)+"', '"+rsHeader.getString(11)+"', '"+rsHeader.getString(12)+"'," +
                            " '"+rsHeader.getString(13)+"', '"+rsHeader.getString(14)+"', '"+rsHeader.getString(15)+"'," +
                            " '"+rsHeader.getString(16)+"', '"+rsHeader.getString(17)+"', '"+rsHeader.getString(18)+"'," +
                            " '"+rsHeader.getString(19)+"', '"+rsHeader.getString(20)+"', '"+rsHeader.getString(21)+"'," +
                            " '"+rsHeader.getString(22)+"', '"+rsHeader.getString(23)+"', '"+rsHeader.getString(24)+"'," +
                            " '"+rsHeader.getString(25)+"')";
                    Statement stmtHeader = conn.createStatement();
                    Log.d("SUCCESS HEADER", insertH);
                    if(numhd==0) {
                        stmtHeader.execute(insertH);
                        String vUpdate = "update stk_inventory_hd set SyncYN='1' where RunNo='" + rsHeader.getString(0) + "' ";
                        db.exeQuery(vUpdate);
                    }else{
                        z=rsHeader.getString(1)+" duplicate";
                    }
                }
                //end header

                //Detail
                String QueryD="select RunNo, Doc1No, N_o, ItemCode, " +
                        " Description, QtyIN, FactorQty, " +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, HCLineAmt, BranchCode, " +
                        " DepartmentCode, ProjectCode, SalesPersonCode, " +
                        " LocationCode, LineNo, BlankLine, " +
                        " WarrantyDate, QtyOUT, LocationCodeTo " +
                        " from stk_inventory_dt " +
                        " where  DocType='"+DocType+"' and SyncYN='0' ";
                Cursor rsDt=db.getQuery(QueryD);
                int i=1;
                while(rsDt.moveToNext()){
                    String vDetail = "INSERT INTO stk_inventory_dt (Doc1No, N_o, ItemCode," +
                            " Description, QtyIN, FactorQty," +
                            " UOM, UOMSingular, HCUnitCost," +
                            " DisRate1, HCDiscount, TaxRate1," +
                            " HCTax, HCLineAmt, BranchCode, " +
                            " DepartmentCode, ProjectCode, SalesPersonCode, " +
                            " LocationCode, LineNo, BlankLine, " +
                            " WarrantyDate, DocType, QtyOUT," +
                            " LocationCodeTo )values(" +
                            " '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"', '"+rsDt.getString(3)+"'," +
                            " '"+Decode.setChar(CharType,rsDt.getString(4))+"', '"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"'," +
                            " '"+Decode.setChar(CharType,rsDt.getString(7))+"', '"+Decode.setChar(CharType,rsDt.getString(8))+"', '"+rsDt.getString(9)+"'," +
                            " '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"', '"+rsDt.getString(12)+"'," +
                            " '"+rsDt.getString(13)+"', '"+rsDt.getString(14)+"', '"+rsDt.getString(15)+"'," +
                            " '"+rsDt.getString(16)+"', '"+rsDt.getString(17)+"', '"+rsDt.getString(18)+"'," +
                            " '"+rsDt.getString(19)+"', '"+i+"', '"+rsDt.getString(21)+"', " +
                            " '"+rsDt.getString(22)+"', '"+DocType+"', '"+rsDt.getString(23)+"'," +
                            " '"+rsDt.getString(24)+"' )";
                    Statement stmtDetail = conn.createStatement();
                    Log.d("SUCCESS DETAIL", vDetail);
                    stmtDetail.execute(vDetail);
                    String vUpdate = "update stk_inventory_dt set SyncYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                    i++;
                }
                //end detail

                //dt trn out
                String qDtOut = "Select RunNo, ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyOUT, FactorQty, UOM, " +
                        "UnitPrice, HCTax, CusCode," +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, VoidYN, " +
                        "GlobalTaxCode, DetailTaxCode from stk_detail_trn_out " +
                        "where SyncYN='0' and DocType3='"+DocType+"' ";
                Cursor rsDtOut=db.getQuery(qDtOut);
                while(rsDtOut.moveToNext()){
                    String insertdtout="INSERT INTO stk_detail_trn_out (ItemCode, Doc1No, Doc2No, " +
                            "Doc3No, D_ate, BookDate, " +
                            "QtyIN, FactorQty, UOM, " +
                            "UnitCost, HCTax, CusCode," +
                            "DocType1, DocType2, DocType3, " +
                            "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                            "LocationCode, ItemBatch, " +
                            "GlobalTaxCode, DetailTaxCode, MovingAveCost, " +
                            "LandingCost, OCCost)values(" +
                            "'"+rsDtOut.getString(1)+"', '"+rsDtOut.getString(2)+"', '"+rsDtOut.getString(3)+"'," +
                            "'"+rsDtOut.getString(4)+"', '"+rsDtOut.getString(5)+"', '"+rsDtOut.getString(6)+"'," +
                            "'"+rsDtOut.getString(7)+"', '"+rsDtOut.getString(8)+"', '"+rsDtOut.getString(9)+"'," +
                            "'"+rsDtOut.getString(10)+"', '"+rsDtOut.getString(11)+"', '"+rsDtOut.getString(12)+"'," +
                            "'"+rsDtOut.getString(13)+"', '"+rsDtOut.getString(14)+"', '"+rsDtOut.getString(15)+"', " +
                            "'"+rsDtOut.getString(16)+"', '"+rsDtOut.getString(17)+"', '"+rsDtOut.getString(18)+"'," +
                            "'"+rsDtOut.getString(19)+"', '"+rsDtOut.getString(20)+"', '"+rsDtOut.getString(21)+"'," +
                            "'"+rsDtOut.getString(22)+"', '"+rsDtOut.getString(23)+"', '"+rsDtOut.getString(24)+"'," +
                            "'"+rsDtOut.getString(25)+"', '"+rsDtOut.getString(26)+"')";
                    Log.d("IN DT OUT",insertdtout);
                    db.exeQuery(insertdtout);
                    String vUpdate = "update stk_detail_trn_out set SyncYN='1' where RunNo='" + rsDtOut.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                }

                //end dt trn out

                //dt trn in
                String qDtIn="select RunNo, ItemCode, Doc1No, Doc2No, " +
                        "Doc3No, D_ate, BookDate, " +
                        "QtyIN, FactorQty, UOM, " +
                        "UnitCost, HCTax, CusCode, " +
                        "DocType1, DocType2, DocType3, " +
                        "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                        "LocationCode, ItemBatch, GlobalTaxCode, " +
                        "DetailTaxCode, MovingAveCost, LandingCost, " +
                        "OCCost " +
                        " from stk_detail_trn_in where SyncYN='0' and DocType3='"+DocType+"' ";
                Cursor rsDtIn=db.getQuery(qDtIn);
                while(rsDtIn.moveToNext()){
                    String insertdt="INSERT INTO stk_detail_trn_in(ItemCode, Doc1No, Doc2No, " +
                            "Doc3No, D_ate, BookDate, " +
                            "QtyIN, FactorQty, UOM, " +
                            "UnitCost, HCTax, CusCode," +
                            "DocType1, DocType2, DocType3, " +
                            "Doc1NoRunNo, Doc2NoRunNo, Doc3NoRunNo, " +
                            "LocationCode, ItemBatch, GlobalTaxCode, " +
                            "DetailTaxCode, MovingAveCost, LandingCost," +
                            "OCCost)values(" +
                            "'"+rsDtIn.getString(1)+"', '"+rsDtIn.getString(2)+"', '"+rsDtIn.getString(3)+"'," +
                            "'"+rsDtIn.getString(4)+"', '"+rsDtIn.getString(5)+"', '"+rsDtIn.getString(6)+"'," +
                            "'"+rsDtIn.getString(7)+"', '"+rsDtIn.getString(8)+"', '"+Decode.setChar(CharType,rsDtIn.getString(9))+"'," +
                            "'"+rsDtIn.getString(10)+"', '"+rsDtIn.getString(11)+"', '"+rsDtIn.getString(12)+"'," +
                            "'"+rsDtIn.getString(13)+"', '"+rsDtIn.getString(14)+"', '"+rsDtIn.getString(15)+"'," +
                            "'"+rsDtIn.getString(16)+"', '"+rsDtIn.getString(17)+"', '"+rsDtIn.getString(18)+"'," +
                            "'"+rsDtIn.getString(19)+"', '"+rsDtIn.getString(20)+"', '"+rsDtIn.getString(21)+"', " +
                            "'"+rsDtIn.getString(22)+"', '"+rsDtIn.getString(23)+"', '"+rsDtIn.getString(24)+"', " +
                            "'"+rsDtIn.getString(25)+"' ) ";
                    Statement stmtDtIn = conn.createStatement();
                    Log.d("DETAIL TRN IN", insertdt);
                    stmtDtIn.execute(insertdt);
                    String vUpdate = "update stk_detail_trn_in set SyncYN='1' where RunNo='" + rsDtIn.getString(0) + "' ";
                    db.exeQuery(vUpdate);
                }

                //end dt trn in
                z="TS success";
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

}
