package com.skybiz.wms02.ui_GRNReceiving;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Location.DialogLoc;
import com.skybiz.wms02.m_StockTake.DialogQty;
import com.skybiz.wms02.m_Supplier.DialogSupplier;
import com.skybiz.wms02.ui_GRNReceiving.m_ListDum.DownloaderList;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDriver.DialogDriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GRN_Receiving extends AppCompatActivity {

    Button btnLocation,btnSupplier,btnDoc1NoPO;
    EditText txtLocation,txtSupplier,txtScanItem;
    Spinner spDoc1NoPO;
    RecyclerView rvList;
    private GridLayoutManager lLayout;
    Button btnBack,btnSave;
    CheckBox chkVerify;
    String vDoc1No="";
    TextView txtProgress;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grn__receiving);
        btnLocation=(Button)findViewById(R.id.btnLocation);
        btnSupplier=(Button)findViewById(R.id.btnSupplier);
        btnDoc1NoPO=(Button)findViewById(R.id.btnDoc1NoPO);
        btnBack=(Button)findViewById(R.id.btnBack);
        btnSave=(Button)findViewById(R.id.btnSave);
        chkVerify=(CheckBox) findViewById(R.id.chkVerify);
        txtLocation=(EditText)findViewById(R.id.txtLocation);
        txtSupplier=(EditText)findViewById(R.id.txtSupplier);
        txtScanItem=(EditText)findViewById(R.id.txtScanItem);
        spDoc1NoPO=(Spinner) findViewById(R.id.spDoc1NoPO);
        rvList=(RecyclerView)findViewById(R.id.rvList);
        txtProgress=(TextView)findViewById(R.id.txtProgress);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
            }
        });
        btnSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSupplier();
            }
        });
        btnDoc1NoPO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPO();
            }
        });

        txtScanItem.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String CusCode=txtScanItem.getText().toString();
                    if(!CusCode.isEmpty()){
                       addScan();
                    }else{
                        txtSupplier.requestFocus();
                    }
                }
                return false;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnsave();
            }
        });
        /*btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnverify();
            }
        });*/
        chkVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fnverify();
                    txtScanItem.setEnabled(false);
                }else{
                    disableSave();
                    txtScanItem.setEnabled(true);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBack();
                //Intent mainIntent = new Intent(GRN_Receiving.this,MainActivity.class);
                //startActivity(mainIntent);
            }
        });
        initData();
        resetDum();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.mnSyncIn){
            SyncIn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initData(){
        fnRetLastNo();
    }
    private void afterSave(){
        txtLocation.getText().clear();
        txtSupplier.getText().clear();
        txtScanItem.getText().clear();
        txtLocation.requestFocus();
        fnSaveLastNo();
        resetDum();
        txtScanItem.setEnabled(true);
        chkVerify.setChecked(false);
        disableSave();
        loadList();
        fnRetLastNo();

    }
    public void enableSave(){
        btnSave.setEnabled(true);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    public void disableSave(){
        btnSave.setEnabled(false);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        txtLocation.setEnabled(true);
        txtSupplier.setEnabled(true);
        spDoc1NoPO.setEnabled(true);
        btnDoc1NoPO.setEnabled(true);
        btnLocation.setEnabled(true);
        btnSupplier.setEnabled(true);

    }
    private void fnverify(){
        String LocationCode =txtLocation.getText().toString();
        String CusCode      =txtSupplier.getText().toString();
        String Doc1NoPO     =spDoc1NoPO.getSelectedItem().toString();
        txtLocation.setEnabled(false);
        txtSupplier.setEnabled(false);
        spDoc1NoPO.setEnabled(false);
        btnDoc1NoPO.setEnabled(false);
        btnLocation.setEnabled(false);
        btnSupplier.setEnabled(false);
        txtScanItem.setEnabled(false);
        VerifyGRN verifyGRN=new VerifyGRN(this,LocationCode,CusCode,Doc1NoPO);
        verifyGRN.execute();
    }
    public void fnSaveLastNo(){
        try {
            String queryLast = "Select LastNo,Prefix from sys_runno_dt where RunNoCode='GRNDO' ";
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            Cursor rsLast = db.getQuery(queryLast);
            Integer NewNo = 0;
            String Prefix = "";
            while (rsLast.moveToNext()) {
               String LastNo    = "1" + rsLast.getString(0);
                Prefix          = "1" + rsLast.getString(1);
                NewNo           = (Integer.parseInt(LastNo)) + 1;
            }
            String NewDoc   = String.valueOf(NewNo);
            String vNewDoc  = NewDoc.substring(1, NewDoc.length());
            vDoc1No = Prefix + vNewDoc;
            String update = "update sys_runno_dt set LastNo='" + vNewDoc + "' where RunNoCode='GRNDO' ";
            db.exeQuery(update);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnRetLastNo(){
        try {
            String Prefix = "";
            String LastNo = "";
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String vQuery = "select Prefix,LastNo from sys_runno_dt where RunNoCode='GRNDO' ";
            Cursor cRunNo = db.getQuery(vQuery);
            while (cRunNo.moveToNext()) {
                Prefix = cRunNo.getString(0);
                LastNo = cRunNo.getString(1);
            }
            vDoc1No = Prefix + LastNo;
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void fnsave(){
        disableSave();
        String CusCode      =txtSupplier.getText().toString();
        String Doc1NoPO     =spDoc1NoPO.getSelectedItem().toString();
        String LocationCode=txtLocation.getText().toString();
        String Doc1No       =vDoc1No+"/"+LocationCode;
        SaveGRN saveGRN=new SaveGRN(this,Doc1No,Doc1NoPO,CusCode);
        saveGRN.execute();
    }
    private void resetDum(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qDelete="delete from tb_dumgrn_receiving ";
            db.exeQuery(qDelete);
            db.closeDB();
            loadList();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void delRow(String RunNo){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qDelete="delete from tb_dumgrn_receiving where RunNo='"+RunNo+"' ";
            db.exeQuery(qDelete);
            db.closeDB();
            loadList();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void openQty(String RunNo, String ItemCode, String D_ateTime){
        Bundle b=new Bundle();
        b.putString("TYPE_KEY","Qty");
        b.putString("UFROM_KEY","GRNDO");
        b.putString("RUNNO_KEY",RunNo);
        b.putString("CODE_KEY",ItemCode);
        b.putString("DATETIME_KEY",D_ateTime);
        DialogQty dialogQty = new DialogQty();
        dialogQty.setArguments(b);
        dialogQty.show(getSupportFragmentManager(), "Dialog Qty");
    }
    public void editQty(String RunNo, String Qty){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String upDum="update tb_dumgrn_receiving set Qty='"+Qty+"' where RunNo='"+RunNo+"' ";
            db.exeQuery(upDum);
            db.closeDB();
            loadList();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    private void addScan(){
        String ItemCode=txtScanItem.getText().toString();
        SetItem setItem=new SetItem(this,ItemCode);
        setItem.execute();
    }
    private void loadList(){
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        DownloaderList dlist=new DownloaderList(this,rvList);
        dlist.execute();
    }
    private void showLocation(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","GRN2");
        b.putString("DBSTATUS_KEY","0");
        DialogLoc dialogLoc = new DialogLoc();
        dialogLoc.setArguments(b);
        dialogLoc.show(getSupportFragmentManager(), "mLocation");
    }
    private void showSupplier(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","GRN2");
        b.putString("DBSTATUS_KEY","0");
        DialogSupplier dialogCustomer = new DialogSupplier();
        dialogCustomer.setArguments(b);
        dialogCustomer.show(getSupportFragmentManager(), "List Supplier");
    }
    private void showListPO(){
        String LocationCode=txtLocation.getText().toString();
        String CusCode=txtSupplier.getText().toString();
        RetDoc1NoPO retDoc1NoPO=new RetDoc1NoPO(this,LocationCode,CusCode);
        retDoc1NoPO.execute();
    }
    public void setCustomerBar(String CusCode){
        txtSupplier.setText(CusCode);
        txtScanItem.requestFocus();
        showListPO();
    }
    public void setLocation(String LocationCode){
        txtLocation.setText(LocationCode);
        txtSupplier.requestFocus();
    }

    public class RetDoc1NoPO extends AsyncTask<Void,Void,String>{
        Context c;
        String LocationCode, CusCode;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        Connection conn=null;
        ArrayList vDoc1NoPO=new ArrayList<String>();

        public RetDoc1NoPO(Context c, String LocationCode, String CusCode) {
            this.c = c;
            this.CusCode=CusCode;
            this.LocationCode=LocationCode;
        }


        @Override
        protected String doInBackground(Void... voids) {

            return this.fnretdoc1no();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")){
                spDoc1NoPO.setAdapter(new ArrayAdapter<String>(c,
                        android.R.layout.simple_spinner_dropdown_item,vDoc1NoPO));
            }else{
                Toast.makeText(c,"Doc1No PO is empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String fnretdoc1no(){
            try{
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

                String MacAddress   = Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);
                String vClause="";
                if(!LocationCode.isEmpty()){
                    vClause=" and D.LocationCode='"+LocationCode+"' ";
                }
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {

                        String vDelete = "DELETE FROM cloud_dt WHERE ComputerName = '"+MacAddress+"'";

                        Statement stmtDel = conn.createStatement();
                        stmtDel.execute(vDelete);

                        String vStrSQL1 = "INSERT INTO cloud_dt (Doc1No, DocType, RunNo," +
                                " Qty, ComputerName)" +
                                " SELECT H.Doc1No, 'PO', D.RunNo, " +
                                " D.Qty, '"+MacAddress+"' " +
                                " From stk_pur_order_dt D INNER " +
                                " JOIN stk_pur_order_hd H ON D.Doc1No = H.Doc1No " +
                                " WHERE H.ApprovedYN = '1' " +
                                " AND D.VoidQty = '0'  AND H.CusCode = '"+CusCode+"' " +
                                " AND H.L_ink = '1'  "+vClause+" ";

                        Statement stmtDum1 = conn.createStatement();
                        stmtDum1.execute(vStrSQL1);

                        //Insert from grndo
                        String vStrSQL2 = "INSERT INTO cloud_dt (Doc1No, DocType, RunNo, Qty, ComputerName) " +
                                "SELECT  Dum.Doc1No, 'GRNDO', D.PORunNo, -D.Qty, '"+MacAddress+"' " +
                                "From stk_grndo_dt D INNER JOIN cloud_dt Dum ON D.PORunNo = Dum.RunNo " +
                                "where Dum.ComputerName='"+MacAddress+"' ";

                        Statement stmtDum2 = conn.createStatement();
                        stmtDum2.execute(vStrSQL2);

                        //$execute = mysqli_query($conn,$vStrSQL);

                        //Insert from supinv
                        String vStrSQL3 = "INSERT INTO cloud_dt (Doc1No, DocType, RunNo, Qty, ComputerName) " +
                                "SELECT Dum.Doc1No, 'SupInv', D.PORunNo, -D.Qty, '"+MacAddress+"' " +
                                "From stk_sup_inv_dt D INNER JOIN cloud_dt Dum ON D.PORunNo = Dum.RunNo" +
                                " WHERE Dum.DocType = 'PO' and Dum.ComputerName='"+MacAddress+"' ";
                        Statement stmtDum3 = conn.createStatement();
                        stmtDum3.execute(vStrSQL3);

                        String sql =" SELECT MAX(D.Doc1No) AS Doc1No, " +
                                " IFNULL(SUM(Dum.Qty),0) AS Qty, MAX(D.ItemCode) AS ItemCode, " +
                                " MAX(D.Description) AS Description " +
                                ", D.RunNo as RunNo1, M.AlternateItem, M.SupplierItemCode, M.BaseCode " +
                                " FROM cloud_dt Dum INNER JOIN stk_pur_order_dt D ON Dum.RunNo = D.RunNo " +
                                " INNER JOIN stk_pur_order_hd H ON D.Doc1No = H.Doc1No " +
                                " LEFT JOIN stk_master M ON D.ItemCode = M.ItemCode " +
                                " WHERE Dum.ComputerName = '" +MacAddress+"' AND D.BlankLine NOT IN ('3') " +
                                " GROUP BY D.Doc1No HAVING ROUND(IFNULL(SUM(Dum.Qty),0),6) > ROUND(0,6) ORDER BY H.Doc1No Desc, D.RunNo ";
                        Log.d("QUERY",sql);
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsPO = statement.getResultSet();
                        while (rsPO.next()) {
                            vDoc1NoPO.add(rsPO.getString(1));
                        }

                        //$execute = mysqli_query($conn,$vStrSQL);

                        /*String sql="SELECT D.Doc1No, D.RunNo, D.Qty," +
                                " D.ItemCode,D.Description From stk_pur_order_dt D " +
                                " INNER JOIN stk_pur_order_hd H ON D.Doc1No = H.Doc1No " +
                                " WHERE H.ApprovedYN = '1' AND D.VoidQty = 0 " +
                                " AND H.CusCode = '"+CusCode+"'  " +
                                " AND H.L_ink = '1'  "+vClause+" " ;
                        Log.d("QUERY",sql);
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsPO = statement.getResultSet();
                        while (rsPO.next()) {
                            Double dQty         =rsPO.getDouble(2);
                            Double dQtyGRN      =getQtyGRNDO(rsPO.getString(2));
                            Double dQtySupInv   =getQtySupInv(rsPO.getString(2));

                            Double Qty          =dQty-dQtyGRN-dQtySupInv;
                            Log.d("RUNNO", rsPO.getString(2));
                            if(Qty>0){
                                vDoc1NoPO.add(rsPO.getString(1));
                            }
                        }*/
                        z="success";
                    }
                }else{
                    z="error";
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

        private Double getQtyGRNDO(String PORunNo){
            Double dQty=0.00;
            try{
                String sqlGRN="SELECT Dt.Qty from stk_grndo_dt Dt " +
                        " INNER JOIN stk_pur_order_dt Dpo " +
                        " ON Dt.PORunNo=Dpo.RunNo" +
                        " where Dt.PORunNo='"+PORunNo+"' " ;
                Log.d("QUERY GRN",sqlGRN);
                Statement statement = conn.createStatement();
                statement.execute(sqlGRN);
                ResultSet rsGRN = statement.getResultSet();
                while (rsGRN.next()) {
                    dQty+= rsGRN.getDouble(1);
                }
                return dQty;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return dQty;
        }

        private Double getQtySupInv(String PORunNo){
            Double dQty=0.00;
            try{

                String sqlSupInv="SELECT Dt.Qty from stk_sup_inv_dt Dt " +
                        " INNER JOIN stk_pur_order_dt Dpo " +
                        " ON Dt.PORunNo=Dpo.RunNo" +
                        " where Dt.PORunNo='"+PORunNo+"' " ;
                Log.d("QUERY SupInv",sqlSupInv);
                Statement statement = conn.createStatement();
                statement.execute(sqlSupInv);
                ResultSet rsSup = statement.getResultSet();
                while (rsSup.next()) {
                    dQty+= rsSup.getDouble(1);
                }
                return dQty;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return dQty;
        }
    }

    public class SetItem extends AsyncTask<Void,Void, String>{
        Context c;
        String vItemCode;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        String Description="";

        public SetItem(Context c, String vItemCode) {
            this.c = c;
            this.vItemCode = vItemCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fnretitem();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("error")) {
                Toast.makeText(c, "Failure, No data retrieve", Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                if(Description.length()>0) {
                    loadList();
                    txtScanItem.getText().clear();
                    txtScanItem.requestFocus();
                    //Toast.makeText(c, "Item Not Found", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(c, "Item Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private String fnretitem() {
            try {
                z = "error";
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

                String sql = " Select ItemCode, Description, AlternateItem, SupplierItemCode, BaseCode From stk_master " +
                            " Where ItemCode = '"+vItemCode+"' or AlternateItem= '"+vItemCode+"' or SupplierItemCode= '"+vItemCode+"'  " +
                            " or BaseCode = '"+vItemCode+"' ";
                Log.d("RET ITEM",sql);
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        statement.execute(sql);
                        ResultSet rsItem = statement.getResultSet();
                        while (rsItem.next()) {
                            String ItemCode         = rsItem.getString(1);
                            Description             = charReplace(Encode.setChar(CharType,rsItem.getString(2)));
                            String AlternateItem    = rsItem.getString(3);
                            String SupplierItemCode = rsItem.getString(4);
                            String BaseCode         = rsItem.getString(5);
                            String qInsert ="insert into tb_dumgrn_receiving(ItemCode, Description, " +
                                    "AlternateItem, SupplierItemCode, " +
                                    "BaseCode, Qty," +
                                    "Remark, MaxQty)values('"+ItemCode+"', '"+Description+"'," +
                                    "'"+AlternateItem+"', '"+SupplierItemCode+"'," +
                                    "'"+BaseCode+"', '1'," +
                                    " '0','0')";
                            Log.d("INSERT DUM",qInsert);
                            db.exeQuery(qInsert);
                        }
                        z="success";
                    } else {
                        z="error";
                    }

                }else if(DBStatus.equals("0")){
                    Cursor rsItem=db.getQuery(sql);
                    while (rsItem.moveToNext()) {


                    }
                    z="success";
                }
                db.closeDB();
                return z;
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return z;
        }
    }

    public class SaveGRN extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,
                DBName,Port,URL,z,
                DBStatus,CharType,Doc1NoPo,
                CusCode,Doc1No;

        public SaveGRN(Context c, String Doc1No, String Doc1NoPO,
                       String CusCode) {
            this.c = c;
            this.Doc1No=Doc1No;
            this.Doc1NoPo=Doc1NoPO;
            this.CusCode=CusCode;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnsave();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("error")) {
                Toast.makeText(c, "Error Connection", Toast.LENGTH_SHORT).show();
                enableSave();
            } else if (result.equals("success")) {
                Toast.makeText(c, "Save Successful", Toast.LENGTH_SHORT).show();
                afterSave();
            } else if (result.equals("duplicate")) {
                Toast.makeText(c, "Duplicate Document #", Toast.LENGTH_SHORT).show();
                enableSave();
            }
        }

        private String fnsave(){
            try {
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
               /* String query="select ItemCode, Qty, PORunNo " +
                        "from dum_stk_pur_order_dt " +
                        "where Doc1No='"+Doc1NoPo+"' ";
                Cursor rsDum=db.getQuery(query);
                while(rsDum.moveToNext()){
                    String ItemCode=rsDum.getString(0);
                    String PORunNo=rsDum.getString(2);
                    Double dQty=rsDum.getDouble(1);
                    String qCheck="select sum(Qty)as DumQty from tb_dumgrn_receiving " +
                            " where ItemCode='"+ItemCode+"' Group By ItemCode ";
                    Cursor rsCheck=db.getQuery(qCheck);
                    Double dDumQty=0.00;
                    while(rsCheck.moveToNext()){
                        dDumQty=rsCheck.getDouble(0);
                    }
                    if(dDumQty<=dQty){
                        String vUpdate="update dum_stk_pur_order_dt " +
                                " set Remark='1' " +
                                " where ItemCode='"+ItemCode+"' and PORunNo='"+PORunNo+"'  ";
                        db.exeQuery(vUpdate);

                        String vUpdate2="update tb_dumgrn_receiving " +
                                " set Remark='1' " +
                                " where ItemCode='"+ItemCode+"'   ";
                        db.exeQuery(vUpdate2);

                    }else{

                    }

                }*/

                String MacAddress   = Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);
                MacAddress          = MacAddress.substring(0,4);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
               // String Doc1No=D_ateTime+MacAddress;
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        String qDuplicate="select count(*)as numrows from stk_grndo_hd where Doc1No= '"+Doc1No+"'";
                        Statement stmtDup = conn.createStatement();
                        stmtDup.execute(qDuplicate);
                        ResultSet rsDup = stmtDup.getResultSet();
                        int numrows=0;
                        while (rsDup.next()) {
                            numrows=rsDup.getInt(1);
                        }

                        if(numrows==0) {
                            String qAll = "select PORunNo, MaxQty, " +
                                    " ItemCode, Qty from tb_dumgrn_receiving " +
                                    " where Remark='1' Order By RunNo DESC ";
                            Cursor rsAll = db.getQuery(qAll);
                            int i = 1;
                            while (rsAll.moveToNext()) {
                                String RunNo = rsAll.getString(0);
                                Double dMaxQty = rsAll.getDouble(1);
                                Double dQty = rsAll.getDouble(3);

                                String query1 = "select Doc1No, N_o, ItemCode," +
                                        " Description, Qty, FactorQty," +
                                        " UOM, UOMSingular, HCUnitCost, " +
                                        " DisRate1, DisRate2, DisRate3," +
                                        " HCDiscount, TaxRate1, TaxRate2, " +
                                        " TaxRate3, HCTax, DetailTaxCode, " +
                                        " HCLineAmt, BranchCode, DepartmentCode, " +
                                        " ProjectCode, LocationCode, ItemBatch, " +
                                        " '' as WarantyDate, OCCode, OCRate, " +
                                        " OCAmt, '' as PONO, '0' as PORunNo, " +
                                        " '0' as SerialNoRunNo, LineNo, BlankLine, " +
                                        " D_isplay, SerialNumber, GradeDescription, " +
                                        " LineType " +
                                        " from stk_pur_order_dt WHERE RunNo = '" + RunNo + "' ";

                                Statement statement = conn.createStatement();
                                statement.executeQuery("SET NAMES 'LATIN1'");
                                statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                                statement.execute(query1);
                                ResultSet rsPO = statement.getResultSet();
                                while (rsPO.next()) {
                                    Double vQtyRate = dQty / dMaxQty;
                                    Double vHCDiscount = rsPO.getDouble(13) * vQtyRate;
                                    Double vHCTax = rsPO.getDouble(17) * vQtyRate;
                                    Double vHCLineAmt = rsPO.getDouble(19) * vQtyRate;
                                    Double vOCAmt = rsPO.getDouble(28) * vQtyRate;

                                    String StrSQL = "Insert Into stk_grndo_dt (Doc1No, N_o, ItemCode," +
                                            " Description, Qty, FactorQty, " +
                                            " UOM, UOMSingular, HCUnitCost, " +
                                            " DisRate1, DisRate2, DisRate3," +
                                            " HCDiscount, TaxRate1, TaxRate2, " +
                                            " TaxRate3, HCTax, DetailTaxCode, " +
                                            " HCLineAmt, BranchCode, DepartmentCode, " +
                                            " ProjectCode, LocationCode, ItemBatch, " +
                                            " WarrantyDate, OCCode, OCRate, " +
                                            " OCAmt, PONo, PORunNo, " +
                                            " SerialNoRunNo, LineNo, BlankLine, " +
                                            " D_isplay, SerialNumber, GradeDescription, " +
                                            " LineType) values ('" + Doc1No + "', '" + i + "', '" + rsPO.getString(3) + "', " +
                                            " '" + rsPO.getString(4) + "', '" + dQty + "', '" + rsPO.getString(6) + "', " +
                                            " '" + rsPO.getString(7) + "', '" + rsPO.getString(8) + "', '" + rsPO.getString(9) + "', " +
                                            " '" + rsPO.getString(10) + "', '" + rsPO.getString(11) + "', '" + rsPO.getString(12) + "', " +
                                            " '" + vHCDiscount + "', '" + rsPO.getString(14) + "', '" + rsPO.getString(15) + "', " +
                                            " '" + rsPO.getString(16) + "', '" + vHCTax + "', '" + rsPO.getString(18) + "', " +
                                            " '" + vHCLineAmt + "', '" + rsPO.getString(20) + "', '" + rsPO.getString(21) + "', " +
                                            " '" + rsPO.getString(22) + "', '" + rsPO.getString(23) + "', '" + rsPO.getString(24) + "', " +
                                            " '" + D_ate + "', '" + rsPO.getString(26) + "', '" + rsPO.getString(27) + "', " +
                                            " '" + vOCAmt + "', '" + Doc1NoPo + "', '" + RunNo + "', " +
                                            " '0', '" + i + "', '" + rsPO.getString(33) + "', " +
                                            " '" + rsPO.getString(34) + "', '" + rsPO.getString(35) + "', '" + rsPO.getString(36) + "', " +
                                            " '" + rsPO.getString(37) + "') ";
                                    Statement stmtIn = conn.createStatement();
                                    stmtIn.execute(StrSQL);
                                }
                                i++;
                            }

                            String qHeader = "Select Doc1No, Doc2No, Doc3No," +
                                    " D_ate, CusCode, CurCode, " +
                                    " CurRate1, CurRate2, CurRate3," +
                                    " Attention, GbDisRate1, GbDisRate2, " +
                                    " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                                    " GbTaxRate2, GbTaxRate3, HCGbTax, " +
                                    " GlobalTaxCode, HCNetAmt, GbOCCode,  " +
                                    " GbOCRate, GbOCAmt, GbOCCode2, " +
                                    " GbOCRate2, GbOCAmt2, UDRunNo, " +
                                    " Status, L_ink From stk_pur_order_hd WHERE Doc1No = '" + Doc1NoPo + "' ";
                            Statement stmtHeader = conn.createStatement();
                            stmtHeader.execute(qHeader);
                            ResultSet rsH = stmtHeader.getResultSet();
                            while (rsH.next()) {
                                String insertH = "INSERT INTO stk_grndo_hd (Doc1No, Doc2No, Doc3No," +
                                        " D_ate, CusCode, CurCode, " +
                                        " CurRate1, CurRate2, CurRate3," +
                                        " Attention, GbDisRate1, GbDisRate2, " +
                                        " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                                        " GbTaxRate2, GbTaxRate3, HCGbTax," +
                                        " GlobalTaxCode, HCNetAmt, GbOCCode, " +
                                        " GbOCRate, GbOCAmt, GbOCCode2, " +
                                        " GbOCRate2, GbOCAmt2, UDRunNo, " +
                                        " Status, L_ink)VALUES ('" + Doc1No + "', '" + Doc1NoPo + "', '',  " +
                                        " '" + D_ate + "', '" + CusCode + "' , '" + rsH.getString(6) + "', " +
                                        " '" + rsH.getString(7) + "', '" + rsH.getString(8) + "', '" + rsH.getString(9) + "',  " +
                                        " '" + rsH.getString(10) + "', '" + rsH.getString(11) + "', '" + rsH.getString(12) + "', " +
                                        " '" + rsH.getString(13) + "', '" + rsH.getString(14) + "', '" + rsH.getString(15) + "', " +
                                        " '" + rsH.getString(16) + "', '" + rsH.getString(17) + "', '" + rsH.getString(18) + "', " +
                                        " '" + rsH.getString(19) + "', '" + rsH.getString(20) + "', '" + rsH.getString(21) + "', " +
                                        " '" + rsH.getString(22) + "', '" + rsH.getString(23) + "', '" + rsH.getString(24) + "', " +
                                        " '" + rsH.getString(25) + "', '" + rsH.getString(26) + "', '0', " +
                                        "  'Waiting', '1')";
                                Statement stmtIn = conn.createStatement();
                                stmtIn.execute(insertH);
                            }

                            String qUpdate = " UPDATE stk_grndo_hd H INNER JOIN " +
                                    " (SELECT Doc1No, SUM(HCLineAmt) AS SumHCLineAmt, SUM(HCTax) AS SumHCTax FROM stk_grndo_dt " +
                                    " WHERE Doc1No = '" + Doc1No + "' GROUP BY Doc1No) D  " +
                                    " ON H.Doc1No = D.Doc1No  " +
                                    " Set H.HCNetAmt = D.SumHCLineAmt, H.HCDtTax = D.SumHCTax ";

                            Statement stmtUp = conn.createStatement();
                            stmtUp.execute(qUpdate);
                            z="success";
                        }else{
                            z="duplicate";
                        }
                    }else{
                        z="error";
                    }
                }
                db.closeDB();
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }

    }


    public class VerifyGRN extends AsyncTask<Void,Void,String>{
        Context c;
        String LocationCode,Doc1NoPO,CusCode;
        String IPAddress,UserName,Password,
                DBName,Port,URL,z,
                DBStatus,CharType;
        int numrows=0;

        Connection conn;

        public VerifyGRN(Context c, String LocationCode,
                         String CusCode, String doc1NoPO) {
            this.c = c;
            this.LocationCode=LocationCode;
            this.CusCode=CusCode;
            Doc1NoPO = doc1NoPO;

        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnverify();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")){
                if(numrows==0){
                   enableSave();
                    Toast.makeText(c,"Verify Suucess", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(c,"Verify Failure", Toast.LENGTH_SHORT).show();
                }
                loadList();
            }else{
                Toast.makeText(c, "Error Verify GRN", Toast.LENGTH_SHORT).show();
            }
        }

        private String fnverify(){
            try{

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

                String vClause="";
                if(!LocationCode.isEmpty()){
                    vClause=" and D.LocationCode='"+LocationCode+"' ";
                }

                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        String qCheck="select sum(Qty)as Qty, ItemCode from tb_dumgrn_receiving Group By ItemCode ";
                        Cursor rsCheck=db.getQuery(qCheck);
                        while(rsCheck.moveToNext()){
                           String ItemCode=rsCheck.getString(1);
                           String sql="SELECT D.Doc1No, D.RunNo, D.Qty," +
                                    " D.ItemCode,D.Description  From stk_pur_order_dt D " +
                                    " INNER JOIN stk_pur_order_hd H ON D.Doc1No = H.Doc1No " +
                                    " WHERE H.ApprovedYN = '1' AND D.VoidQty = 0 and D.ItemCode='"+ItemCode+"' " +
                                    " AND H.CusCode = '"+CusCode+"' and H.Doc1No='"+Doc1NoPO+"' " +
                                    " AND H.L_ink = '1' "+vClause+" " ;
                           Log.d("QUERY",sql);
                           Statement statement = conn.createStatement();
                           statement.execute(sql);
                           ResultSet rsPO = statement.getResultSet();
                           Double MaxQty=0.00;
                           String PORunNo="";
                           while (rsPO.next()) {
                               PORunNo             = rsPO.getString(2);
                               Double dQty         = rsPO.getDouble(3);
                               Double dQtyGRN      = getQtyGRNDO(rsPO.getString(2));
                               Double dQtySupInv   = getQtySupInv(rsPO.getString(2));
                               MaxQty              += dQty - dQtyGRN - dQtySupInv;
                           }
                           String limitQty=String.format(Locale.US, "%,.0f", MaxQty);
                           String qDum="select RunNo, ItemCode from tb_dumgrn_receiving where ItemCode='"+ItemCode+"' limit "+limitQty+" ";
                           Log.d("LIMIT QTY",limitQty);
                           Cursor rsDum=db.getQuery(qDum);
                           while(rsDum.moveToNext()){
                               String vUpdate="update tb_dumgrn_receiving set PORunNo='"+PORunNo+"', Remark='1', " +
                                       " MaxQty='"+MaxQty+"' where  RunNo='"+rsDum.getString(0)+"' ";
                               db.exeQuery(vUpdate);
                           }
                        }

                        String qCount="select count(*)as numrows from tb_dumgrn_receiving where Remark='0' ";
                        Cursor rsCount=db.getQuery(qCount);
                        while(rsCount.moveToNext()){
                            numrows=rsCount.getInt(0);
                        }
                        z="success";
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

        private Double getQtyGRNDO(String PORunNo){
            Double dQty=0.00;
            try{
                String sqlGRN="SELECT Dt.Qty from stk_grndo_dt Dt " +
                        " INNER JOIN stk_pur_order_dt Dpo " +
                        " ON Dt.PORunNo=Dpo.RunNo" +
                        " where Dt.PORunNo='"+PORunNo+"' " ;
                Log.d("QTY GRN", sqlGRN);
                Statement statement = conn.createStatement();
                statement.execute(sqlGRN);
                ResultSet rsGRN = statement.getResultSet();
                while (rsGRN.next()) {
                    dQty+= rsGRN.getDouble(1);
                }
                return dQty;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return dQty;
        }

        private Double getQtySupInv(String PORunNo){
            Double dQty=0.00;
            try{

                String sqlSupInv="SELECT Dt.Qty from stk_sup_inv_dt Dt " +
                        " INNER JOIN stk_pur_order_dt Dpo " +
                        " ON Dt.PORunNo=Dpo.RunNo" +
                        " where Dt.PORunNo='"+PORunNo+"' " ;
                Statement statement = conn.createStatement();
                statement.execute(sqlSupInv);
                ResultSet rsSup = statement.getResultSet();
                while (rsSup.next()) {
                    dQty+= rsSup.getDouble(1);
                }
                return dQty;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return dQty;
        }

    }

    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
    }
    private Double parseToDb(String values){
        Double newDouble=0.00;
        newDouble=Double.parseDouble(values);
        return newDouble;
    }
    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }

    private void confirmBack(){
        AlertDialog alertDialog = new AlertDialog.Builder(GRN_Receiving.this).create();
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Are you sure want to back ?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent mainIntent = new Intent(GRN_Receiving.this,MainActivity.class);
                        startActivity(mainIntent);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void setProgress(String vValue){
        txtProgress.setText(vValue);
    }
    private void SyncIn(){
        SyncIN sync=new SyncIN(this);
        sync.execute();
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

                   /* String qDelGroup = "delete from stk_group";
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
                    }*/

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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
