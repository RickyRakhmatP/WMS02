package com.skybiz.wms02.ui_PrintInvLabel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Print.By_Wifi.PrintingWifiTsc;
import com.skybiz.wms02.m_Supplier.DialogSupplier;
import com.skybiz.wms02.ui_PrintInvLabel.m_ListUser.UserDownload;
import com.skybiz.wms02.ui_PrintInvLabel.m_ManualPrint.DialogManual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintInvLabel extends AppCompatActivity {

    RecyclerView rvUser;
    private GridLayoutManager lLayout;
    TextView txtProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_inv_label);
        getSupportActionBar().setTitle("Print Invoice Label");
        rvUser      =(RecyclerView)findViewById(R.id.rvUser);
        txtProgress =(TextView) findViewById(R.id.txtProgress);
        loadlist();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invlabel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mnManual) {
            DialogManual dialogManual = new DialogManual();
            dialogManual.show(getSupportFragmentManager(), "mManual");
            return true;
        }else if (id == R.id.mnSyncIn) {
            fnsyncin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fnsyncin(){
        SyncIN syncIN=new SyncIN(this);
        syncIN.execute();
    }
    private void loadlist(){
        rvUser.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 2);
        rvUser.setLayoutManager(lLayout);
        rvUser.setItemAnimator(new DefaultItemAnimator());
        UserDownload userDownload=new UserDownload(this,rvUser);
        userDownload.execute();
    }
    public void setInvoice(String UserCode){
        SetInvLabel setInvLabel=new SetInvLabel(this,UserCode);
        setInvLabel.execute();
    }
    public void setInvoice2(String UserCode, String DateFrom, String DateTo, String Doc1NoFrom, String Doc1NoTo){
        SetInvLabel2 setInvLabel=new SetInvLabel2(this,UserCode, DateFrom, DateTo, Doc1NoFrom, Doc1NoTo);
        setInvLabel.execute();
    }
    private void setPrintLabel(String jsonData, String T_ype){
        try {
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String qPrinter     = "select IFNULL(IPAddress,'')as IPAddress, " +
                                " Port from tb_settingprinter";
            Cursor rsPrinter    = db.getQuery(qPrinter);
            String Port         = "";
            String IPAddress    = "";
            while (rsPrinter.moveToNext()) {
                IPAddress   = rsPrinter.getString(0);
                Port        = rsPrinter.getString(1);
            }
            if(!IPAddress.isEmpty()) {
                 JSONArray ja    = new JSONArray(jsonData);
                 JSONObject jo   = null;
                 Handler handler1 = new Handler();
                for (int i = 0; i < ja.length(); i++) {
                    jo              = ja.getJSONObject(i);
                    final String UserCode = jo.getString("UserCode");
                    final String Doc1No   = jo.getString("Doc1No");
                    final String vIPAddress = IPAddress;
                    final String vPort       = Port;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PrintingWifiTsc print = new PrintingWifiTsc();
                            Boolean isBT = print.printInvLabel(PrintInvLabel.this, vIPAddress, vPort, Doc1No,UserCode);
                            if (isBT == false) {
                                Log.d("Printing", "Error Printing Inv Label");
                            }
                        }
                    },1500 * i);
                }

                if(T_ype.equals("Auto")) {
                    UpdateStatus updateStatus = new UpdateStatus(this, jsonData);
                    updateStatus.execute();
                }


               /* PrintingWifiTsc print = new PrintingWifiTsc();
                Boolean isBT = print.printInvLabel(this, IPAddress, Port, UserCode);
                if (isBT == false) {
                    Toast.makeText(this,"Error Printing", Toast.LENGTH_SHORT).show();
                } else {
                    if(T_ype.equals("Auto")) {
                        UpdateStatus updateStatus = new UpdateStatus(this, jsonData);
                        updateStatus.execute();
                    }
                }*/
            }else{
                Toast.makeText(this,"Check your setting printer", Toast.LENGTH_SHORT).show();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public class UpdateStatus extends AsyncTask<Void,Void,String>{

        Context c;
        String Data;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public UpdateStatus(Context c, String data) {
            this.c = c;
            Data = data;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnupdate();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Error Update Status", Toast.LENGTH_SHORT).show();
            }else if(result.equals("success")){
                Toast.makeText(c,"Print Inv Label Succesfull", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnupdate(){
            Cursor curSet   =null;
            DBAdapter db    =null;
            try{
                z="error";
                db = new DBAdapter(c);
                db.openDB();
                String querySet = "select * from tb_setting";
                curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                }
                //curSet.close();
                JSONArray ja    = new JSONArray(Data);
                JSONObject jo   = null;
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        for (int i = 0; i < ja.length(); i++) {
                            jo = ja.getJSONObject(i);
                            String UserCode = jo.getString("UserCode");
                            String Doc1No   = jo.getString("Doc1No");
                            String sql="update stk_cus_inv_hd set Status='P.Label' where Doc1No='"+Doc1No+"'  ";
                            Statement statement = conn.createStatement();
                            statement.execute(sql);
                        }
                        z="success";
                    }else{
                        z="error";
                    }
                }else if(DBStatus.equals("0")){
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        String UserCode = jo.getString("UserCode");
                        String Doc1No   = jo.getString("Doc1No");
                        String sql="update stk_cus_inv_hd set Status='P.Label' where Doc1No='"+Doc1No+"'  ";
                        db.exeQuery(sql);
                    }
                    z="success";
                }
                //db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                curSet.close();
                db.closeDB();
            }
            return z;
        }
    }
    public class SetInvLabel extends AsyncTask<Void,Void,String>{

        Context c;
        String UserCode;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public SetInvLabel(Context c, String userCode) {
            this.c = c;
            UserCode = userCode;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnretinv();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Toast.makeText(c,"invoice not found from user", Toast.LENGTH_SHORT).show();
            }else{
                setPrintLabel(result,"Auto");
            }
        }
        private  String fnretinv(){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ate = sdf.format(date);
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet = "select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                }

               /* String sql="select H.Doc1No from stk_cus_inv_hd H" +
                        " inner join stk_receipt2 R on H.Doc1No=R.Doc1No" +
                        " where H.Status='Waiting' and H.Status<>'P.Label' and R.UserCode='"+UserCode+"' ";*/

                String sql="SELECT H.Doc1No, S.UserCode " +
                        " FROM stk_cus_inv_hd H " +
                        " INNER JOIN sys_audit_log S ON H.Doc1No = S.Doc1No" +
                        " WHERE S.UserCode = '"+UserCode+"'" +
                        " and H.Status='Waiting' " +
                        " and H.Status<>'P.Label' "+
                        " and H.D_ate>='"+D_ate+"' " +
                        " GROUP BY H.Doc1No";
                Log.d("QUERY AUTO",sql);
                JSONArray results  =new JSONArray();
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsData = statement.getResultSet();
                        while (rsData.next()) {
                            JSONObject row=new JSONObject();
                            row.put("Doc1No",rsData.getString(1));
                            row.put("UserCode",UserCode);
                            results.put(row);
                        }
                        db.closeDB();
                        return results.toString();
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsData=db.getQuery(sql);
                    while (rsData.moveToNext()) {
                        JSONObject row=new JSONObject();
                        row.put("Doc1No",rsData.getString(0));
                        row.put("UserCode",UserCode);
                        results.put(row);
                    }
                    db.closeDB();
                    return results.toString();
                }
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

    }


    public class SetInvLabel2 extends AsyncTask<Void,Void,String>{

        Context c;
        String UserCode,DateFrom,DateTo,Doc1NoFrom,Doc1NoTo;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public SetInvLabel2(Context c, String userCode, String DateFrom,
                            String DateTo, String Doc1NoFrom, String Doc1NoTo) {
            this.c          = c;
            UserCode        = userCode;
            this.DateFrom   =DateFrom;
            this.DateTo     =DateTo;
            this.Doc1NoFrom =Doc1NoFrom;
            this.Doc1NoTo   =Doc1NoTo;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnretinv();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Toast.makeText(c,"invoice not found from user", Toast.LENGTH_SHORT).show();
            }else{
                setPrintLabel(result,"Manual");
            }
        }
        private  String fnretinv(){
            try {
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet = "select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                }

               /* String sql="select H.Doc1No from stk_cus_inv_hd H" +
                        " inner join stk_receipt2 R on H.Doc1No=R.Doc1No" +
                        " where R.UserCode='"+UserCode+"' "+
                        " and (H.D_ate between '"+DateFrom+"' and '"+DateTo+"') " +
                        " and (H.Doc1No='"+UserCode+"' between '"+Doc1NoFrom+"'  and '"+Doc1NoTo+"') ";*/
                String sql=" SELECT H.Doc1No, S.UserCode " +
                        " FROM stk_cus_inv_hd H " +
                        " INNER JOIN sys_audit_log S ON H.Doc1No = S.Doc1No" +
                        " WHERE S.UserCode = '"+UserCode+"'" +
                        " and  (H.D_ate between '"+DateFrom+"' and '"+DateTo+"') " +
                        " and  (H.Doc1No between '"+Doc1NoFrom+"'  and '"+Doc1NoTo+"') "+
                        " GROUP BY H.Doc1No";
                Log.d("QUERY",sql);
                JSONArray results  =new JSONArray();
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsData = statement.getResultSet();
                        while (rsData.next()) {
                            JSONObject row=new JSONObject();
                            row.put("Doc1No",rsData.getString(1));
                            row.put("UserCode",UserCode);
                            results.put(row);
                        }
                        db.closeDB();
                        return results.toString();
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsData=db.getQuery(sql);
                    while (rsData.moveToNext()) {
                        JSONObject row=new JSONObject();
                        row.put("Doc1No",rsData.getString(0));
                        row.put("UserCode",UserCode);
                        results.put(row);
                    }
                    db.closeDB();
                    return results.toString();
                }
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

    }
    private void setProgress(String vValue){
        txtProgress.setText(vValue);
    }

    private class SyncIN extends AsyncTask<Void,Integer,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        String T_ypeSync="";
        int totalrows=0;

        public SyncIN(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnsyncin();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            setProgress(T_ypeSync+" "+values[0] +" of "+ totalrows);

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"Successful Sync IN", Toast.LENGTH_SHORT).show();
            }else if(result.equals("error")){
                Toast.makeText(c,"Error Sync IN", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnsyncin(){
            try{
                z="error";
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

                    int i = 1;
                    String qDelUser = "delete from sys_userprofile_hd";
                    db.exeQuery(qDelUser);

                    String qTotUser = "select count(*)as totalrows from sys_userprofile_hd  ";
                    Statement stmtTotU = conn.createStatement();
                    stmtTotU.execute(qTotUser);
                    ResultSet rsTotU = stmtTotU.getResultSet();
                    while (rsTotU.next()) {
                        totalrows = rsTotU.getInt(1);
                    }
                    T_ypeSync = "Sync In Sys User Profile : ";
                    String qUser="select UserCode, " +
                            " UserName, Password, DepartmentCode," +
                            " AddNewYN, ViewYN, RePrintYN," +
                            " OWCreditLimitYN, FOCItemYN, OWNegativeStkYN, " +
                            " ItemListingBalQtyYN, ItemListingCostYN, ItemListingPriceYN," +
                            " EditAmountYN, OWInvoiceLimitYN, OWOverdueOutstandingYN," +
                            " VoidPOYN, VoidSOYN, VoidCusCNYN," +
                            " OWMSPYN, EncryptionYN, MinItemLevelYN," +
                            " MaxItemLevelYN, PrintDocumentYN, PrintDocumentTimes," +
                            " PrintReportYN, OWMPPYN, Password2, " +
                            " MRUserLevel, 'Dis', Status from sys_userprofile_hd";

                    Statement stmtUser = conn.createStatement();
                    stmtUser.execute(qUser);
                    ResultSet rsUser=stmtUser.getResultSet();
                    while(rsUser.next()){
                        String insertU="insert into sys_userprofile_hd(UserCode," +
                                " UserName, Password, DepartmentCode, " +
                                " AddNewYN, ViewYN, RePrintYN," +
                                " OWCreditLimitYN, FOCItemYN, OWNegativeStkYN, " +
                                " ItemListingBalQtyYN, ItemListingCostYN, ItemListingPriceYN," +
                                " EditAmountYN, OWInvoiceLimitYN, OWOverdueOutstandingYN," +
                                " VoidPOYN, VoidSOYN, VoidCusCNYN, " +
                                " OWMSPYN, EncryptionYN, MinItemLevelYN," +
                                " MaxItemLevelYN, PrintDocumentYN, PrintDocumentTimes," +
                                " PrintReportYN, OWMPPYN, Password2, " +
                                " MRUserLevel, programname, Status)values('"+rsUser.getString(1)+"'," +
                                " '"+rsUser.getString(2)+"', '"+charReplace(Encode.setChar(CharType,rsUser.getString(3)))+"', '"+rsUser.getString(4)+"'," +
                                " '"+rsUser.getString(5)+"', '"+rsUser.getString(6)+"', '"+rsUser.getString(7)+"'," +
                                " '"+rsUser.getString(8)+"', '"+rsUser.getString(9)+"', '"+rsUser.getString(10)+"'," +
                                " '"+rsUser.getString(11)+"', '"+rsUser.getString(12)+"', '"+rsUser.getString(13)+"'," +
                                " '"+rsUser.getString(14)+"', '"+rsUser.getString(15)+"', '"+rsUser.getString(16)+"'," +
                                " '"+rsUser.getString(17)+"', '"+rsUser.getString(18)+"', '"+rsUser.getString(19)+"'," +
                                " '"+rsUser.getString(20)+"', '"+rsUser.getString(21)+"', '"+rsUser.getString(22)+"'," +
                                " '"+rsUser.getString(23)+"', '"+rsUser.getString(24)+"', '"+rsUser.getString(25)+"'," +
                                " '"+rsUser.getString(26)+"', '"+rsUser.getString(27)+"', '"+rsUser.getString(28)+"'," +
                                " '"+rsUser.getString(29)+"', '"+rsUser.getString(30)+"', '"+rsUser.getString(31)+"' )";
                        db.exeQuery(insertU);
                        publishProgress(i);
                        i++;
                    }

                    z="success";
                }else{
                    z="error";
                }
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']", "");
        return newText;
    }
}
