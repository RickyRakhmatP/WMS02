package com.skybiz.wms02.ui_PrintPrepackLabel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.IpPrefix;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Print.By_USB.PrintingUSB;
import com.skybiz.wms02.m_Print.By_Wifi.PrintingWifiTsc;
import com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail.DownloaderListCS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PrintPrepackLabel extends AppCompatActivity {

    EditText txtDate,txtCusName,txtQty,txtAddress;
    Spinner spDoc1No;
    Button btnPrint;
    DatePickerDialog datePickerDialog;
    RecyclerView rvList;
    private GridLayoutManager lLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_prepack_label);
        txtDate=(EditText)findViewById(R.id.txtDate);
        txtCusName=(EditText)findViewById(R.id.txtCusName);
        txtQty=(EditText)findViewById(R.id.txtQty);
        txtAddress=(EditText)findViewById(R.id.txtAddress);
        spDoc1No=(Spinner) findViewById(R.id.spDoc1No);
        btnPrint=(Button) findViewById(R.id.btnPrint);
        rvList=(RecyclerView) findViewById(R.id.rvList);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
        spDoc1No.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Doc1No=parent.getItemAtPosition(position).toString();
                retList(Doc1No);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnPrint();
            }
        });
        initData();
    }

    private void fnPrint(){
        String Doc1No=spDoc1No.getSelectedItem().toString();
        String Qty=txtQty.getText().toString();
        String CusName=txtCusName.getText().toString();
        String Address=txtAddress.getText().toString();
        if(Qty.isEmpty()){
            Toast.makeText(this,"Qty Cannot Empty", Toast.LENGTH_SHORT).show();
        }else {
            SavePrepack savePrepack = new SavePrepack(this, Doc1No, Qty, CusName,Address);
            savePrepack.execute();
        }
    }

    private void initData(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String D_ate = sdf.format(date);
        txtDate.setText(D_ate);
        //RetDoc1No retDoc1No=new RetDoc1No()
    }
    private void retList(String Doc1No){
        //String Doc1No=spDoc1No.getSelectedItem().toString();
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        DownloaderListCS downloaderList=new DownloaderListCS(this,Doc1No,rvList);
        downloaderList.execute();
    }
    private void loadDoc1No(){
        String D_ate=txtDate.getText().toString();
        RetDoc1No retDoc1No=new RetDoc1No(this,D_ate);
        retDoc1No.execute();
    }
    public void setCusName(String CusName, String Address){
        txtCusName.setText(CusName);
        txtAddress.setText(Address);
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
                loadDoc1No();
            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }

    public class RetDoc1No extends AsyncTask<Void,Void,String> {
        Context c;
        String D_ate;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        ArrayList lsDoc1No=new ArrayList<String>();

        public RetDoc1No(Context c, String D_ate) {
            this.c = c;
            this.D_ate = D_ate;
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
            if(result.equals("success")){
                spDoc1No.setAdapter(new ArrayAdapter<String>(c,
                        android.R.layout.simple_spinner_dropdown_item,lsDoc1No));
            }else{
                Toast.makeText(c,"ret doc1no is empty", Toast.LENGTH_SHORT).show();
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
                String sql ="select Doc1No from stk_cus_inv_hd where D_ate='"+D_ate+"' ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {

                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        while (resultSet.next()) {
                            lsDoc1No.add(resultSet.getString(1));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsGroup=db.getQuery(sql);
                    int i=0;
                    while(rsGroup.moveToNext()){
                        lsDoc1No.add(rsGroup.getString(0));
                        i++;
                    }
                    if(i>0){
                        z="success";
                    }else{
                        z="error";
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
    }

    private void reset(){
        txtQty.getText().clear();
        txtCusName.getText().clear();
    }
    public void setPrint(String jsonData){
        try {
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qPrinter="select IPAddress,Port from tb_settingprinter";
            Cursor rsPrinter=db.getQuery(qPrinter);
            String Port="";
            String IPAddress="";
            while(rsPrinter.moveToNext()){
                IPAddress=rsPrinter.getString(0);
                Port=rsPrinter.getString(1);
            }
            reset();
            JSONArray ja = new JSONArray(jsonData);
            JSONObject jo = null;
            Handler handler1 = new Handler();
            for (int i = 0; i <= ja.length(); i++) {
                jo = ja.getJSONObject(i);
                final String ComName = jo.getString("ComName");
                final String CusName = jo.getString("CusName");
                final String Address = jo.getString("Address");
                final String PrepackFactor = jo.getString("PrepackFactor");
                final String PrepackID = jo.getString("PrepackID");
                final String vPort = Port;
                final String vIPAddress = IPAddress;
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // PrintingUSB print = new PrintingUSB();
                       // Boolean isBT = print.printTSC(PrintPrepackLabel.this, ComName, CusName, Address, PrepackFactor, PrepackID);
                        PrintingWifiTsc print = new PrintingWifiTsc();
                        Boolean isBT = print.printwifiTsc2(PrintPrepackLabel.this, vIPAddress,vPort,ComName, CusName,
                                Address, PrepackFactor, PrepackID);
                        if (isBT == false) {
                            Log.d("Error", "Printing");
                        }
                    }
                }, 1000 * i);
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
