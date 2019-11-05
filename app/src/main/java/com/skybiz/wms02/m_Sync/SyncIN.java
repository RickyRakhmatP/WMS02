package com.skybiz.wms02.m_Sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.skybiz.wms02.Synchronize;
import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SyncIN extends AsyncTask<Void,Integer,String> {
    Context c;
    ProgressBar pb;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
    int totalrows=0;
    String T_ypeSync="";

    public SyncIN(Context c, ProgressBar pb) {
        this.c = c;
        this.pb=pb;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.pb != null) {
            pb.setMax(totalrows);
            pb.setProgress(values[0]);
            ((Synchronize)c).setProgress(T_ypeSync+" "+values[0] +" of "+ totalrows);
        }
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
            Toast.makeText(c,"Failure, sync in", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Succesful, sync in", Toast.LENGTH_SHORT).show();
           // ((Synchronize)c).afterSave();
        }
    }

    private String downloadData() {
        try {
            DBAdapter db=new DBAdapter(c);
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
            Connection conn = Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn != null) {
                if (DBStatus.equals("0")) {
                    String sqlDel = "delete from stk_master";
                    db.exeQuery(sqlDel);
                    String qTotal = "select count(*)as totalrows from stk_master where SuspendedYN='0' ";
                    Statement stmtTotal = conn.createStatement();
                    stmtTotal.execute(qTotal);
                    ResultSet rsTotal = stmtTotal.getResultSet();
                    while (rsTotal.next()) {
                        totalrows = rsTotal.getInt(1);
                    }
                    T_ypeSync = "Sync In Item : ";
                    String sql = "select ItemCode,Description,ItemGroup,FORMAT(UnitPrice,2) as UnitPrice," +
                            " FORMAT(UnitCost,2) as UnitCost,DefaultUOM,UOM,UOM1," +
                            " UOM2,UOM3,UOM4,UOMPrice1," +
                            " UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1," +
                            " UOMFactor2,UOMFactor3,UOMFactor4,AnalysisCode1," +
                            " AnalysisCode2,AnalysisCode3,AnalysisCode4,AnalysisCode5," +
                            " MAXSP,MAXSP1,MAXSP2,MAXSP3," +
                            " MAXSP4,SalesTaxCode,RetailTaxCode,PurchaseTaxCode," +
                            " FixedPriceYN, SuspendedYN, AlternateItem," +
                            " BaseCode, UOMCode1, UOMCode2, UOMCode3," +
                            " UOMCode4 " +
                            " from stk_master where SuspendedYN='0' Order By ItemCode ";
                    Statement stmt = conn.createStatement();
                    stmt.executeQuery("SET NAMES 'LATIN1'");
                    stmt.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmt.execute(sql);
                    ResultSet rsItem = stmt.getResultSet();
                    int i = 1;
                    while (rsItem.next()) {
                        String insert = "Insert into stk_master(ItemCode,Description,ItemGroup," +
                                "UnitPrice,UnitCost,DefaultUOM," +
                                "UOM,UOM1,UOM2," +
                                "UOM3,UOM4,UOMPrice1," +
                                "UOMPrice2,UOMPrice3,UOMFactor4," +
                                "UOMFactor1,UOMFactor2,UOMFactor3," +
                                "UOMFactor4,AnalysisCode1,AnalysisCode2," +
                                "AnalysisCode3,AnalysisCode4,AnalysisCode5," +
                                "MAXSP,MAXSP1,MAXSP2," +
                                "MAXSP3,MAXSP4,SalesTaxCode," +
                                "RetailTaxCode,PurchaseTaxCode,FixedPriceYN," +
                                "SuspendedYN,AlternateItem,BaseCode," +
                                "UOMCode1, UOMCode2, UOMCode3," +
                                "UOMCode4)values(" +
                                "'" + rsItem.getString(1) + "', '" + charReplace(Encode.setChar(CharType, rsItem.getString(2))) + "', '" + charReplace(Encode.setChar(CharType, rsItem.getString(3))) + "'," +
                                "'" + rsItem.getString(4) + "', '" + rsItem.getString(5) + "', '" + rsItem.getString(6) + "'," +
                                "'" + charReplace(Encode.setChar(CharType, rsItem.getString(7))) + "', '" + charReplace(Encode.setChar(CharType, rsItem.getString(8))) + "', '" + charReplace(Encode.setChar(CharType, rsItem.getString(9))) + "'," +
                                "'" + charReplace(Encode.setChar(CharType, rsItem.getString(10))) + "', '" + charReplace(Encode.setChar(CharType, rsItem.getString(11))) + "', '" + rsItem.getString(12) + "'," +
                                "'" + rsItem.getString(13) + "', '" + rsItem.getString(14) + "', '" + rsItem.getString(15) + "'," +
                                "'" + rsItem.getString(16) + "', '" + rsItem.getString(17) + "', '" + rsItem.getString(18) + "'," +
                                "'" + rsItem.getString(19) + "', '" + charReplace(rsItem.getString(20)) + "', '" + charReplace(rsItem.getString(21)) + "', " +
                                "'" + charReplace(rsItem.getString(22)) + "', '" + charReplace(rsItem.getString(23)) + "', '" + rsItem.getString(24) + "'," +
                                "'" + rsItem.getString(25) + "', '" + rsItem.getString(26) + "', '" + rsItem.getString(27) + "'," +
                                "'" + rsItem.getString(28) + "', '" + rsItem.getString(29) + "', '" + rsItem.getString(30) + "'," +
                                "'" + rsItem.getString(31) + "', '" + rsItem.getString(32) + "', '" + rsItem.getString(33) + "'," +
                                "'" + rsItem.getString(34) + "', '" + rsItem.getString(35) + "', '" + rsItem.getString(36) + "'," +
                                "'" + rsItem.getString(37) + "', '" + rsItem.getString(38) + "', '" + rsItem.getString(39) + "'," +
                                "'" + rsItem.getString(40) + "'  )";
                       // Log.d("INSERT ITEM", insert);
                        db.exeQuery(insert);
                        publishProgress(i);
                        i++;
                    }
                    //location
                    String qDelLoc = "delete from stk_itemlocation";
                    db.exeQuery(qDelLoc);

                    String qTotLoc = "select count(*)as totalrows from stk_itemlocation  ";
                    Statement stmtTotLoc = conn.createStatement();
                    stmtTotLoc.execute(qTotLoc);
                    ResultSet rsTotLoc = stmtTotLoc.getResultSet();
                    while (rsTotLoc.next()) {
                        totalrows = rsTotLoc.getInt(1);
                    }
                    T_ypeSync = "Sync In Location : ";

                    String sqlLoc = "select LocationCode,LocationName,BranchCode," +
                            "DepartmentCode,ProjectCode,S_ection," +
                            "C_olumn,R_ow,DetailYN," +
                            "Link from stk_itemlocation order by LocationCode";
                    Statement stmtLoc = conn.createStatement();
                    stmtLoc.executeQuery("SET NAMES 'LATIN1'");
                    stmtLoc.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmtLoc.execute(sqlLoc);
                    ResultSet rsLoc = stmtLoc.getResultSet();
                    i=1;
                    while (rsLoc.next()) {
                        String insertLoc = "insert into stk_itemlocation(LocationCode,LocationName,BranchCode," +
                                "DepartmentCode,ProjectCode,S_ection," +
                                "C_olumn,R_ow,DetailYN," +
                                "Link)values(" +
                                "'" + rsLoc.getString(1) + "', '" + charReplace(rsLoc.getString(2)) + "', '" + rsLoc.getString(3) + "'," +
                                "'" + rsLoc.getString(4) + "', '" + rsLoc.getString(5) + "', '" + rsLoc.getString(6) + "'," +
                                "'" + rsLoc.getString(7) + "', '" + rsLoc.getString(8) + "', '" + rsLoc.getString(9) + "'," +
                                "'" + rsLoc.getString(10) + "')";
                        db.exeQuery(insertLoc);
                        publishProgress(i);
                        i++;
                    }
                    //end  location

                    //stk_group
                    String qDelGroup = "delete from stk_group";
                    db.exeQuery(qDelGroup);

                    String qTotGroup = "select count(*)as totalrows from stk_group  ";
                    Statement stmtTotGroup = conn.createStatement();
                    stmtTotGroup.execute(qTotGroup);
                    ResultSet rsTotGroup = stmtTotGroup.getResultSet();
                    while (rsTotGroup.next()) {
                        totalrows = rsTotGroup.getInt(1);
                    }
                    T_ypeSync = "Sync In Group : ";

                    String sqlGroup = "select ItemGroup, Description," +
                            " Modifier1, Modifier2, L_ink, " +
                            " DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified," +
                            " Printer from stk_group Order By ItemGroup";
                    Statement stmtGroup = conn.createStatement();
                    stmtGroup.executeQuery("SET NAMES 'LATIN1'");
                    stmtGroup.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmtGroup.execute(sqlGroup);
                    ResultSet rsGroup = stmtGroup.getResultSet();
                    i=1;
                    while (rsGroup.next()) {
                        String insertGroup = "Insert into stk_group(ItemGroup, Description, " +
                                "Modifier1, Modifier2, " +
                                " L_ink, DateTimeModified, " +
                                " Printer)values(" +
                                " '" + charReplace(Encode.setChar(CharType, rsGroup.getString("ItemGroup"))) + "', '" + charReplace(Encode.setChar(CharType, rsGroup.getString("Description"))) + "',  " +
                                " '" + charReplace(Encode.setChar(CharType, rsGroup.getString("Modifier1"))) + "', '" + rsGroup.getString("Modifier2") + "', '" + rsGroup.getString("L_ink") + "', " +
                                " '" + rsGroup.getString("DateTimeModified") + "', '" + rsGroup.getString("Printer") + "' )";
                        //Log.d("GROUP", insertGroup);
                        db.exeQuery(insertGroup);
                        publishProgress(i);
                        i++;
                    }

                    //start customer
                    String vDelCus = "delete from customer";
                    db.exeQuery(vDelCus);

                    String qTotCus = "select count(*)as totalrows from customer  where StatusBadYN='0' ";
                    Statement stmtTotCus = conn.createStatement();
                    stmtTotCus.execute(qTotCus);
                    ResultSet rsTotCus = stmtTotCus.getResultSet();
                    while (rsTotCus.next()) {
                        totalrows = rsTotCus.getInt(1);
                    }
                    T_ypeSync = "Sync In Customer : ";

                    String sqlCus = "select CusCode,CusName,FinCatCode,AccountCode,Address,CurCode, " +
                            " TermCode,D_ay,SalesPersonCode,Tel,Tel2,Fax,Fax2,Contact,ContactTel,Email,StatusBadYN, " +
                            " Town,State,Country,PostCode,L_ink,NRICNo,DATE_FORMAT(DOB,'%Y-%m-%d') as DOB, Sex," +
                            " MemberType,CardNo,PaymentCode,DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified " +
                            " from customer where StatusBadYN='0' order by CusCode ";
                    Statement stmtCus = conn.createStatement();
                    stmtCus.execute(sqlCus);
                    ResultSet rsCus = stmtCus.getResultSet();
                    i=1;
                    while (rsCus.next()) {
                        String QueryIn = "Insert into customer(CusCode, CusName, FinCatCode, " +
                                " AccountCode, Address,CurCode, " +
                                " TermCode, D_ay, SalesPersonCode, " +
                                " Tel, Tel2, Fax, " +
                                " Fax2, Contact, ContactTel, " +
                                " Email, StatusBadYN, Town," +
                                " State, Country, PostCode," +
                                " L_ink, NRICNo, DOB, " +
                                " Sex, MemberType, CardNo," +
                                " PaymentCode, DateTimeModified)values(" +
                                " '" + rsCus.getString("CusCode") + "', '" + charReplace(rsCus.getString("CusName")) + "', '" + rsCus.getString("FinCatCode") + "', " +
                                " '" + rsCus.getString("AccountCode") + "', '" + charReplace(rsCus.getString("Address")) + "', '" + rsCus.getString("CurCode") + "', " +
                                " '" + rsCus.getString("TermCode") + "', '" + rsCus.getString("D_ay") + "', '" + rsCus.getString("SalesPersonCode") + "'," +
                                " '" + rsCus.getString("Tel") + "', '" + rsCus.getString("Tel2") + "',  '" + rsCus.getString("Fax") + "', " +
                                " '" + rsCus.getString("Fax2") + "', '" + charReplace(rsCus.getString("Contact")) + "', '" + charReplace(rsCus.getString("ContactTel")) + "', " +
                                " '" + rsCus.getString("Email") + "', '" + rsCus.getString("StatusBadYN") + "', '" + rsCus.getString("Town") + "',  " +
                                " '" + rsCus.getString("State") + "', '" + rsCus.getString("Country") + "', '" + rsCus.getString("PostCode") + "',  " +
                                " '" + rsCus.getString("L_ink") + "', '" + rsCus.getString("NRICNo") + "', '" + rsCus.getString("DOB") + "',  " +
                                " '" + rsCus.getString("Sex") + "', '" + rsCus.getString("MemberType") + "', '" + rsCus.getString("CardNo") + "',  " +
                                " '" + rsCus.getString("PaymentCode") + "', '" + rsCus.getString("DateTimeModified") + "')";
                        db.exeQuery(QueryIn);
                        publishProgress(i);
                        i++;
                    }
                    //end customer

                    //stk tax
                    String vDelTax = "delete from stk_tax";
                    db.exeQuery(vDelTax);

                    String qTotTax = "select count(*)as totalrows from stk_tax  ";
                    Statement stmtTotTax = conn.createStatement();
                    stmtTotTax.execute(qTotTax);
                    ResultSet rsTotTax = stmtTotTax.getResultSet();
                    while (rsTotTax.next()) {
                        totalrows = rsTotTax.getInt(1);
                    }
                    T_ypeSync = "Sync In Tax : ";

                    String sqlTax = "select TaxCode, Description, R_ate," +
                            " TaxType, GSTTaxType, " +
                            " GSTTaxCode " +
                            " from stk_tax ";
                    Statement stmtTax = conn.createStatement();
                    stmtTax.execute(sqlTax);
                    ResultSet rsTax = stmtTax.getResultSet();
                    i=1;
                    while (rsTax.next()) {
                        String insertTax = "Insert into stk_tax(TaxCode,Description," +
                                "R_ate,TaxType," +
                                "GSTTaxType)values(" +
                                "'" + rsTax.getString(1) + "', '" + rsTax.getString(2) + "', " +
                                "'" + rsTax.getString(3) + "', '" + rsTax.getString(4) + "'," +
                                "'" + rsTax.getString(5) + "') ";
                        db.exeQuery(insertTax);
                        publishProgress(i);
                        i++;
                    }
                    //end stk_tax

                    //general setup2
                    String vDelGen2 = "delete from sys_general_setup2";
                    db.exeQuery(vDelGen2);
                    String sqlGen2 = "SELECT IFNULL(PostGlobalTaxYN,'')as PostGlobalTaxYN FROM sys_general_setup2";
                    Statement stmtGen2 = conn.createStatement();
                    stmtGen2.execute(sqlGen2);
                    ResultSet rsGen2 = stmtGen2.getResultSet();
                    while (rsGen2.next()) {
                        String vPostGlobalTaxYN = rsGen2.getString("PostGlobalTaxYN");
                        String insert = "insert into sys_general_setup2(PostGlobalTaxYN,Remark)values('" + vPostGlobalTaxYN + "','')";
                        db.exeQuery(insert);
                    }
                    //end gen setup2

                    //company setup
                    String vDelCom = "delete from companysetup";
                    db.exeQuery(vDelCom);
                    String sqlCom = "select CurCode,GSTNo,CompanyName from companysetup ";
                    Statement stmtCom = conn.createStatement();
                    stmtCom.execute(sqlCom);
                    ResultSet rsCom = stmtCom.getResultSet();
                    while (rsCom.next()) {
                        String insert = "Insert into companysetup(CurCode,GSTNo,CompanyName)values(" +
                                "'" + rsCom.getString(1) + "', '" + rsCom.getString(2) + "', '" + rsCom.getString(3) + "')";
                        db.exeQuery(insert);
                    }
                    //end company setup
                    z = "success";
                } else {
                    z = "error";
                }
            }
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }
}
