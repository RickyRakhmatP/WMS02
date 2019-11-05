package com.skybiz.wms02;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_Listing.Listing;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsIn;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsOut;
import com.skybiz.wms02.ui_PickingList.PickingList;
import com.skybiz.wms02.ui_PrintInvLabel.PrintInvLabel;
import com.skybiz.wms02.ui_PrintPrepackLabel.PrintPrepackLabel;
import com.skybiz.wms02.ui_PrintPrepackLabel.PrintPrepackLabel2;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_Setting.Setting;
import com.skybiz.wms02.ui_StockEnquiry.StockEnquiry;
import com.skybiz.wms02.ui_SupInv.SupplierInvoice;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    Button btnStockTake,btnSync,btnSetting,
            btnIS,btnListing,btnGRNDO,
            btnRS,btnTS,btnStockEnquiry,btnPrintPrepack,
            btnPrintPrepack2,btnPickingList,btnGRNReceiving,
            btnPrintInvLabel,btnLoadingGoodsOUT,btnLoadingGoodsIN,btnSupInv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // enableStrictMode(this);
      /*  if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }*/
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.app_version);
        btnStockTake=(Button)findViewById(R.id.btnStockTake);
        btnSync=(Button)findViewById(R.id.btnSync);
        btnSetting=(Button)findViewById(R.id.btnSetting);
        btnIS=(Button)findViewById(R.id.btnIS);
        btnGRNDO=(Button)findViewById(R.id.btnGRNDO);
        btnTS=(Button)findViewById(R.id.btnTS);
        btnRS=(Button)findViewById(R.id.btnRS);
        btnListing=(Button)findViewById(R.id.btnListing);
        btnStockEnquiry=(Button)findViewById(R.id.btnStockEnquiry);
        btnPrintPrepack=(Button)findViewById(R.id.btnPrintPrepack);
        btnPrintPrepack2=(Button)findViewById(R.id.btnPrintPrepack2);
        btnPickingList=(Button)findViewById(R.id.btnPickingList);
        btnGRNReceiving=(Button)findViewById(R.id.btnGRNReceive);
        btnPrintInvLabel=(Button)findViewById(R.id.btnPrintInvLabel);
        btnLoadingGoodsOUT=(Button)findViewById(R.id.btnLoadingGoodsOUT);
        btnLoadingGoodsIN=(Button)findViewById(R.id.btnLoadingGoodsIN);
        btnSupInv=(Button)findViewById(R.id.btnSupInv);
        btnStockTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, StockTake.class);
                startActivity(mainIntent);
            }
        });

        btnGRNReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, GRN_Receiving.class);
                startActivity(mainIntent);
            }
        });
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, Synchronize.class);
                startActivity(mainIntent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, Setting.class);
                startActivity(mainIntent);
            }
        });

        btnGRNDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, GRNDO.class);
                startActivity(mainIntent);
            }
        });
        btnIS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, IssueStock.class);
                startActivity(mainIntent);
            }
        });
        btnRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, ReceiptStock.class);
                startActivity(mainIntent);
            }
        });
        btnTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, TransferStock.class);
                startActivity(mainIntent);
            }
        });
        btnListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, Listing.class);
                startActivity(mainIntent);
            }
        });
        btnStockEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, StockEnquiry.class);
                startActivity(mainIntent);
            }
        });
        btnPrintPrepack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, PrintPrepackLabel.class);
                startActivity(mainIntent);
            }
        });
        btnPrintPrepack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, PrintPrepackLabel2.class);
                startActivity(mainIntent);
            }
        });
        btnPickingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, PickingList.class);
                startActivity(mainIntent);
            }
        });
        btnPrintInvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, PrintInvLabel.class);
                startActivity(mainIntent);
            }
        });
        btnLoadingGoodsOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, LoadingGoodsOut.class);
                startActivity(mainIntent);
            }
        });
        btnLoadingGoodsIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, LoadingGoodsIn.class);
                startActivity(mainIntent);
            }
        });
        btnSupInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, SupplierInvoice.class);
                startActivity(mainIntent);
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        initPermission();
        createNewTable();
        initButton();
        fnchecksetting();

    }

    private void initPermission(){

    }
    private void createNewTable(){
        try{
            DBAdapter db=new DBAdapter(this );
            db.openDB();

            String new1="CREATE TABLE IF NOT EXISTS tb_settingprinter(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " IPAddress TEXT, Port TEXT Default '', Remark TEXT Default '') ";
            db.exeQuery(new1);

            String new2="CREATE TABLE IF NOT EXISTS tb_dumpicking(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', Qty TEXT Default '', Remark TEXT Default '') ";
            db.exeQuery(new2);

            String new3="CREATE TABLE IF NOT EXISTS tb_dumprint2(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Address1 TEXT Default '', Address2 TEXT Default '', Address3 TEXT Default '',  Address4 TEXT Default '', " +
                    " CusName1 TEXT Default '', CusName2 TEXT Default '', CusName3 TEXT Default '') ";
            db.exeQuery(new3);

            String new4="CREATE TABLE IF NOT EXISTS stk_userdefine_trn(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', UserDefine1 TEXT Default '', UserDefine2 TEXT Default '',  UserDefine3 TEXT Default '', " +
                    " UserDefine4 TEXT Default '', UserDefine5 TEXT Default '', UserDefine6 TEXT Default ''," +
                    " UserDefine7 TEXT Default '', UserDefine8 TEXT Default '', UserDefine9 TEXT Default ''," +
                    " UserDefine10 TEXT Default '', UserDefine11 TEXT Default '', UserDefine12 TEXT Default ''," +
                    " UserDefine13 TEXT Default '', UserDefine14 TEXT Default '', UserDefine15 TEXT Default ''," +
                    " Delivery TEXT Default '', DocType TEXT Default '', AmountInWord TEXT Default ''," +
                    " AttachmentDescription TEXT Default '', AmountInWordFC TEXT Default '', PhotoFile BLOB) ";

            db.exeQuery(new4);

            String new5="CREATE TABLE IF NOT EXISTS tb_dumgrn_receiving(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ItemCode TEXT Default '', Qty REAL, Description TEXT Default '',  AlternateItem TEXT Default '', " +
                    " SupplierItemCode TEXT Default '', BaseCode TEXT Default '', Remark TEXT Default '', " +
                    " PORunNo TEXT Default '', MaxQty REAL ) ";
            db.exeQuery(new5);

            String new6="CREATE TABLE IF NOT EXISTS dum_stk_pur_order_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ItemCode TEXT Default '', Qty REAL, Description TEXT Default '',  AlternateItem TEXT Default '', " +
                    " SupplierItemCode TEXT Default '', BaseCode TEXT Default '', PORunNo TEXT Default ''," +
                    " Doc1No TEXT Default '' ) ";
            db.exeQuery(new6);

            String new7="CREATE TABLE IF NOT EXISTS tb_dumlist(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', Qty TEXT Default '', Remark TEXT Default '') ";
            db.exeQuery(new7);

            String new8="CREATE TABLE IF NOT EXISTS wms_loading_bay(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Bay TEXT Default '', Remark TEXT Default '') ";
            db.exeQuery(new8);

            String new9="CREATE TABLE IF NOT EXISTS wms_checker(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " CheckerCode TEXT Default '', CheckerName TEXT Default '') ";
            db.exeQuery(new9);

            String new10="CREATE TABLE IF NOT EXISTS wms_driver(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " DriverCode TEXT Default '', DriverName TEXT Default '') ";
            db.exeQuery(new10);

            String new11="CREATE TABLE IF NOT EXISTS wms_lorry(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " LorryNo TEXT Default '', Remark TEXT Default '') ";
            db.exeQuery(new11);

            String new12="CREATE TABLE IF NOT EXISTS cloud_sup_inv_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', " +
                    " ItemCode TEXT Default '', " +
                    " Description TEXT Default '', " +
                    " Qty REAL Default 0, " +
                    " FactorQty  REAL Default 0, " +
                    " UOM TEXT Default '', " +
                    " UOMSingular TEXT Default '', " +
                    " HCUnitCost REAL Default 0, " +
                    " DisRate1 REAL Default 0, " +
                    " HCDiscount REAL Default 0, " +
                    " TaxRate1 REAL Default 0, " +
                    " DetailTaxCode TEXT Default '', " +
                    " HCTax REAL Default 0, " +
                    " HCLineAmt REAL Default 0, " +
                    " DepartmentCode TEXT Default '', " +
                    " BranchCode TEXT Default '', " +
                    " ProjectCode TEXT Default '', " +
                    " LocationCode TEXT Default '', " +
                    " ItemBatch TEXT Default '', " +
                    " WarrantyDate TEXT Default '') ";
            db.exeQuery(new12);

            String new13="CREATE TABLE IF NOT EXISTS cloud_sup_inv_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', " +
                    " D_ate TEXT Default '', " +
                    " LocationCode TEXT Default '', " +
                    " CusCode TEXT Default '', " +
                    " CusName TEXT Default '', " +
                    " Remark TEXT Default '' ) ";
            db.exeQuery(new13);

            String new14 = "CREATE TABLE IF NOT EXISTS sys_userprofile_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, UserCode TEXT, " +
                    "UserName TEXT, Password TEXT, DepartmentCode TEXT," +
                    "AddNewYN TEXT, ViewYN TEXT, RePrintYN TEXT," +
                    "OWCreditLimitYN TEXT, FOCItemYN TEXT, OWNegativeStkYN TEXT," +
                    "ItemListingBalQtyYN TEXT, ItemListingCostYN TEXT, ItemListingPriceYN TEXT," +
                    "EditAmountYN TEXT, OWInvoiceLimitYN TEXT, OWOverdueOutstandingYN TEXT," +
                    "VoidPOYN TEXT, VoidSOYN TEXT, VoidCusCNYN TEXT," +
                    "OWMSPYN TEXT, EncryptionYN TEXT, MinItemLevelYN TEXT," +
                    "MaxItemLevelYN TEXT, PrintDocumentYN TEXT, PrintDocumentTimes TEXT," +
                    "PrintReportYN TEXT, OWMPPYN TEXT, Password2 TEXT, " +
                    "MRUserLevel TEXT, programname TEXT, Status TEXT Default '')";
            db.exeQuery(new14);

            db.closeDB();

            db.openDB();
            if (db.isColumnExists("tb_setting", "Modules") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN Modules TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }
            if (db.isColumnExists("dumlist", "D_ateTime") != true) {
                String alterCloud = "ALTER TABLE dumlist ADD COLUMN D_ateTime TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }
            if (db.isColumnExists("ostk_stkcheck_dt", "D_ateTime") != true) {
                String alterCloud = "ALTER TABLE ostk_stkcheck_dt ADD COLUMN D_ateTime TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }

            if (db.isColumnExists("stk_master", "BaseCode") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN BaseCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }

            if (db.isColumnExists("stk_master", "UOMCode1") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode1 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }

            if (db.isColumnExists("stk_master", "UOMCode2") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode2 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "UOMCode3") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode3 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "UOMCode4") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode4 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }
            if (db.isColumnExists("tb_dumlist", "DeliveredYN") != true) {
                String alterCloud = "ALTER TABLE tb_dumlist ADD COLUMN DeliveredYN TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.exeQuery(alterCloud);
            }

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ///DialogPassword dialogTable=new DialogPassword();
        //dialogTable.show(getSupportFragmentManager(),"mTag");
        getMenuInflater().inflate(R.menu.menu_mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnSetting) {
            DialogPassword dialogPassword=new DialogPassword();
            dialogPassword.show(getSupportFragmentManager(),"mTag");
            return true;
        }else if (id == R.id.mnBackup) {
           BackupDatabase();
           return true;
        }else if (id == R.id.mnDatabaseManager) {
            showDialogSuper();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showDialogSuper(){
        Bundle b=new Bundle();
        b.putString("CODE_KEY","");
        b.putString("TYPE_KEY","");
        b.putString("UFROM_KEY","mainactivity");
        DialogSuperUser dialogSuperUser=new DialogSuperUser();
        dialogSuperUser.setArguments(b);
        dialogSuperUser.show(getSupportFragmentManager(),"mTag");
    }
    public void openSettings(){
        Intent mainIntent = new Intent(MainActivity.this, Setting.class);
        startActivity(mainIntent);

    }



    public void fnchecksetting(){
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        String qSetting="select count(*)as numrows from tb_setting";
        Cursor rsSet=db.getQuery(qSetting);
        int numrows=0;
        while (rsSet.moveToNext()) {
           numrows=rsSet.getInt(0);
        }
        if(numrows==0) {

        }else{
            fncheckmodules();
        }

    }

    public void fncheckmodules(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qModule="select Modules from tb_setting";
            Cursor rsModule=db.getQuery(qModule);
            String lisModule="";
            while(rsModule.moveToNext()){
                lisModule=rsModule.getString(0);
            }
            rsModule.close();
            db.closeDB();
            String[] modules = lisModule.split(";");
            for (String add : modules) {
                showModule(add);
            }

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void showModule(String module){
        switch (module) {
            case "Stock Take":
                btnStockTake.setVisibility(View.VISIBLE);
                break;
            case "Issue Stock":
                btnIS.setVisibility(View.VISIBLE);
                break;
            case "Receipt Stock":
                btnRS.setVisibility(View.VISIBLE);
                break;
            case "Goods Receive Note":
                btnGRNDO.setVisibility(View.VISIBLE);
            case "Listing":
                 btnListing.setVisibility(View.VISIBLE);
                break;
            case "Transfer Stock":
                btnTS.setVisibility(View.VISIBLE);
                break;
            case "Sync":
                btnSync.setVisibility(View.VISIBLE);
                break;
            case "Stock Enquiry":
                btnStockEnquiry.setVisibility(View.VISIBLE);
                break;
            case "Print Prepack Label":
                btnPrintPrepack.setVisibility(View.VISIBLE);
                break;
            case "Print Prepack Label 2":
                btnPrintPrepack2.setVisibility(View.VISIBLE);
                break;
            case "Picking List":
                btnPickingList.setVisibility(View.VISIBLE);
                break;
            case "GRN Receiving":
                btnGRNReceiving.setVisibility(View.VISIBLE);
                break;
            case "Print Invoice Label":
                btnPrintInvLabel.setVisibility(View.VISIBLE);
                break;
            case "Loading Goods OUT":
                btnLoadingGoodsOUT.setVisibility(View.VISIBLE);
                break;
            case "Loading Goods IN":
                btnLoadingGoodsIN.setVisibility(View.VISIBLE);
                break;
            case "Invoice From Supplier":
                btnSupInv.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void initButton(){
        btnGRNReceiving.setVisibility(View.GONE);
        btnStockTake.setVisibility(View.GONE);
        btnIS.setVisibility(View.GONE);
        btnSync.setVisibility(View.GONE);
        btnRS.setVisibility(View.GONE);
        btnGRNDO.setVisibility(View.GONE);
        btnTS.setVisibility(View.GONE);
        btnStockEnquiry.setVisibility(View.GONE);
        btnPrintPrepack.setVisibility(View.GONE);
        btnPrintPrepack2.setVisibility(View.GONE);
        btnPickingList.setVisibility(View.GONE);
        btnListing.setVisibility(View.GONE);
        btnPrintInvLabel.setVisibility(View.GONE);
        btnLoadingGoodsOUT.setVisibility(View.GONE);
        btnLoadingGoodsIN.setVisibility(View.GONE);
        btnSupInv.setVisibility(View.GONE);
    }

    public static void enableStrictMode(Context context) {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .penaltyLog()
                        .build());
    }

    private void BackupDatabase(){
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/db_wms02";
                String backupDBPath = "and_mobile_wms02.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                Toast.makeText(this, "Backup Database Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
