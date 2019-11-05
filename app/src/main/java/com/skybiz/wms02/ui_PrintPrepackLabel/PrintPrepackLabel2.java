package com.skybiz.wms02.ui_PrintPrepackLabel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skybiz.wms02.BuildConfig;
import com.skybiz.wms02.MyKeyboard;
import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Detail;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Print.By_Wifi.PrintingWifiTsc;
import com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail.DownloaderListCS;
import com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail.DownloaderListCS2;
import com.skybiz.wms02.ui_PrintPrepackLabel.m_ListDetail.ListCSAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrintPrepackLabel2 extends AppCompatActivity {


    EditText txtCusName,txtQty,txtAddress,
            txtDoc1No,txtScanDoc1No,txtD_ate;
    Button btnPrint,btnClear;
    DatePickerDialog datePickerDialog;
    RecyclerView rvList;
    private GridLayoutManager lLayout;
    MyKeyboard keyboard;
    CheckBox chkScanYN;

    ListCSAdapter adapter;
    ArrayList<Spacecraft_Detail> spacecrafts=new ArrayList<>();

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //enableStrictMode(this);
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
        setContentView(R.layout.activity_print_prepack_label2);
        getSupportActionBar().setTitle("Print Prepack Label 2");
        txtCusName=(EditText)findViewById(R.id.txtCusName);
        txtQty=(EditText)findViewById(R.id.txtQty);
        txtAddress=(EditText)findViewById(R.id.txtAddress);
        txtDoc1No=(EditText) findViewById(R.id.txtDoc1No);
        btnPrint=(Button) findViewById(R.id.btnPrint);
        rvList=(RecyclerView) findViewById(R.id.rvList);
        txtScanDoc1No=(EditText)findViewById(R.id.txtScanDoc1No);
        txtD_ate=(EditText)findViewById(R.id.txtD_ate);
        btnClear=(Button)findViewById(R.id.btnClear);
        chkScanYN=(CheckBox)findViewById(R.id.chkScanYN);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnPrint();
            }
        });
        keyboard=(MyKeyboard)findViewById(R.id.keyboard);
        txtDoc1No.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String Doc1No=     txtDoc1No.getText().toString();
                    retList(Doc1No);
                    return true;
                }
                return false;
            }
        });
        txtScanDoc1No.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String Doc1No=     txtScanDoc1No.getText().toString();
                    txtDoc1No.setText(Doc1No);
                    retList(Doc1No);
                    afterScan();
                    return true;
                }
                return false;
            }
        });


        txtQty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtQty.setInputType(0);
                txtQty.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtQty.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic2);
                return false;
            }
        });

        chkScanYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtScanDoc1No.requestFocus();
                }else{
                    txtDoc1No.requestFocus();
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDoc1No.getText().clear();
                txtDoc1No.requestFocus();
            }
        });
        initView();

    }
    private void afterScan(){
        txtScanDoc1No.getText().clear();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtQty.requestFocus();
            }
        }, 600);
    }

    private  void initView(){
        InputConnection ic=txtQty.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtQty.setTextIsSelectable(true);
        txtQty.setInputType(0);

    }
    private void fnPrint(){
        String Doc1No=txtDoc1No.getText().toString();
        String Qty=txtQty.getText().toString();
        String CusName=txtCusName.getText().toString();
        String Address=charReplace(txtAddress.getText().toString().trim());
        String D_ate=txtD_ate.getText().toString();
        if(Qty.isEmpty()){
            Toast.makeText(this,"Qty Cannot Empty", Toast.LENGTH_SHORT).show();
        }else {
            SavePrepack2 savePrepack = new SavePrepack2(this, Doc1No, Qty, CusName,Address, D_ate);
            savePrepack.execute();
        }
    }
    private void retList(String Doc1No){
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        DownloaderListCS2 downloaderList=new DownloaderListCS2(this,Doc1No,rvList);
        downloaderList.execute();
    }

    public void setCusName(String CusName, String Address, String D_ate){

            txtCusName.setText(CusName);
            txtAddress.setText(Address);
            txtD_ate.setText(D_ate);

    }

    private void reset(){
        spacecrafts.clear();
        txtDoc1No.getText().clear();
        txtQty.getText().clear();
        txtCusName.getText().clear();
        txtScanDoc1No.requestFocus();
        //refreshNextAct();
        //retList("");
        adapter=new ListCSAdapter(this,spacecrafts);
        rvList.setAdapter(adapter);
         ///adapter.clear();

    }
    public void setPrint(Context c, String jsonData){
        String CusName1="",CusName2="",CusName3="",Address1="",Address2="",Address3="",Address4="";
        Cursor rsPrinter=null;
        Cursor rsDum=null;
        DBAdapter db=null;
        try {
            db=new DBAdapter(this);
            db.openDB();
            String qPrinter="select IPAddress,Port from tb_settingprinter";
            rsPrinter=db.getQuery(qPrinter);
            String Port="";
            String IPAddress="";
            while(rsPrinter.moveToNext()){
                IPAddress=rsPrinter.getString(0);
                Port=rsPrinter.getString(1);
            }
          //  rsPrinter.close();
           // db.closeDB();
           // db.openDB();
            String queryDum="select CusName1,CusName2,CusName3," +
                    "Address1,Address2,Address3," +
                    "Address4 from tb_dumprint2 limit 1";
            rsDum=db.getQuery(queryDum);
            while(rsDum.moveToNext()){
                CusName1=rsDum.getString(0);
                CusName2=rsDum.getString(1);
                CusName3=rsDum.getString(2);
                Address1=rsDum.getString(3);
                Address2=rsDum.getString(4);
                Address3=rsDum.getString(5);
                Address4=rsDum.getString(6);
            }
           // rsDum.close();
           // db.closeDB();
            reset();
            JSONArray ja = new JSONArray(jsonData);
            JSONObject jo ;
            Handler handler1 = new Handler();
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                final String Doc1No = jo.getString("Doc1No");
                final String CusName = jo.getString("CusName");
                final String vAddress = jo.getString("Address");
                Log.d("Address",vAddress);
                final String PrepackFactor = jo.getString("PrepackFactor");
                final String D_ate = jo.getString("D_ate");
                final String vPort = Port;
                final String vIPAddress = IPAddress;
                final String finalCusName = CusName2;
                final String finalCusName1 = CusName1;
                final String finalAddress = Address1;
                final String finalAddress1 = Address2;
                final String finalAddress2 = Address3;
                final String finalAddress3 = Address4;
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrintingWifiTsc print = new PrintingWifiTsc();
                        Boolean isBT = print.printwifiTsc3(PrintPrepackLabel2.this, vIPAddress,vPort, Doc1No, CusName,
                                vAddress, PrepackFactor, D_ate, finalCusName1, finalCusName, finalAddress, finalAddress1, finalAddress2, finalAddress3);
                        if (isBT == false) {
                            Log.d("Error", "Printing");
                        }
                    }
                }, 1500 * i);
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rsPrinter.close();
            rsDum.close();
            db.closeDB();
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

    private void refreshNextAct(){
        finish();
        Intent mainIntent = new Intent(PrintPrepackLabel2.this, PrintPrepackLabel2.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    public String str_pad(String input, int length, String pad, String    sense) {
        int resto_pad = length - input.length();
        String padded = "";
        if (resto_pad <= 0){ return input; }

        if(sense.equals("STR_PAD_RIGHT")) {
            padded  = input;
            padded += _fill_string(pad,resto_pad);
        } else if(sense.equals("STR_PAD_LEFT")) {
            padded  = _fill_string(pad, resto_pad);
            padded += input;
        } else {
            int pad_left  = (int) Math.ceil(resto_pad/2);
            int pad_right = resto_pad - pad_left;

            padded  = _fill_string(pad, pad_left);
            padded += input;
            padded += _fill_string(pad, pad_right);
        }
        return padded;
    }

    protected String _fill_string(String pad, int resto ) {
        boolean first = true;
        String padded = "";
        if (resto >= pad.length()) {
            for (int i = resto; i >= 0; i = i - pad.length()) {
                if (i  >= pad.length()) {
                    if (first){ padded = pad; } else { padded += pad; }
                } else {
                    if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                }
                first = false;
            }
        } else {
            padded = pad.substring(0,resto);
        }
        return padded;
    }

    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|/']", "");
        return newText;
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

}

