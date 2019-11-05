package com.skybiz.wms02.ui_LoadingGoods;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.ui_LoadingGoods.m_ListBay.DialogBay;
import com.skybiz.wms02.ui_LoadingGoods.m_ListChecker.DialogChecker;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDriver.DialogDriver;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDum.DownloadDum;
import com.skybiz.wms02.ui_LoadingGoods.m_ListLorry.DialogLorry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadingGoodsOut extends AppCompatActivity {

    EditText txtBay,txtCheckerCode,txtDriverCode,txtLorryNo,txtInvoiceNo;
    Button btnRefBay,btnRefChecker,btnRefDriver,btnRefLorry,btnClear,btnBack,btnSave;
    RecyclerView rvList;
    private GridLayoutManager lLayout;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_goods_out);
        getSupportActionBar().setTitle("Loading Goods Out");
        txtBay=(EditText)findViewById(R.id.txtBay);
        txtCheckerCode=(EditText)findViewById(R.id.txtCheckerCode);
        txtDriverCode=(EditText)findViewById(R.id.txtDriverCode);
        txtLorryNo=(EditText)findViewById(R.id.txtLorryNo);
        txtInvoiceNo=(EditText)findViewById(R.id.txtInvoiceNo);
        btnRefBay=(Button)findViewById(R.id.btnRefBay);
        btnRefChecker=(Button)findViewById(R.id.btnRefChecker);
        btnRefDriver=(Button)findViewById(R.id.btnRefDriver);
        btnRefLorry=(Button)findViewById(R.id.btnRefLorry);
        btnClear=(Button)findViewById(R.id.btnClear);
        btnSave=(Button)findViewById(R.id.btnSave);
        btnBack=(Button)findViewById(R.id.btnBack);
        rvList=(RecyclerView)findViewById(R.id.rvList);
        btnRefBay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBay();
            }
        });
        btnRefChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChecker();
            }
        });
        btnRefLorry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLorry();
            }
        });
        btnRefDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDriver();
            }
        });

        txtInvoiceNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String InvoiceNo=txtInvoiceNo.getText().toString();
                    if(!InvoiceNo.isEmpty()){
                        addInvoice();
                       /* final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txtInvoiceNo.requestFocus();
                            }
                        }, 300);*/

                    }else{
                        txtInvoiceNo.requestFocus();
                    }
                }
                return false;
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent mainIntent = new Intent(LoadingGoodsOut.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOUT();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInvoiceNo.getText().clear();
                txtInvoiceNo.requestFocus();
            }
        });
       initData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loadingout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mnSyncIn) {
            syncIN();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void saveOUT(){
        btnSave.setEnabled(false);
        String Bay          =txtBay.getText().toString();
        String CheckerCode  =txtCheckerCode.getText().toString();
        String LorryNo      =txtLorryNo.getText().toString();
        String DriverCode   =txtDriverCode.getText().toString();
        if(DriverCode.isEmpty()){
            Toast.makeText(this, "Driver Cannot Empty", Toast.LENGTH_SHORT).show();
        }else {
            SaveLoadingOUT save = new SaveLoadingOUT(this, Bay, CheckerCode, LorryNo, DriverCode);
            save.execute();
        }
    }
    private void initData(){
       resetDum();
       txtInvoiceNo.requestFocus();
    }
    private void loadBay(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","OUT");
        DialogBay dialogBay = new DialogBay();
        dialogBay.setArguments(b);
        dialogBay.show(getSupportFragmentManager(), "mList Bay");
    }
    private void loadChecker(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","OUT");
        DialogChecker dialogChecker = new DialogChecker();
        dialogChecker.setArguments(b);
        dialogChecker.show(getSupportFragmentManager(), "mList Checker");
    }
    private void loadLorry(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","OUT");
        DialogLorry dialogLorry = new DialogLorry();
        dialogLorry.setArguments(b);
        dialogLorry.show(getSupportFragmentManager(), "mList Lorry");
    }
    private void loadDriver(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","OUT");
        DialogDriver dialogDriver = new DialogDriver();
        dialogDriver.setArguments(b);
        dialogDriver.show(getSupportFragmentManager(), "mList Driver");
    }
    public void setBay(String Bay){
        txtBay.setText(Bay);
    }
    public void setChecker(String CheckerCode,String CheckerName){
        txtCheckerCode.setText(CheckerCode);
    }
    public void setDriver(String DriverCode,String DriverName){
        txtDriverCode.setText(DriverCode);
    }
    public void setLorry(String LorryNo){
        txtLorryNo.setText(LorryNo);
    }

    private void resetDum(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qDelete="delete from tb_dumlist";
            long add=db.exeQuery(qDelete);
            if(add>0){
                loadlist();
            }else{
                Toast.makeText(this, "Error, Add Invoice No", Toast.LENGTH_SHORT).show();
            }

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void addInvoice(){
        try{
            String InvoiceNo=txtInvoiceNo.getText().toString();
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qInsertDum="insert into tb_dumlist(Doc1No,Qty,Remark)values('"+InvoiceNo+"', '1', '')";
            long add=db.exeQuery(qInsertDum);
            if(add>0){
                loadlist();
                txtInvoiceNo.getText().clear();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtInvoiceNo.requestFocus();
                    }
                }, 300);
            }else{
                Toast.makeText(this, "Error, Add Invoice No", Toast.LENGTH_SHORT).show();
            }

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void delRow(String RunNo){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qDelete="delete from tb_dumlist where RunNo='"+RunNo+"' ";
            long add=db.exeQuery(qDelete);
            if(add>0){
                loadlist();

            }else{
                Toast.makeText(this, "Error, Delete Row", Toast.LENGTH_SHORT).show();
            }

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void loadlist(){
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        DownloadDum dList=new DownloadDum(this,"OUT", rvList);
        dList.execute();
    }

    private void afterSave(){
        btnSave.setEnabled(true);
        resetDum();
        loadlist();
        txtInvoiceNo.requestFocus();
    }
    private class SaveLoadingOUT extends AsyncTask<Void,Void,String>{
        Context c;
        String Bay,CheckerCode,LorryNo,DriverCode;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public SaveLoadingOUT(Context c, String bay, String checkerCode, String lorryNo, String driverCode) {
            this.c = c;
            Bay = bay;
            CheckerCode = checkerCode;
            LorryNo = lorryNo;
            DriverCode = driverCode;
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
            if(result.equals("success")){
                Toast.makeText(c, "Succesful, Save Loading Goods Out", Toast.LENGTH_SHORT).show();
                afterSave();
            }else if(result.equals("error")){
                Toast.makeText(c, "Failure, Save Loading Goods Out", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnsave(){
            try{
                z="error";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
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
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String qDumAll = "select RunNo, Doc1No, Qty, Remark from tb_dumlist Order By RunNo ";
                    Cursor rsDum = db.getQuery(qDumAll);
                    while (rsDum.moveToNext()) {
                        String RunNo = rsDum.getString(0);
                        String Doc1No = rsDum.getString(1);
                        String Qty = rsDum.getString(2);
                        String qInsertDt = "insert into wms_loading_dt(RunNo_Hd,Doc1NoOUT,DeliveredYN,Reason)values(" +
                                "'0', '" + Doc1No + "', '0', '')";
                        Statement stmtDt =conn.createStatement();
                        stmtDt.execute(qInsertDt);
                        String qDel="delete from tb_dumlist where RunNo='"+RunNo+"' ";
                        db.exeQuery(qDel);
                    }

                    String qInsertHd="insert into wms_loading_hd(Bay, CheckerCode, LorryNo," +
                            " DriverCode, D_ateTimeOUT)values('"+Bay+"', '"+CheckerCode+"', '"+LorryNo+"'," +
                            " '"+DriverCode+"', '"+D_ateTime+"')";

                    Statement stmtHd =conn.createStatement();
                    stmtHd.execute(qInsertHd);

                    String qCheckHd="select RunNo from wms_loading_hd where Bay='"+Bay+"' " +
                            "and CheckerCode='"+CheckerCode+"' " +
                            "and LorryNo='"+LorryNo+"' " +
                            "and DriverCode='"+DriverCode+"' " +
                            "and D_ateTimeOUT='"+D_ateTime+"' " ;

                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheckHd);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                        String RunNo_Hd=rsCheck.getString(1);
                        String qUpdateDt="update wms_loading_dt set RunNo_Hd='"+RunNo_Hd+"' where RunNo_Hd='0'  ";
                        Statement stmtUpDt =conn.createStatement();
                        stmtUpDt.execute(qUpdateDt);
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

    private void syncIN(){
        SyncINLoading syncin=new SyncINLoading(this);
        syncin.execute();
    }
    private class SyncINLoading extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public SyncINLoading(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnsync();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"Success, Synchronize In Data", Toast.LENGTH_SHORT).show();
            }else if(result.equals("error")){
                Toast.makeText(c,"Error, Synchronize In Data", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnsync(){
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
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {

                    String vDel1="delete from wms_loading";
                    db.exeQuery(vDel1);
                    String sql      = "select Bay from wms_loading_bay ";
                    Statement stmt  = conn.createStatement();
                    stmt.execute(sql);
                    ResultSet rsBay = stmt.getResultSet();
                    while(rsBay.next()){
                        String qInsertBay="insert into wms_loading_bay(Bay,Remark)values('"+rsBay.getString(1)+"','')";
                        db.exeQuery(qInsertBay);
                    }

                    String vDel2="delete from wms_checker";
                    db.exeQuery(vDel2);
                    String sqlChecker      = "select CheckerCode, CheckerName from wms_checker ";
                    Statement stmtChecker  = conn.createStatement();
                    stmtChecker.execute(sqlChecker);
                    ResultSet rsChecker = stmtChecker.getResultSet();
                    while(rsChecker.next()){
                        String qInsertChecker="insert into wms_checker(CheckerCode, CheckerName)values(" +
                                " '"+rsChecker.getString(1)+"', '"+rsChecker.getString(2)+"')";
                        db.exeQuery(qInsertChecker);
                    }

                    String vDel3="delete from wms_driver";
                    db.exeQuery(vDel3);
                    String sqlDriver      = "select DriverCode, DriverName from wms_driver ";
                    Statement stmtDriver  = conn.createStatement();
                    stmtDriver.execute(sqlDriver);
                    ResultSet rsDriver = stmtDriver.getResultSet();
                    while(rsDriver.next()){
                        String qInsertDriver="insert into wms_driver(DriverCode, DriverName)values(" +
                                " '"+rsDriver.getString(1)+"', '"+rsDriver.getString(2)+"')";
                        db.exeQuery(qInsertDriver);
                    }

                    String vDel4="delete from wms_lorry";
                    db.exeQuery(vDel4);
                    String sqlLorry      = "select LorryNo from wms_lorry ";
                    Statement stmtLorry  = conn.createStatement();
                    stmtLorry.execute(sqlLorry);
                    ResultSet rsLorry = stmtLorry.getResultSet();
                    while(rsLorry.next()){
                        String qInsertLorry="insert into wms_lorry(LorryNo, Remark)values(" +
                                " '"+rsLorry.getString(1)+"', '')";
                        db.exeQuery(qInsertLorry);
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
}
