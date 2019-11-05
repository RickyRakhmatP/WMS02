package com.skybiz.wms02.ui_SupInv;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_DataObject.SetUOM;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Location.DialogLoc;
import com.skybiz.wms02.m_Supplier.DialogSupplier;
import com.skybiz.wms02.ui_SupInv.m_List.DownloaderList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SupplierInvoice extends AppCompatActivity {


    DatePickerDialog datePickerDialog;
    EditText txtDoc1No,txtItemCode,txtItemBatch,txtWarrantyDate,
    txtQty,txtHCUnitCost,txtHCLineAmt;
    TextView txtDescription,txtUOM,txtFactorQty;
    RecyclerView rvList;
    Button btnBack,btnSave,btnAddItem,btnPlus,btnMinus;
    boolean doubleBackToExitPressedOnce = false;
    private GridLayoutManager lLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_invoice);
        getSupportActionBar().setTitle("Invoice From Supplier");
        txtDoc1No       =(EditText)findViewById(R.id.txtDoc1No);
        txtItemCode     =(EditText)findViewById(R.id.txtItemCode);
        txtItemBatch    =(EditText)findViewById(R.id.txtItemBatch);
        txtWarrantyDate =(EditText)findViewById(R.id.txtWarrantyDate);
        txtQty          =(EditText)findViewById(R.id.vQty);
        txtHCUnitCost   =(EditText)findViewById(R.id.txtHCUnitCost);
        txtHCLineAmt    =(EditText)findViewById(R.id.txtHCLineAmt);
        txtDescription  =(TextView)findViewById(R.id.txtDescription);
        txtUOM          =(TextView)findViewById(R.id.txtUOM);
        txtFactorQty    =(TextView)findViewById(R.id.txtFactorQty);
        rvList          =(RecyclerView)findViewById(R.id.rvList);
        btnAddItem      =(Button)findViewById(R.id.btnAddItem);
        btnBack         =(Button)findViewById(R.id.btnBack);
        btnSave         =(Button)findViewById(R.id.btnSave);
        btnPlus         =(Button)findViewById(R.id.btnPlus);
        btnMinus        =(Button)findViewById(R.id.btnMinus);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmexit();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnsave();
            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnadditem();
            }
        });
        txtItemCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String ItemCode=txtItemCode.getText().toString();
                    if(!ItemCode.isEmpty()){
                        retItem();
                    }else{
                        txtItemCode.requestFocus();
                    }
                }
                return false;
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnplusqty();
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnminqty();
            }
        });
        initData();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supinv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnLocation) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","SupInv");
            b.putString("DBSTATUS_KEY","1");
            DialogLoc dialogLoc = new DialogLoc();
            dialogLoc.setArguments(b);
            dialogLoc.show(getSupportFragmentManager(), "mLocation");
            return true;
        }else if(id==R.id.mnSupplier){
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","SupInv");
            b.putString("DBSTATUS_KEY","1");
            DialogSupplier dialogCustomer = new DialogSupplier();
            dialogCustomer.setArguments(b);
            dialogCustomer.show(getSupportFragmentManager(), "List Supplier");
            return true;
        }else if(id==R.id.mnDate){
            openDate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(){
        delAll();
        txtItemBatch.setText(fngetitembatch());
        initHeader();
    }
    private void initHeader(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qSet="select LocationCode from tb_setting";
            Cursor rsSet=db.getQuery(qSet);
            String LocationCode="";
            while(rsSet.moveToNext()){
                LocationCode=rsSet.getString(0);
            }

            String D_ate=fngetdate();
            setHeader(LocationCode,"","",D_ate);
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnplusqty(){
        String Qty=txtQty.getText().toString();
        int iQty=Integer.parseInt(Qty);
        int newQty=iQty+1;
        txtQty.setText(String.valueOf(newQty));
        fncalculatelineamt();
    }
    private void fnminqty(){
        String Qty  =txtQty.getText().toString();
        int iQty    =Integer.parseInt(Qty);
        if(iQty>=1) {
            int newQty = iQty - 1;
            txtQty.setText(String.valueOf(newQty));
        }
        fncalculatelineamt();
    }
    public void activeSubmit(){
        btnSave.setEnabled(true);
    }
    private void afterSave(){
        btnSave.setEnabled(true);
        txtDoc1No.getText().clear();
        txtItemCode.getText().clear();
        txtDoc1No.requestFocus();
        delAll();
        loadlist();
        initHeader();
    }
    private void fnsave(){
        try{
            btnSave.setEnabled(false);
            String Doc1No       =txtDoc1No.getText().toString();
            String LocationCode ="";
            String CusCode      ="";
            String D_ate        ="";
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String sql="select LocationCode, CusCode, D_ate from cloud_sup_inv_hd ";
            Cursor rsH=db.getQuery(sql);
            while(rsH.moveToNext()) {
                LocationCode    =rsH.getString(0);
                CusCode         =rsH.getString(1);
                D_ate           =rsH.getString(2);
            }
            if(D_ate.isEmpty()){
                D_ate=fngetdate();
            }
            if(Doc1No.isEmpty()){
                Toast.makeText(this, "Invoice # Cannot Empty", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
            }else if(LocationCode.isEmpty()){
                Toast.makeText(this, "Location Cannot Empty", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
            }else if(CusCode.isEmpty()){
                Toast.makeText(this, "Supplier Cannot Empty", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
            }else{
                SaveSupInv saveSupInv=new SaveSupInv(this,Doc1No,CusCode,LocationCode,D_ate);
                saveSupInv.execute();
            }
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnadditem(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String ItemCode=txtItemCode.getText().toString();
            String HCUnitCost=txtHCUnitCost.getText().toString();
            String Qty=txtQty.getText().toString();
            String UOM=txtUOM.getText().toString();
            String FactorQty=txtFactorQty.getText().toString();
            String ItemBatch=txtItemBatch.getText().toString();
            String WarrantyDate=txtWarrantyDate.getText().toString();
            String Doc1No=txtDoc1No.getText().toString();
            String Description=txtDescription.getText().toString();
            String HCLineAmt=txtHCLineAmt.getText().toString();
            if(ItemCode.isEmpty()){
                Toast.makeText(this, "Item Code Cannot Empty", Toast.LENGTH_SHORT).show();
            }else if(HCUnitCost.isEmpty()){
                Toast.makeText(this, "HCUnitCost Cannot Empty", Toast.LENGTH_SHORT).show();
            }else if(HCLineAmt.isEmpty()){
                Toast.makeText(this, "HCLineAmt Cannot Empty", Toast.LENGTH_SHORT).show();
            }else{
                String qInsert="insert into cloud_sup_inv_dt(Doc1No, ItemCode, Description , " +
                        " Qty, FactorQty, UOM, " +
                        " UOMSingular, HCUnitCost, DisRate1, " +
                        " HCDiscount, TaxRate1, DetailTaxCode , " +
                        " HCTax, HCLineAmt, DepartmentCode, " +
                        " BranchCode, ProjectCode, LocationCode, " +
                        " ItemBatch, WarrantyDate)values('"+Doc1No+"', '"+ItemCode+"', '"+Description+"'," +
                        " '"+Qty+"', '"+FactorQty+"', '"+UOM+"'," +
                        " '"+UOM+"', '"+HCUnitCost+"', '0'," +
                        " '0', '0', ''," +
                        " '0', '"+HCLineAmt+"', ''," +
                        " '', '', ''," +
                        " '"+ItemBatch+"', '"+WarrantyDate+"')";
                Log.d("INSERT",qInsert);
                db.exeQuery(qInsert);
                loadlist();
                fnreset();
                txtItemCode.getText().clear();
                txtItemCode.requestFocus();
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
        DownloaderList dList=new DownloaderList(this,rvList);
        dList.execute();
    }
    private void fnreset(){
        txtItemCode.getText().clear();
        txtDescription.setText("Description");
        txtHCLineAmt.getText().clear();
        txtHCUnitCost.getText().clear();
        txtUOM.setText("");
        txtFactorQty.setText("1");
        txtWarrantyDate.getText().clear();
        txtQty.setText("1");
    }
    public void delAll(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String vDel="delete from cloud_sup_inv_dt ";
            db.exeQuery(vDel);
            String vDel2="delete from cloud_sup_inv_hd ";
            db.exeQuery(vDel2);
            loadlist();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void delRow(String RunNo){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String vDel="delete from cloud_sup_inv_dt where RunNo='"+RunNo+"' ";
            db.exeQuery(vDel);
            loadlist();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void setHeader(String LocationCode, String CusCode, String CusName, String D_ate ){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            int numrows=0;
            String qCheck="select count(*)as numrows from cloud_sup_inv_hd ";
            Cursor rsCheck=db.getQuery(qCheck);
            while(rsCheck.moveToNext()){
                numrows=rsCheck.getInt(0);
            }

            if(numrows==0){
                String qInsert="insert into cloud_sup_inv_hd (LocationCode,CusCode," +
                        "CusName,D_ate)values('"+LocationCode+"', '"+CusCode+"'," +
                        "'"+CusName+"', '"+D_ate+"')";
                db.exeQuery(qInsert);
            }else{
                if(!CusCode.isEmpty()){
                    String qUpdate="update cloud_sup_inv_hd set CusCode='"+CusCode+"', " +
                            "CusName='"+CusName+"' ";
                    db.exeQuery(qUpdate);
                }
                if(!D_ate.isEmpty()){
                    String qUpdate="update cloud_sup_inv_hd set D_ate='"+D_ate+"' ";
                    db.exeQuery(qUpdate);
                }
                if(!LocationCode.isEmpty()){
                    String qUpdate="update cloud_sup_inv_hd set LocationCode='"+LocationCode+"' ";
                    db.exeQuery(qUpdate);
                }

            }

            Log.d("Customer", CusCode + CusName);
            String sql="select LocationCode, CusCode, CusName, D_ate from cloud_sup_inv_hd ";
            Cursor rsH=db.getQuery(sql);
            while(rsH.moveToNext()) {
                String vLocCode=rsH.getString(0);
                String vCusCode=rsH.getString(1);
                String vCusName=rsH.getString(2);
                String vD_ate=rsH.getString(3);
                getSupportActionBar().setTitle("IFS | " + vLocCode + " | " + vD_ate);
                getSupportActionBar().setSubtitle(vCusCode + " | " + vCusName);
            }

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }
    private void openDate(){
        final Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        final int mMonth=c.get(Calendar.MONTH);
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        final DecimalFormat mFormat= new DecimalFormat("00");
        datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                final Double dMonth=(monthOfYear+1)*1.00;
                final Double dDay=dayOfMonth*1.00;
                final String D_ate=year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay);
                setHeader("","","",D_ate);
            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }
    private void fncalculatelineamt(){
        Double HCUnitCost=Double.parseDouble(txtHCUnitCost.getText().toString().replaceAll(",",""));
        Double Qty       =Double.parseDouble(txtQty.getText().toString());
        Double dHCLineAmt=HCUnitCost*Qty;
        String HCLineAmt =String.format(Locale.US, "%,.2f", dHCLineAmt);
        txtHCLineAmt.setText(HCLineAmt);
    }
    private void retItem(){
        String ItemCode=txtItemCode.getText().toString();
        if(ItemCode.isEmpty()){

        }else{
            SetItem setItem=new SetItem(this, ItemCode);
            setItem.execute();
        }
    }

    private class SetItem extends AsyncTask<Void,Void, String> {
        Context c;
        String ItemCode,CurrDate;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        String Description="",UnitPrice="0.00",UOM,FactorQty,
                TaxCode,SalesTaxCode,RetailTaxCode,PurchaseTaxCode,
                AlternateItem,SuspendedYN,UOM1,
                UOM2,UOM3,UOM4,
                UOMFactor1,UOMFactor2,UOMFactor3,
                UOMFactor4,DefUOM,DefaultUOM,vItemCode,
                UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,WarrantyDay="0",WarrantyDate;
        DBAdapter db=null;
        int numrows=0;

        public SetItem(Context c, String itemCode) {
            this.c = c;
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
                if(numrows>0) {
                    txtItemCode.setText(ItemCode);
                    txtDescription.setText(Description);
                    txtWarrantyDate.setText(WarrantyDate);
                    txtUOM.setText(UOM);
                    txtFactorQty.setText(FactorQty);
                    txtHCUnitCost.setText(UnitPrice);
                    txtQty.requestFocus();
                    fncalculatelineamt();
                }else{
                    Toast.makeText(c, "Item Not Found", Toast.LENGTH_SHORT).show();
                }
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                CurrDate = sdf.format(date);

                String sql="select  D_ate from cloud_sup_inv_hd ";
                Cursor rsH=db.getQuery(sql);
                while(rsH.moveToNext()) {
                    CurrDate=rsH.getString(0);
                }

                sql= "select ItemCode, Description, ItemGroup, UnitPrice," +
                        " UnitCost, DefaultUOM, UOM, IFNULL(UOM1,'') as UOM1, " +
                        " IFNULL(UOM2,'') as UOM2, IFNULL(UOM3,'') as UOM3, IFNULL(UOM4,'') as UOM4, IFNULL(UOMPrice1,0) as UOMPrice1," +
                        " IFNULL(UOMPrice2,0) as UOMPrice2,  IFNULL(UOMPrice3,0)as UOMPrice3, IFNULL(UOMPrice4,0)as UOMPrice4, IFNULL(UOMFactor1,0)as UOMFactor1," +
                        " IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4, AnalysisCode1," +
                        " AnalysisCode2, AnalysisCode3, AnalysisCode4, AnalysisCode5," +
                        " MAXSP, MAXSP1, MAXSP2, MAXSP3," +
                        " MAXSP4, SalesTaxCode, RetailTaxCode, PurchaseTaxCode," +
                        " FixedPriceYN, SuspendedYN, AlternateItem, WarrantyDay " +
                        " from stk_master where SuspendedYN='0' and ItemCode='"+ItemCode+"' ";
                Log.d("QUERY",sql);

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
                        numrows++;
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
                        UOM         = vData.getUOM();
                        UnitPrice   = vData.getUnitPrice();
                        FactorQty   = vData.getFactorQty();
                        Description = charReplace(Encode.setChar(CharType,rsItem.getString(2)));
                        WarrantyDay = rsItem.getString(36);
                    }

                    if(WarrantyDay.equals("0") && numrows>0){
                        WarrantyDate=fnchangedate(CurrDate,365);
                    }else{
                        WarrantyDate=fnchangedate(CurrDate,Integer.parseInt(WarrantyDay));
                    }

                    z="success";
                } else {
                    z="error";
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

    private class SaveSupInv extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No,CusCode,LocationCode,D_ate;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        public SaveSupInv(Context c, String doc1No, String cusCode, String locationCode, String d_ate) {
            this.c = c;
            Doc1No = doc1No;
            CusCode = cusCode;
            LocationCode = locationCode;
            D_ate = d_ate;
        }
        @Override
        protected String doInBackground(Void... voids) {
            return this.fnsavesupinv();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c, "Sucessful, Save Data Invoice From Supplier ", Toast.LENGTH_SHORT).show();
                afterSave();
            }else if(result.equals("error")){
                Toast.makeText(c, "Error", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
            }else if(result.equals("duplicate")){
                Toast.makeText(c, "Duplicate Invoice #", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
            }
        }

        private String fnsavesupinv(){
            try{
                z="error";
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName, UserName, Password," +
                        "Port, DBName, DBStatus, " +
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
                int numrows=0;
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                Double dHCNetAmt=0.00;
                if (conn != null) {

                    String qCheck="select count(*)as numrows from stk_sup_inv_hd where Doc1No='"+Doc1No+"' ";
                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                       numrows=rsCheck.getInt(1);
                    }
                    if(numrows==0) {
                        String sql = "select '" + Doc1No + "', ItemCode, Description ," +
                                " Qty, FactorQty, UOM, " +
                                " UOMSingular, HCUnitCost, DisRate1, " +
                                " HCDiscount, TaxRate1, DetailTaxCode, " +
                                " HCTax, HCLineAmt, DepartmentCode, " +
                                " BranchCode, ProjectCode, LocationCode, " +
                                " ItemBatch, WarrantyDate , 'SupInv' as DocType from  cloud_sup_inv_dt ";

                        Cursor rsDt = db.getQuery(sql);
                        while (rsDt.moveToNext()) {
                            dHCNetAmt += rsDt.getDouble(13);
                            String qInsertDt = "insert into stk_sup_inv_dt (Doc1No, ItemCode, Description," +
                                    " Qty, FactorQty, UOM, " +
                                    " UOMSingular, HCUnitCost, DisRate1, " +
                                    " HCDiscount, TaxRate1, DetailTaxCode, " +
                                    " HCTax, HCLineAmt, DepartmentCode, " +
                                    " BranchCode, ProjectCode, LocationCode, " +
                                    " ItemBatch, WarrantyDate, DocType)values('" + rsDt.getString(0) + "', '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "'," +
                                    " '" + rsDt.getString(3) + "', '" + rsDt.getString(4) + "', '" + rsDt.getString(5) + "'," +
                                    " '" + rsDt.getString(6) + "', '" + rsDt.getString(7) + "', '" + rsDt.getString(8) + "'," +
                                    " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "'," +
                                    " '" + rsDt.getString(12) + "', '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "'," +
                                    " '" + rsDt.getString(15) + "', '" + rsDt.getString(16) + "', '" + LocationCode + "'," +
                                    " '" + rsDt.getString(18) + "', '" + rsDt.getString(19) + "', '" + rsDt.getString(20) + "')";
                           Log.d("Detail", qInsertDt);
                            Statement stmtIn = conn.createStatement();
                            stmtIn.execute(qInsertDt);

                            String qInsertDtTrn = "INSERT INTO stk_detail_trn_in (ItemCode, Doc1No, Doc2No, " +
                                    "Doc3No, D_ate, BookDate, " +
                                    "ExpireDate, QtyIN, FactorQty, " +
                                    "UOM, UnitCost, MovingAveCost, " +
                                    "LandingCost, OCCost, HCTax, " +
                                    "CusCode, DocType1, DocType2, " +
                                    "DocType3, Doc1NoRunNo, Doc2NoRunNo, " +
                                    "Doc3NoRunNo, LocationCode, ItemBatch, " +
                                    "GlobalTaxCode, DetailTaxCode)values('"+rsDt.getString(1)+"', '', ''," +
                                    "'"+rsDt.getString(0)+"', '"+D_ate+"', '"+D_ate+"', " +
                                    "'"+rsDt.getString(19)+"', '"+rsDt.getString(3)+"', '"+rsDt.getString(4)+"'," +
                                    "'"+rsDt.getString(5)+"', '"+rsDt.getString(7)+"', '0'," +
                                    "'0', '0', '0'," +
                                    "'"+CusCode+"', '', ''," +
                                    "'SupInv', '0', '0'," +
                                    "'0', '"+LocationCode+"', '"+rsDt.getString(18)+"'," +
                                    "'','')";
                            Log.d("Detail2", qInsertDtTrn);
                            Statement stmtTrnIn = conn.createStatement();
                            stmtTrnIn.execute(qInsertDtTrn);


                        }
                        String qInsertHd = "INSERT INTO stk_sup_inv_hd (Doc1No, Doc2No, Doc3No, " +
                                " D_ate, CusCode, DueDate, " +
                                " TaxDate, CurCode, CurRate1, " +
                                " CurRate2, CurRate3, PermitNo, " +
                                " PermitDate, TermCode, D_ay, " +
                                " Attention, BatchCode, JournalDescription, " +
                                " GbDisRate1, GbDisRate2, GbDisRate3," +
                                " HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                                " GbTaxRate3, HCGbTax, GlobalTaxCode, " +
                                " HCDtTax, HCNetAmt, GbOCCode, " +
                                " GbOCRate, GbOCAmt, GbOCAccountCode, " +
                                " GbOCFinCatCode, GbOCCode2, GbOCRate2, " +
                                " GbOCAmt2, GbOCAccountCode2, GbOCFinCatCode2, " +
                                " GbPTPAccountCode, GbPTEAccountCode, GbPTPFinCatCode, " +
                                " GbPTEFinCatCode, PurchaseGbDisAccountCode, PurchaseGbDisFinCatCode, " +
                                " DepositNo, Deposit, DepositAccountCode, " +
                                " DocType, ApprovedYN, PostedYN, " +
                                " UDRunNo, L_ink) VALUES ('" + Doc1No + "', '', '', " +
                                " '" + D_ate + "', '" + CusCode + "', '" + D_ate + "'," +
                                " '" + D_ate + "', '', '1', " +
                                " 1, 1, '', " +
                                " '" + D_ate + "', '1', '0', " +
                                " '', '', '', " +
                                " '0', 0, 0," +
                                " 0, 0, 0, " +
                                " 0, '0',''," +
                                " '0', '" + dHCNetAmt + "', ''," +
                                " 0, 0, '', " +
                                " '', '', 0," +
                                " 0, '', '', " +
                                " '', '', ''," +
                                " '', '', '', " +
                                " '', 0, '', " +
                                " 'SupInv', '1', '1'," +
                                "  0, '1') ";
                        Log.d("Header", qInsertHd);
                        Statement stmtHd = conn.createStatement();
                        stmtHd.execute(qInsertHd);
                        z = "success";
                    }else{
                        z="duplicate";
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

    private String fnchangedate(String D_ate, int calDay){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(D_ate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, calDay);
        Date resultdate = new Date(c.getTimeInMillis());
        String olDate=sdf.format(resultdate);
        return olDate;
    }

    private String fngetitembatch(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String curentDate = sdf.format(date);
        return curentDate;
    }
    private String fngetdate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String curentDate = sdf.format(date);
        return curentDate;
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
    private void confirmexit(){
        AlertDialog alertDialog = new AlertDialog.Builder(SupplierInvoice.this).create();
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Are you sure want to exit module ?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        fnexit();
                    }
                });
        alertDialog.show();
    }

    private void fnexit(){
        finish();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
    }

}
