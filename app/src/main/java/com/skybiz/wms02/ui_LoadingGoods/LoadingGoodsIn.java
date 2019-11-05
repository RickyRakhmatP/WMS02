package com.skybiz.wms02.ui_LoadingGoods;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.ui_LoadingGoods.m_ListDum.DownloadDum;
import com.skybiz.wms02.ui_LoadingGoods.m_ListLoading.DialogLoading;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoadingGoodsIn extends AppCompatActivity {

    EditText txtRunNoHd,txtInvoiceNo,txtReason,txtDate,txtHeader;
    CheckBox chkDeliveredYN;
    //Spinner spReason;
    Button btnRefHeader,btnBack,btnClear,
            btnSave,btnRefReason;
    RecyclerView rvList;
    ListView lv;
    TextView txtLGODate;
    Boolean deliveryn=false;
    DatePickerDialog datePickerDialog;
    private GridLayoutManager lLayout;
    boolean doubleBackToExitPressedOnce = false;
    ImageView imgDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_goods_in);
        getSupportActionBar().setTitle("Loading Goods Out - Verify");
        txtRunNoHd      =(EditText)findViewById(R.id.txtRunNoHd);
        txtInvoiceNo    =(EditText)findViewById(R.id.txtInvoiceNo);
        txtReason       =(EditText) findViewById(R.id.txtReason);
        txtDate         =(EditText) findViewById(R.id.txtDate);
        txtHeader       =(EditText) findViewById(R.id.txtHeader);
        btnRefHeader    =(Button)findViewById(R.id.btnRefHeader);
        btnRefReason    =(Button)findViewById(R.id.btnRefReason);
        btnBack         =(Button)findViewById(R.id.btnBack);
        btnClear        =(Button)findViewById(R.id.btnClear);
        btnSave         =(Button)findViewById(R.id.btnSave);
        rvList          =(RecyclerView)findViewById(R.id.rvList);
        chkDeliveredYN  =(CheckBox)findViewById(R.id.chkDeliveredYN);
        txtLGODate      =(TextView)findViewById(R.id.txtLGODate);
        imgDate         =(ImageView)findViewById(R.id.imgDate);

        txtInvoiceNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String InvoiceNo=txtInvoiceNo.getText().toString();
                    if(!InvoiceNo.isEmpty()){
                        addInvoice();
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
                Intent mainIntent = new Intent(LoadingGoodsIn.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIN();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInvoiceNo.getText().clear();
                txtInvoiceNo.requestFocus();
            }
        });
        btnRefReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReason();
            }
        });
        btnRefHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHeader();
            }
        });
        txtLGODate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
        chkDeliveredYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryn=true;
                }else{
                    deliveryn=false;
                }
            }
        });
        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
        initData();
    }
    private void initData(){
        resetDum();
        txtDate.setText(newDated());
        txtInvoiceNo.requestFocus();
        //loadReason();
    }
    private String newDated(){
        String newDated="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        newDated = sdf.format(date);
        return newDated;
    }
    private void addInvoice(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String Reason       ="";
            String DeliveredYN  ="0";
            if(deliveryn){
                DeliveredYN     ="1";
            }else{
                Reason          =txtReason.getText().toString();
                DeliveredYN     ="0";
            }
            String InvoiceNo = txtInvoiceNo.getText().toString();
            if(deliveryn==false && Reason.isEmpty()) {
                Toast.makeText(this, "Reason Cannot Empty", Toast.LENGTH_SHORT).show();
            }else if(InvoiceNo.equals("")){
                Toast.makeText(this, "Invoice No Cannot Empty", Toast.LENGTH_SHORT).show();
            }else{

                String qInsertDum = "insert into tb_dumlist(Doc1No,Qty,Remark,DeliveredYN)values('" + InvoiceNo + "', '1', '" + Reason + "','" + DeliveredYN + "')";
                long add = db.exeQuery(qInsertDum);
                if (add > 0) {
                    loadlist();
                    txtInvoiceNo.getText().clear();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtInvoiceNo.requestFocus();
                        }
                    }, 300);
                } else {
                    Toast.makeText(this, "Error, Add Invoice No", Toast.LENGTH_SHORT).show();
                }
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
        DownloadDum dList=new DownloadDum(this,"IN",rvList);
        dList.execute();
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
    private void saveIN(){
        btnSave.setEnabled(false);
        String RunNo=txtRunNoHd.getText().toString();
        if(RunNo.isEmpty()){
            Toast.makeText(this, "Please, Select Loading", Toast.LENGTH_SHORT).show();
        }else {
            SaveLoadingIN fnsave = new SaveLoadingIN(this, RunNo);
            fnsave.execute();
        }
    }
    private void afterSave(){
        btnSave.setEnabled(true);
        resetDum();
        loadlist();
        txtInvoiceNo.requestFocus();
    }
    public void setLoading(String RunNo,String CheckerCode,String D_ateTime){
        txtRunNoHd.setText(RunNo);
        txtHeader.setText(CheckerCode+" | "+D_ateTime);
    }
    private void loadHeader(){
        String D_ate=txtDate.getText().toString();
        Bundle b=new Bundle();
        b.putString("DATE_KEY",D_ate);
        DialogLoading dialogLoading = new DialogLoading();
        dialogLoading.setArguments(b);
        dialogLoading.show(getSupportFragmentManager(), "mList Loading");
    }
    private void loadDate(){
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
                txtDate.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }
    private void loadReason(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        LayoutInflater inflater =this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reason, null);
        builderSingle.setView(dialogView);
        lv = (ListView) dialogView.findViewById(R.id.lsReason);
        final String[] rs = getResources().getStringArray(
                R.array.reason_array);
        CustomAdapter adapter = new CustomAdapter(this,rs);
        lv.setAdapter(adapter);
        final AlertDialog alertDialog = builderSingle.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                String  Reason        =rs[position];
                txtReason.setText(Reason);
                addInvoice();
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }


    private class SaveLoadingIN extends AsyncTask<Void,Void,String>{
        Context c;
        String RunNo_Hd;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public SaveLoadingIN(Context c, String RunNo_Hd) {
            this.c = c;
            this.RunNo_Hd = RunNo_Hd;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnsavein();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c, "Succesful, Save Loading Goods In", Toast.LENGTH_SHORT).show();
                afterSave();
            }else if(result.equals("error")){
                Toast.makeText(c, "Failure, Save Loading Goods In", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnsavein(){
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
                    String qDumAll = "select RunNo, Doc1No, DeliveredYN, Remark from tb_dumlist Order By RunNo ";
                    Cursor rsDum = db.getQuery(qDumAll);
                    while (rsDum.moveToNext()) {
                        String RunNo        = rsDum.getString(0);
                        String Doc1No       = rsDum.getString(1);
                        String DeliveredYN  = rsDum.getString(2);
                        String Reason       = rsDum.getString(3);
                        String qUpdateDt = "update wms_loading_dt set DeliveredYN='"+DeliveredYN+"', " +
                                "Reason='"+Reason+"' where Doc1NoOUT='"+Doc1No+"' and RunNo_Hd='"+RunNo_Hd+"'  ";
                        Statement stmtDt =conn.createStatement();
                        stmtDt.execute(qUpdateDt);
                        String qDel="delete from tb_dumlist where RunNo='"+RunNo+"' ";
                        db.exeQuery(qDel);
                    }

                    String qUpdateHd="update wms_loading_hd set D_ateTimeIN='"+D_ateTime+"' where RunNo='"+RunNo_Hd+"' ";
                    Statement stmtHd =conn.createStatement();
                    stmtHd.execute(qUpdateHd);

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
