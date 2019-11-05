package com.skybiz.wms02;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_DataObject.SetUOM;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_ItemList.DialogItem;
import com.skybiz.wms02.m_OnHand.DialogOnHand;
import com.skybiz.wms02.m_StockTake.DialogQty;
import com.skybiz.wms02.m_StockTake.m_List.DownloaderList;
import com.skybiz.wms02.m_Location.DialogLoc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StockTake extends AppCompatActivity {
    Button btnLoc,btnScan,btnOnHand,
            btnSave,btnBack;
    EditText txtLocationCode,txtItemCode,txtDescription,txtQty;
    TextView txtTaxCode,txtUnitPrice1,txtUnitPrice2,
            txtUOM,txtFactorQty,txtBarcode,
            txtSalesTaxCode,txtRetailTaxCode,txtUOM1,
            txtUOM2,txtUOM3,txtUOM4,txtUOMFactor1,
            txtUOMFactor2,txtUOMFactor3, txtUOMFactor4,
            txtTotalQty,chkSuspendedYN,txtTotal,txtProgress;
    boolean doubleBackToExitPressedOnce = false;
    private GridLayoutManager lLayout;
    RecyclerView rvList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_stock_take);
        getSupportActionBar().setTitle("Stock Take");
        rvList=(RecyclerView) findViewById(R.id.rvList);
        btnLoc=(Button)findViewById(R.id.btnLoc);
        btnScan=(Button)findViewById(R.id.btnScan);
        btnOnHand=(Button)findViewById(R.id.btnOnHand);
        btnBack=(Button)findViewById(R.id.btnBack);
        btnSave=(Button)findViewById(R.id.btnSave);
        txtLocationCode=(EditText)findViewById(R.id.txtLocationCode);

        txtDescription=(EditText)findViewById(R.id.txtDescription);
        txtQty=(EditText)findViewById(R.id.txtQty);
        txtItemCode=(EditText) findViewById(R.id.txtItemCode);
        txtUOM=(TextView)findViewById(R.id.txtUOM);
        txtFactorQty=(TextView)findViewById(R.id.txtFactorQty);
        txtTotalQty=(TextView)findViewById(R.id.txtTotalQty);
        txtTotal=(TextView)findViewById(R.id.txtTotalQty);
        txtProgress=(TextView)findViewById(R.id.txtProgress);
       /* txtTaxCode=(TextView)findViewById(R.id.txtTaxCode);
        //txtBarcode=(TextView)findViewById(R.id.txtBarcode);
        txtUnitPrice1=(TextView)findViewById(R.id.txtUnitPrice1);
        txtUnitPrice2=(TextView)findViewById(R.id.txtUnitPrice2);
        txtUOM=(TextView)findViewById(R.id.txtUOM);
        txtUOM1=(TextView)findViewById(R.id.txtUOM1);
        txtUOM2=(TextView) findViewById(R.id.txtUOM2);
        txtUOM3=(TextView)findViewById(R.id.txtUOM3);
        txtUOM4=(TextView)findViewById(R.id.txtUOM4);
        txtUOMFactor1=(TextView) findViewById(R.id.txtUOMFactor1);
        txtUOMFactor2=(TextView) findViewById(R.id.txtUOMFactor2);
        txtUOMFactor3=(TextView) findViewById(R.id.txtUOMFactor3);
        txtUOMFactor4=(TextView) findViewById(R.id.txtUOMFactor4);
        txtSalesTaxCode=(TextView)findViewById(R.id.txtSalesTaxCode);
        txtRetailTaxCode=(TextView)findViewById(R.id.txtRetailTaxCode);
        chkSuspendedYN=(TextView) findViewById(R.id.chkSuspendedYN);*/
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retLocation();
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemList();
            }
        });
        btnOnHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retOnHand();
            }
        });
        txtLocationCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String LocCode=txtLocationCode.getText().toString();
                    if(!LocCode.isEmpty()){
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txtItemCode.requestFocus();
                            }
                        }, 300);

                    }else{
                        txtLocationCode.requestFocus();
                    }
                }*/
                return false;
            }
        });
        txtQty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String Qty=txtQty.getText().toString();
                    if(!Qty.isEmpty()){
                        addItem();
                        //addItemNew();
                    }else{
                        txtQty.requestFocus();
                    }
                }
                return false;
            }
        });
        txtItemCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String ItemCode=txtItemCode.getText().toString();
                    if(!ItemCode.isEmpty()){
                       // addItemNew();
                        retItem();
                    }else{
                        txtItemCode.requestFocus();
                    }
                }
                return false;
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBack();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveST();
            }
        });
        txtLocationCode.setEnabled(false);
        onready();
        //disabledSave();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_take, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mnSyncOut) {
            SyncOut();
            return true;
        }else if(id == R.id.mnSyncIn){
            SyncIn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void SaveST(){
        try {
            disabledSave();
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String qInsert2 = "insert into ostk_stkcheck_dt(Doc1No, RunNoHd, ItemCode, " +
                    " ItemDesc, OnHandQty, CheckQty," +
                    " UnitCost, LineAmt,PostedYN," +
                    " ProcessYN, D_ate, UOM, " +
                    " FactorQty, LocationCode, UserEmail," +
                    " SyncYN, D_ateTime)select Doc1No, RunNoHd, ItemCode," +
                    " ItemDesc, OnHandQty, SUM(CheckQty)as CheckQty," +
                    " UnitCost, LineAmt, PostedYN," +
                    " ProcessYN, D_ate, UOM," +
                    " FactorQty, LocationCode, UserEmail," +
                    " '0', D_ateTime " +
                    " from dumlist Group By ItemCode ";
            Log.d("INSERT LOCAL",qInsert2);
            long save=db.exeQuery(qInsert2);
            if(save>0){
                afterSync();
            }
            /*String qInsertDum = "insert into dumlist(Doc1No, RunNoHd, ItemCode," +
                    " ItemDesc, OnHandQty, CheckQty," +
                    " UnitCost, LineAmt,PostedYN," +
                    " ProcessYN, D_ate, UOM, " +
                    " FactorQty, LocationCode, UserEmail," +
                    " AlternateItem,D_ateTime)values(" +
                    " '', '', '" + ItemCode + "'," +
                    " '" + Description + "', '0', '" + ChkQty + "'," +
                    " '0', '0', '0'," +
                    " '0', '" + D_ate + "', '" + UOM + "'," +
                    " '" + FactorQty + "', '" + LocationCode + "', '" + UserEmail + "'," +
                    " '" + AlternateItem + "', '"+D_ateTime+"')";
            Log.d("INSERT DUM",qInsert2);
            db.exeQuery(qInsertDum);*/
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void confirmBack(){
        AlertDialog alertDialog = new AlertDialog.Builder(StockTake.this).create();
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Are you sure want to back ?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent mainIntent = new Intent(StockTake.this,MainActivity.class);
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

    private void SyncOut(){
        //disabledSave();
        SyncStockTake sync=new SyncStockTake(this);
        sync.execute();
    }

    private void SyncIn(){
        SyncIN sync=new SyncIN(this);
        sync.execute();
    }
    private void disabledSave(){
        btnSave.setEnabled(false);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorBlack));
    }
    private void enabledSave(){
        btnSave.setEnabled(true);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    private void onready(){
        try {
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String vDel = "delete from dumlist";
            db.exeQuery(vDel);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void openUOM(String RunNo, String ItemCode, String D_ateTime){
        Bundle b=new Bundle();
        b.putString("TYPE_KEY","UOM");
        b.putString("UFROM_KEY","ST");
        b.putString("RUNNO_KEY",RunNo);
        b.putString("CODE_KEY",ItemCode);
        b.putString("DATETIME_KEY",D_ateTime);
        DialogQty dialogQty = new DialogQty();
        dialogQty.setArguments(b);
        dialogQty.show(getSupportFragmentManager(), "Dialog Qty");
    }
    public void openQty(String RunNo, String ItemCode, String D_ateTime){
        Bundle b=new Bundle();
        b.putString("TYPE_KEY","Qty");
        b.putString("UFROM_KEY","ST");
        b.putString("RUNNO_KEY",RunNo);
        b.putString("CODE_KEY",ItemCode);
        b.putString("DATETIME_KEY",D_ateTime);
        DialogQty dialogQty = new DialogQty();
        dialogQty.setArguments(b);
        dialogQty.show(getSupportFragmentManager(), "Dialog Qty");
    }

    public void editUOM(String RunNo, String UOM, String UOMPrice, String UOMFactor){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String upDum="update dumlist set UOM='"+UOM+"', FactorQty='"+UOMFactor+"' " +
                    " where RunNo='"+RunNo+"' ";
            db.exeQuery(upDum);
            db.closeDB();
            loadlist();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }
    public void editQty(String RunNo, String ItemCode, String D_ateTime, String Qty){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String upDum="update dumlist set CheckQty='"+Qty+"' where RunNo='"+RunNo+"' ";
            db.exeQuery(upDum);
           // String upStock="update ostk_stkcheck_dt set CheckQty='"+Qty+"'  " +
           //         "where ItemCode='"+ItemCode+"' and D_ateTime='"+D_ateTime+"' ";
          //  db.exeQuery(upStock);
            db.closeDB();
            loadlist();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }
    private void afterSync(){
        enabledSave();
        onready();
        loadlist();
        retTotal();
        txtLocationCode.getText().clear();
        txtLocationCode.requestFocus();
    }
    public void loadlist(){
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(StockTake.this, 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        DownloaderList dList=new DownloaderList(StockTake.this,rvList);
        dList.execute();
        txtItemCode.getText().clear();
        txtQty.setText("");
        txtDescription.setText("");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtItemCode.requestFocus();
            }
        }, 300);

        /*txtUOM.setText("");
         txtBarcode.setText("");
        txtUOM1.setText("");
        txtUOM2.setText("");
        txtUOM3.setText("");
        txtUOM4.setText("");
        txtUnitPrice1.setText("0.00");
        txtFactorQty.setText("0.00");
        txtUOMFactor1.setText("");
        txtUOMFactor2.setText("");
        txtUOMFactor3.setText("");
        txtUOMFactor4.setText("");
        txtTaxCode.setText("");*/
    }

    private void addItemNew(){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String D_ate = sdf.format(date);
            String D_ateTime = sdf2.format(date);
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String ChkQty       ="1";
            String AlternateItem="";
            String ItemCode     =txtItemCode.getText().toString();
            String Description  ="";
            String UOM          ="";
            String FactorQty    ="1";
            String LocationCode =txtLocationCode.getText().toString();
            String UserEmail     = Settings.Secure.getString(StockTake.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(LocationCode.isEmpty()){
                Toast.makeText(this, "Location Code Cannot Empty", Toast.LENGTH_SHORT).show();
            }else if(ItemCode.isEmpty()){
                Toast.makeText(this, "Item Code Cannot Empty", Toast.LENGTH_SHORT).show();
            }else {
                String qInsertDum = "insert into dumlist(Doc1No, RunNoHd, ItemCode," +
                        " ItemDesc, OnHandQty, CheckQty," +
                        " UnitCost, LineAmt,PostedYN," +
                        " ProcessYN, D_ate, UOM, " +
                        " FactorQty, LocationCode, UserEmail," +
                        " AlternateItem,D_ateTime)values(" +
                        " '', '', '" + ItemCode + "'," +
                        " '" + Description + "', '0', '" + ChkQty + "'," +
                        " '0', '0', '0'," +
                        " '0', '" + D_ate + "', '" + UOM + "'," +
                        " '" + FactorQty + "', '" + LocationCode + "', '" + UserEmail + "'," +
                        " '" + AlternateItem + "', '" + D_ateTime + "')";
                Log.d("INSERT DUM", qInsertDum);
                long add = db.exeQuery(qInsertDum);
                if (add > 0) {
                    loadlist();
                }
            }
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void addItem(){
        //String ChkQty       =txtQty.getText().toString();
        String ChkQty       ="1";
        String AlternateItem="";
        String ItemCode     =txtItemCode.getText().toString();
        String Description  =txtDescription.getText().toString();
        String UOM          =txtUOM.getText().toString();
        String FactorQty    =txtFactorQty.getText().toString();;
        String LocationCode =txtLocationCode.getText().toString();
        String UserEmail     = Settings.Secure.getString(StockTake.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(LocationCode.isEmpty()){
            Toast.makeText(this,"Location Code cannot empty", Toast.LENGTH_SHORT).show();
            txtLocationCode.requestFocus();
        }else if(ItemCode.isEmpty()){
            Toast.makeText(this,"Item Code cannot empty", Toast.LENGTH_SHORT).show();
            txtItemCode.requestFocus();
        }else {
            SaveItem saveItem = new SaveItem(StockTake.this, AlternateItem, ItemCode, Description, ChkQty, UOM, FactorQty, LocationCode, UserEmail);
            saveItem.execute();
        }
    }
    public void delRow(String RunNo, String ItemCode, String D_ateTime){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String delDum="delete from dumlist where RunNo='"+RunNo+"' ";
            db.exeQuery(delDum);
            //String delStock="delete from ostk_stkcheck_dt where ItemCode='"+ItemCode+"' and D_ateTime='"+D_ateTime+"' ";
            //db.exeQuery(delStock);
            loadlist();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void retOnHand(){
        String ItemCode=txtItemCode.getText().toString();
        String LocationCode=txtLocationCode.getText().toString();
        String QtyCur=txtQty.getText().toString();
        Bundle b=new Bundle();
        b.putString("ITEMCODE_KEY",ItemCode);
        b.putString("LOCATIONCODE_KEY",LocationCode);
        b.putString("QTYCUR_KEY",QtyCur);
        DialogOnHand dialogOnHand = new DialogOnHand();
        dialogOnHand.setArguments(b);
        dialogOnHand.show(getSupportFragmentManager(), "mOnHand");
    }
    private void retLocation(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","ST");
        b.putString("DBSTATUS_KEY","0");
        DialogLoc dialogLoc = new DialogLoc();
        dialogLoc.setArguments(b);
        dialogLoc.show(getSupportFragmentManager(), "mLocation");
    }
    public void setLocation(String LocatioCode){
        txtLocationCode.setText(LocatioCode);
        txtItemCode.requestFocus();
    }
    private void showItemList(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","ST");
        DialogItem dialogItem = new DialogItem();
        dialogItem.setArguments(b);
        dialogItem.show(getSupportFragmentManager(), "mListItem");
    }
    private void retItem(){
        String ItemCode=txtItemCode.getText().toString();
        if(!ItemCode.isEmpty()) {
            SetItem set = new SetItem(StockTake.this, "Any",ItemCode);
            set.execute();
        }else{
            Toast.makeText(this,"Item Code cannot empty", Toast.LENGTH_SHORT).show();
        }

    }
    public void setItem(String ItemCode){
       SetItem setItem = new SetItem(StockTake.this, "ItemCode" ,ItemCode);
       setItem.execute();
    }
    public class SetItem extends AsyncTask<Void,Void, String>{
        Context c;
        String C_ode, ItemCode,sql;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        String Description="",UnitPrice,UOM,FactorQty,
                TaxCode,SalesTaxCode,RetailTaxCode,PurchaseTaxCode,
                AlternateItem,SuspendedYN,UOM1,
                UOM2,UOM3,UOM4,
                UOMFactor1,UOMFactor2,UOMFactor3,
                UOMFactor4,DefUOM,DefaultUOM,vItemCode,
                UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4;
        DBAdapter db=null;

        public SetItem(Context c, String C_ode, String itemCode) {
            this.c = c;
            this.C_ode = C_ode;
            ItemCode = itemCode;
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
                //if(Description.length()>0) {
                txtItemCode.setText(vItemCode);
                txtDescription.setText(Description);
                txtUOM.setText(UOM);
                Toast.makeText(c, "UOM "+UOM, Toast.LENGTH_SHORT).show();
                txtFactorQty.setText(FactorQty);
                txtQty.requestFocus();
                addItem();
                //}else{
                  //  Toast.makeText(c, "Item Not Found", Toast.LENGTH_SHORT).show();
                //}
            }else if(result.equals("empty")){
                Toast.makeText(c, "Item Not Found", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c, "Error", Toast.LENGTH_SHORT).show();
            }
        }

        private String fnretitem() {
            try {
                z = "error";
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

                getCode();
                int i=0;
                Cursor rsItem=db.getQuery(sql);
                String tUnitPrice="";
                while (rsItem.moveToNext()) {
                    vItemCode = rsItem.getString(0);
                    Double dUnitPrice = rsItem.getDouble(3);
                    tUnitPrice = String.format(Locale.US, "%,.2f", dUnitPrice);
                    UOM     = Encode.setChar(CharType, rsItem.getString(6));
                    UOM1    = Encode.setChar(CharType, rsItem.getString(7));
                    UOM2    = Encode.setChar(CharType, rsItem.getString(8));
                    UOM3    = Encode.setChar(CharType, rsItem.getString(9));
                    UOM4    = Encode.setChar(CharType, rsItem.getString(10));
                    UOMFactor1 = rsItem.getString(15);
                    UOMFactor2 = rsItem.getString(16);
                    UOMFactor3 = rsItem.getString(17);
                    UOMFactor4 = rsItem.getString(18);
                    DefaultUOM =rsItem.getString(5);
                    UOMPrice1=rsItem.getString(11);
                    UOMPrice2=rsItem.getString(12);
                    UOMPrice3=rsItem.getString(13);
                    UOMPrice4=rsItem.getString(14);
                    Description = charReplace(Encode.setChar(CharType,rsItem.getString(1)));
                    i++;
                }
                if(C_ode.equals("ItemCode")){
                    DefUOM=DefaultUOM;
                }
                if(i>0){
                    SetUOM vData = SetUOM.set(tUnitPrice, DefUOM, UOM,
                            UOM1, UOM2, UOM3,
                            UOM4, UOMFactor1, UOMFactor2,
                            UOMFactor3, UOMFactor4,UOMPrice1,
                            UOMPrice2, UOMPrice3, UOMPrice4);
                    UOM         = vData.getUOM();
                    UnitPrice   = vData.getUnitPrice();
                    FactorQty   = vData.getFactorQty();
                    z="success";
                }else{
                    z="empty";
                }

                /*if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        statement.execute(sql);
                        ResultSet rsItem = statement.getResultSet();
                        while (rsItem.next()) {
                            String tUnitPrice = twoDecimal(rsItem.getDouble(4));
                            UOM1 = Encode.setChar(CharType, rsItem.getString(8));
                            UOM2 = Encode.setChar(CharType, rsItem.getString(9));
                            UOM3 = Encode.setChar(CharType, rsItem.getString(10));
                            UOM4 = Encode.setChar(CharType, rsItem.getString(11));
                            UOMFactor1 = twoDecimal(rsItem.getDouble(16));
                            UOMFactor2 = twoDecimal(rsItem.getDouble(17));
                            UOMFactor3 = twoDecimal(rsItem.getDouble(18));
                            UOMFactor4 = twoDecimal(rsItem.getDouble(19));
                            SetUOM vData = SetUOM.set(tUnitPrice, rsItem.getString(6), Encode.setChar(CharType, rsItem.getString(7)),
                                    UOM1, UOM2, UOM3,
                                    UOM4, UOMFactor1, UOMFactor2,
                                    UOMFactor3, UOMFactor4, rsItem.getString(12),
                                    rsItem.getString(13), rsItem.getString(14), rsItem.getString(15));
                            UOM       = vData.getUOM();
                            UnitPrice = vData.getUnitPrice();
                            FactorQty = vData.getFactorQty();
                            Description = charReplace(Encode.setChar(CharType,rsItem.getString(2)));
                        }
                        z="success";
                    } else {
                        z="error";
                    }

                }else if(DBStatus.equals("0")){
                    Cursor rsItem=db.getQuery(sql);
                    while (rsItem.moveToNext()) {
                        Double dUnitPrice = rsItem.getDouble(3);
                        String tUnitPrice = String.format(Locale.US, "%,.2f", dUnitPrice);
                        UOM1 = Encode.setChar(CharType, rsItem.getString(7));
                        UOM2 = Encode.setChar(CharType, rsItem.getString(8));
                        UOM3 = Encode.setChar(CharType, rsItem.getString(9));
                        UOM4 = Encode.setChar(CharType, rsItem.getString(10));
                        UOMFactor1 = rsItem.getString(15);
                        UOMFactor2 = rsItem.getString(16);
                        UOMFactor3 = rsItem.getString(17);
                        UOMFactor4 = rsItem.getString(18);
                        SetUOM vData = SetUOM.set(tUnitPrice, rsItem.getString(5), Encode.setChar(CharType, rsItem.getString(6)),
                                UOM1, UOM2, UOM3,
                                UOM4, UOMFactor1, UOMFactor2,
                                UOMFactor3, UOMFactor4, rsItem.getString(11),
                                rsItem.getString(12), rsItem.getString(13), rsItem.getString(14));
                        UOM = vData.getUOM();
                        UnitPrice = vData.getUnitPrice();
                        FactorQty = vData.getFactorQty();
                        Description = charReplace(Encode.setChar(CharType,rsItem.getString(1)));
                    }
                    z="success";
                }*/
                db.closeDB();
                return z;
            } catch (SQLiteException e) {
                e.printStackTrace();
            } /*catch (SQLException e) {
                e.printStackTrace();
            }*/
            return z;
        }

        private int getNumRow(String C_ode){
            int i=0;
            try{
                String vClause="";
                if(C_ode.equals("BaseCode")){
                    vClause="and BaseCode= '"+ItemCode+"' ";
                }else if(C_ode.equals("UOMCode1")){
                    vClause="and UOMCode1= '"+ItemCode+"' ";
                }else if(C_ode.equals("UOMCode2")){
                    vClause="and UOMCode2= '"+ItemCode+"' ";
                }else if(C_ode.equals("UOMCode3")){
                    vClause="and UOMCode3= '"+ItemCode+"' ";
                }else if(C_ode.equals("UOMCode4")){
                    vClause="and UOMCode4= '"+ItemCode+"' ";
                }else if(C_ode.equals("ItemCode")){
                    vClause="and ItemCode= '"+ItemCode+"' ";
                }
                String qCount="select count(*)as numrows from stk_master where SuspendedYN='0' "+vClause+" ";
                Cursor rsCount=db.getQuery(qCount);
                while(rsCount.moveToNext()){
                    i=rsCount.getInt(0);
                }
                return i;
            }catch (SQLiteException e){
                e.printStackTrace();
            }
            return i;
        }
        private void getCode(){
            String vClause="";
            if(C_ode.equals("ItemCode")){
                vClause="and ItemCode='"+ItemCode+"' ";
               // DefUOM=DefaultUOM;
            }else{
                int i0=getNumRow("ItemCode");
                if(i0==0) {
                    int i1 = getNumRow("BaseCode");
                    if (i1 == 0) {
                        int i2 = getNumRow("UOMCode1");
                        if (i2 == 0) {
                            int i3 = getNumRow("UOMCode2");
                            if (i3 == 0) {
                                int i4 = getNumRow("UOMCode3");
                                if (i4 == 0) {
                                    int i5 = getNumRow("UOMCode4");
                                    if (i5 == 0) {
                                        vClause = "and ItemCode= '" + ItemCode + "' ";
                                        z       = "empty";
                                    } else {
                                        DefUOM = "4";
                                        vClause = "and UOMCode4= '" + ItemCode + "' ";
                                    }
                                } else {
                                    DefUOM = "3";
                                    vClause = "and UOMCode3= '" + ItemCode + "' ";
                                }
                            } else {
                                DefUOM = "2";
                                vClause = "and UOMCode2= '" + ItemCode + "' ";
                            }
                        } else {
                            DefUOM = "1";
                            vClause = "and UOMCode1= '" + ItemCode + "' ";
                        }
                    } else {
                        DefUOM = "0";
                        vClause = "and BaseCode= '" + ItemCode + "' ";
                    }
                }else{
                    C_ode = "ItemCode";
                    vClause = "and ItemCode= '" + ItemCode + "' ";
                }
            }
            sql = "select ItemCode, Description, ItemGroup, UnitPrice," +
                    " UnitCost, DefaultUOM, UOM, IFNULL(UOM1,'') as UOM1, " +
                    " IFNULL(UOM2,'') as UOM2, IFNULL(UOM3,'') as UOM3, IFNULL(UOM4,'') as UOM4, IFNULL(UOMPrice1,0) as UOMPrice1," +
                    " IFNULL(UOMPrice2,0) as UOMPrice2,  IFNULL(UOMPrice3,0)as UOMPrice3, IFNULL(UOMPrice4,0)as UOMPrice4, IFNULL(UOMFactor1,0)as UOMFactor1," +
                    " IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4 "+
                    " from stk_master where SuspendedYN='0' "+vClause+"  ";
        }
    }
    public class SetItemOld extends AsyncTask<Void,Void,String>{
        Context c;
        String ItemCode;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        String Description="",UnitPrice,UOM,FactorQty,
                TaxCode,SalesTaxCode,RetailTaxCode,PurchaseTaxCode,
                AlternateItem,SuspendedYN,UOM1,
                UOM2,UOM3,UOM4,
                UOMFactor1,UOMFactor2,UOMFactor3,
                UOMFactor4;

        public SetItemOld(Context c, String ItemCode) {
            this.c = c;
            this.ItemCode = ItemCode;
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
            if(result.equals("error")){
                Toast.makeText(c,"Failure, No data retrieve", Toast.LENGTH_SHORT).show();
            }else if(result.equals("success")){
                if(Description.length()>0) {
                    txtItemCode.setText(ItemCode);
                    txtDescription.setText(Description);
                    txtTaxCode.setText(TaxCode);
                    txtUnitPrice1.setText(UnitPrice);
                    txtUOM.setText(UOM);
                    txtUOM1.setText(UOM1);
                    txtUOM2.setText(UOM2);
                    txtUOM3.setText(UOM3);
                    txtUOM4.setText(UOM4);
                    txtFactorQty.setText(FactorQty);
                    Double dFactor1 = parseToDb(UOMFactor1);
                    Double dFactor2 = parseToDb(UOMFactor2);
                    Double dFactor3 = parseToDb(UOMFactor3);
                    Double dFactor4 = parseToDb(UOMFactor3);
                    if (dFactor1 > 1) {
                        txtUOMFactor1.setText(UOMFactor1);
                    }
                    if (dFactor2 > 1) {
                        txtUOMFactor2.setText(UOMFactor2);
                    }
                    if (dFactor3 > 1) {
                        txtUOMFactor3.setText(UOMFactor3);
                    }
                    if (dFactor4 > 1) {
                        txtUOMFactor4.setText(UOMFactor4);
                    }
                    txtBarcode.setText(AlternateItem);
                    if (SuspendedYN.equals("1")) {
                        chkSuspendedYN.setText("[Suspended]");
                    } else {
                        chkSuspendedYN.setText("[Active]");
                    }
                    txtQty.requestFocus();
                }else{
                    Toast.makeText(c,"Failure, Item Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private String downloadData() {
            try {
                z="error";
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

                String sql = "select ItemCode, Description, ItemGroup, UnitPrice," +
                        " UnitCost, DefaultUOM,UOM, IFNULL(UOM1,'') as UOM1, " +
                        " IFNULL(UOM2,'') as UOM2, IFNULL(UOM3,'') as UOM3, IFNULL(UOM4,'') as UOM4, IFNULL(UOMPrice1,0) as UOMPrice1," +
                        " IFNULL(UOMPrice2,0) as UOMPrice2,  IFNULL(UOMPrice3,0)as UOMPrice3, IFNULL(UOMPrice4,0)as UOMPrice4, IFNULL(UOMFactor1,0)as UOMFactor1," +
                        " IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4, AnalysisCode1," +
                        " AnalysisCode2, AnalysisCode3, AnalysisCode4, AnalysisCode5," +
                        " MAXSP, MAXSP1, MAXSP2, MAXSP3," +
                        " MAXSP4, SalesTaxCode, RetailTaxCode, PurchaseTaxCode," +
                        " FixedPriceYN, SuspendedYN, AlternateItem " +
                        " from stk_master where SuspendedYN='0' and ItemCode='"+ItemCode+"' ";
                Log.d("QUERY",sql);
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if(conn!=null){
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (statement.execute(sql)) {
                            ResultSet rsItem = statement.getResultSet();
                            while (rsItem.next()) {
                                String tUnitPrice   =twoDecimal(rsItem.getDouble(4));
                                UOM1                =Encode.setChar(CharType,rsItem.getString(8));
                                UOM2                =Encode.setChar(CharType,rsItem.getString(9));
                                UOM3                =Encode.setChar(CharType,rsItem.getString(10));
                                UOM4                =Encode.setChar(CharType,rsItem.getString(11));
                                UOMFactor1          =twoDecimal(rsItem.getDouble(16));
                                UOMFactor2          =twoDecimal(rsItem.getDouble(17));
                                UOMFactor3          =twoDecimal(rsItem.getDouble(18));
                                UOMFactor4          =twoDecimal(rsItem.getDouble(19));
                                SetUOM vData=SetUOM.set(tUnitPrice,rsItem.getString(6),Encode.setChar(CharType,rsItem.getString(7)),
                                            UOM1,UOM2,UOM3,
                                            UOM4,UOMFactor1,UOMFactor2,
                                            UOMFactor3,UOMFactor4, rsItem.getString(12),
                                            rsItem.getString(13),rsItem.getString(14),rsItem.getString(15));
                                UOM         =vData.getUOM();
                                UnitPrice   =vData.getUnitPrice();
                                FactorQty   =vData.getFactorQty();
                                SalesTaxCode =rsItem.getString(30);
                                RetailTaxCode=rsItem.getString(31);
                                TaxCode     =rsItem.getString(32);
                                ItemCode    =rsItem.getString(1);
                                Description = charReplace(Encode.setChar(CharType,rsItem.getString(2)));
                                SuspendedYN=rsItem.getString(34);
                                AlternateItem=rsItem.getString(35);
                            }
                            z="success";
                        }
                    }else{
                        z="error";
                    }
                }else{
                    Cursor rsItem=db.getQuery(sql);
                    while (rsItem.moveToNext()) {
                        Double dUnitPrice   =rsItem.getDouble(3);
                        String tUnitPrice    = String.format(Locale.US, "%,.2f", dUnitPrice);
                        UOM1   =Encode.setChar(CharType,rsItem.getString(7));
                        UOM2   =Encode.setChar(CharType,rsItem.getString(8));
                        UOM3   =Encode.setChar(CharType,rsItem.getString(9));
                        UOM4   =Encode.setChar(CharType,rsItem.getString(10));
                        UOMFactor1  =rsItem.getString(15);
                        UOMFactor2  =rsItem.getString(16);
                        UOMFactor3  =rsItem.getString(17);
                        UOMFactor4  =rsItem.getString(18);
                        SetUOM vData=SetUOM.set(tUnitPrice,rsItem.getString(5),Encode.setChar(CharType,rsItem.getString(6)),
                                UOM1,UOM2,UOM3,
                                UOM4,UOMFactor1,UOMFactor2,
                                UOMFactor3,UOMFactor4, rsItem.getString(11),
                                rsItem.getString(12),rsItem.getString(13),rsItem.getString(14));
                        UOM         =vData.getUOM();
                        UnitPrice   =vData.getUnitPrice();
                        FactorQty   =vData.getFactorQty();
                        SalesTaxCode =rsItem.getString(29);
                        RetailTaxCode=rsItem.getString(30);
                        TaxCode     =rsItem.getString(31);
                        ItemCode    =rsItem.getString(0);
                        Description =rsItem.getString(1);
                        SuspendedYN=rsItem.getString(33);
                        AlternateItem=rsItem.getString(34);
                    }
                    z="success";
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
    }

    private void retTotal(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qCount="select count(*)as total from dumlist";
            Cursor rsCount=db.getQuery(qCount);
            while(rsCount.moveToNext()){
                txtTotalQty.setText("Qty "+rsCount.getString(0));
            }

            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public class SaveItem extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        String ItemCode,Description,ChkQty,UOM,FactorQty,LocationCode,UserEmail,AlternateItem;

        public SaveItem(Context c, String AlternateItem, String itemCode,
                        String description, String chkQty, String UOM,
                        String factorQty, String locationCode,
                        String userEmail) {
            this.c = c;
            this.AlternateItem=AlternateItem;
            ItemCode = itemCode;
            Description = description;
            ChkQty = chkQty;
            this.UOM = UOM;
            FactorQty = factorQty;
            LocationCode = locationCode;
            UserEmail = userEmail;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.save();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Failure, add item", Toast.LENGTH_SHORT).show();
            }else{
                loadlist();
                retTotal();
                //resetform();
            }
        }

        private String save() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String D_ate = sdf.format(date);
                String D_ateTime = sdf2.format(date);
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "Port,DBName,DBStatus from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    Port = curSet.getString(3);
                    DBName = curSet.getString(4);
                    DBStatus = curSet.getString(5);
                }
               /* if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        String qInsert="insert into ostk_stkcheck_dt(Doc1No, RunNoHd, ItemCode," +
                                " ItemDesc, OnHandQty, CheckQty," +
                                " UnitCost, LineAmt,PostedYN," +
                                " ProcessYN, D_ate, UOM, " +
                                " FactorQty, LocationCode, UserEmail)values(" +
                                " '', '0', '"+ItemCode+"'," +
                                " '"+Description+"', '0', '"+ChkQty+"'," +
                                " '0', '0', '0'," +
                                " '0', '"+D_ate+"', '"+UOM+"'," +
                                " '"+FactorQty+"', '"+LocationCode+"', '"+UserEmail+"')";
                        Statement statement = conn.createStatement();
                        statement.execute(qInsert);
                    }
                }else {
                    String qInsert2 = "insert into ostk_stkcheck_dt(Doc1No, RunNoHd, ItemCode," +
                            " ItemDesc, OnHandQty, CheckQty," +
                            " UnitCost, LineAmt,PostedYN," +
                            " ProcessYN, D_ate, UOM, " +
                            " FactorQty, LocationCode, UserEmail," +
                            " SyncYN)values(" +
                            " '', '', '" + ItemCode + "'," +
                            " '" + Description + "', '0', '" + ChkQty + "'," +
                            " '0', '0', '0'," +
                            " '0', '" + D_ate + "', '" + UOM + "'," +
                            " '" + FactorQty + "', '" + LocationCode + "', '" + UserEmail + "'," +
                            " '" + DBStatus + "')";
                    db.exeQuery(qInsert2);
                    Log.d("INSERT LOCAL",qInsert2);
                }*/

                /*String qInsert2 = "insert into ostk_stkcheck_dt(Doc1No, RunNoHd, ItemCode, " +
                        " ItemDesc, OnHandQty, CheckQty," +
                        " UnitCost, LineAmt,PostedYN," +
                        " ProcessYN, D_ate, UOM, " +
                        " FactorQty, LocationCode, UserEmail," +
                        " SyncYN, D_ateTime)values(" +
                        " '', '', '" + ItemCode + "'," +
                        " '" + Description + "', '0', '" + ChkQty + "'," +
                        " '0', '0', '0'," +
                        " '0', '" + D_ate + "', '" + UOM + "'," +
                        " '" + FactorQty + "', '" + LocationCode + "', '" + UserEmail + "'," +
                        " '0', '"+D_ateTime+"')";
                db.exeQuery(qInsert2);
                Log.d("INSERT LOCAL",qInsert2);*/

                String qInsertDum = "insert into dumlist(Doc1No, RunNoHd, ItemCode," +
                        " ItemDesc, OnHandQty, CheckQty," +
                        " UnitCost, LineAmt,PostedYN," +
                        " ProcessYN, D_ate, UOM, " +
                        " FactorQty, LocationCode, UserEmail," +
                        " AlternateItem,D_ateTime)values(" +
                        " '', '', '" + ItemCode + "'," +
                        " '" + Description + "', '0', '" + ChkQty + "'," +
                        " '0', '0', '0'," +
                        " '0', '" + D_ate + "', '" + UOM + "'," +
                        " '" + FactorQty + "', '" + LocationCode + "', '" + UserEmail + "'," +
                        " '" + AlternateItem + "', '"+D_ateTime+"')";
                Log.d("INSERT DUM",qInsertDum);
                db.exeQuery(qInsertDum);
                z="success";
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }
            return z;
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
    private String charReplace2(String text){
        String newText=text.replaceAll("[\\.$|,|;|'()]","\\$1");
        return newText;
    }
    public void setProgress(String vValue){
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

    public class SyncStockTake extends AsyncTask<Void,Void, String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        public SyncStockTake(Context c) {
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
            if (result.equals("error")) {
                Toast.makeText(c, "Error Connection", Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                Toast.makeText(c, "Synchronize Stock Take successful", Toast.LENGTH_SHORT).show();

                afterSync();
            }else if(result.equals("not found")){
                Toast.makeText(c, "Data to synchronize out not found", Toast.LENGTH_SHORT).show();
            }
        }

        private String syncOUT() {
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
                    String query="select Doc1No, RunNoHd, ItemCode, " +
                            " ItemDesc, OnHandQty, CheckQty, " +
                            " UnitCost, LineAmt,PostedYN, " +
                            " ProcessYN, D_ate, UOM, " +
                            " IFNULL(FactorQty,'1')as FactorQty, LocationCode, UserEmail," +
                            " RunNo " +
                            " from ostk_stkcheck_dt where SyncYN='0' ";
                    Cursor rsData=db.getQuery(query);
                    int i=0;
                    while(rsData.moveToNext()){
                        String FactorQty=rsData.getString(12);
                        if(FactorQty.isEmpty()){
                            FactorQty="1";
                        }
                        String qInsert="insert into ostk_stkcheck_dt(Doc1No, RunNoHd, ItemCode," +
                                " ItemDesc, OnHandQty, CheckQty," +
                                " UnitCost, LineAmt,PostedYN," +
                                " ProcessYN, D_ate, UOM, " +
                                " FactorQty, LocationCode, UserEmail)values(" +
                                " '', '0', '"+rsData.getString(2)+"'," +
                                " '"+rsData.getString(3)+"', '"+rsData.getString(4)+"', '"+rsData.getString(5)+"'," +
                                " '"+rsData.getString(6)+"', '"+rsData.getString(7)+"', '"+rsData.getString(8)+"'," +
                                " '"+rsData.getString(9)+"', '"+rsData.getString(10)+"', '"+rsData.getString(11)+"'," +
                                " '"+FactorQty+"', '"+rsData.getString(13)+"', '"+rsData.getString(14)+"')";
                        Log.d("QUERY",qInsert);
                        Statement statement = conn.createStatement();
                        statement.execute(qInsert);
                        String vUpdate="update ostk_stkcheck_dt set SyncYN='1' where RunNo='"+rsData.getString(15)+"' ";
                        Log.d("Update Sync OUT", vUpdate);
                        db.exeQuery(vUpdate);
                        i++;
                    }
                    if(i>0){
                        z="success";
                    }else{
                        z="not found";
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
