package com.skybiz.wms02.ui_Setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.R;
import com.skybiz.wms02.Setting;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.CheckConnection;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Summary.CheckPaidAmt;
import com.skybiz.wms02.m_Summary.SaveTrn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class Fragment_Server extends Fragment {
    View view;
    EditText txtServerName,txtUserName,txtDBName,
            txtPassword,txtPort,txtBranchCode,
            txtLocationCode,txtStoreCode,txtPrinterIP,
            txtPrinterPort;
    Switch swDBStatus,swCharSetType;
    String deviceId,Modules;
    Button btnSave,btnCheck,btnBack;
    TextView txtMACAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_server, container, false);

        txtServerName=(EditText)view.findViewById(R.id.txtServerName);
        txtDBName=(EditText)view.findViewById(R.id.txtDBName);
        txtUserName=(EditText)view.findViewById(R.id.txtUserName);
        txtPassword=(EditText)view.findViewById(R.id.txtPassword);
        txtPort=(EditText)view.findViewById(R.id.txtPort);
        txtBranchCode=(EditText)view.findViewById(R.id.txtBranchCode);
        txtLocationCode=(EditText)view.findViewById(R.id.txtLocationCode);
        txtStoreCode=(EditText)view.findViewById(R.id.txtStoreCode);
        swDBStatus=(Switch) view.findViewById(R.id.swDBStatus);
        swCharSetType=(Switch) view.findViewById(R.id.swCharSetType);
        btnSave=(Button)view.findViewById(R.id.btnSave);
        btnCheck=(Button)view.findViewById(R.id.btnCheck);
        btnBack=(Button)view.findViewById(R.id.btnBack);
        txtPrinterPort=(EditText) view.findViewById(R.id.txtPrinterPort);
        txtPrinterIP=(EditText) view.findViewById(R.id.txtPrinterIP);
        txtMACAddress=(TextView) view.findViewById(R.id.txtMACAddress);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConn();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetting();
            }
        });
        swDBStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swDBStatus.setText("Online");
                }else {
                    swDBStatus.setText("Offline");
                }
            }
        });
        swCharSetType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swCharSetType.setText("GBK");
                }else {
                    swCharSetType.setText("UTF-8");
                }
            }
        });
        retMac();
        getConn();
        return view;
    }
    private void saveSetting(){
        String vIPAddress=txtServerName.getText().toString();
        String vUserName=txtUserName.getText().toString();
        String vPassword=txtPassword.getText().toString();
        String vDBName=txtDBName.getText().toString();
        String vPort=txtPort.getText().toString();
        String vLocationCode=txtLocationCode.getText().toString();
        String vBranchCode=txtBranchCode.getText().toString();
        String vStoreCode=txtStoreCode.getText().toString();
        String vPrinterIP=txtPrinterIP.getText().toString();
        String vPrinterPort=txtPrinterPort.getText().toString();
        String vOnlineYN="0";
        String vCharSet="UTF-8";
        if(swDBStatus.isChecked()){
            vOnlineYN="1";
        }
        if(swCharSetType.isChecked()){
            vCharSet="GBK";
        }
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String check="select count(*) as numrows from tb_setting";
        Cursor rsCheck=db.getQuery(check);
        int numrows=0;
        while(rsCheck.moveToNext()){
            numrows=rsCheck.getInt(0);
        }
        long add=0;
        if(numrows==0){
            String insert="insert into tb_setting(ServerName, UserName, Password," +
                    "DBName, Port, UserCode," +
                    "StoreCode,DBStatus,CharSetType," +
                    "LocationCode, BranchCode, Modules)values" +
                    "('"+vIPAddress+"', '"+vUserName+"', '"+vPassword+"'," +
                    " '"+vDBName+"', '"+vPort+"', '"+deviceId+"', " +
                    "'"+vStoreCode+"', '"+vOnlineYN+"', '"+vCharSet+"'," +
                    " '"+vLocationCode+"', '"+vBranchCode+"', '"+Modules+"')";
          //  Log.d("INSERT LOCAL",insert);
            add=db.exeQuery(insert);

            String qPrinter="insert into tb_settingprinter(IPAddress,Port)values('"+vPrinterIP+"', '"+vPrinterPort+"')";
            db.exeQuery(qPrinter);
        }else{
            String del="delete from tb_setting";
            db.exeQuery(del);
            String del2="delete from tb_settingprinter";
            db.exeQuery(del2);
            String insert="insert into tb_setting(ServerName, UserName, Password," +
                    "DBName, Port, UserCode," +
                    "StoreCode,DBStatus,CharSetType," +
                    "LocationCode, BranchCode, Modules)values" +
                    "('"+vIPAddress+"', '"+vUserName+"', '"+vPassword+"'," +
                    " '"+vDBName+"', '"+vPort+"', '"+deviceId+"', " +
                    "'"+vStoreCode+"', '"+vOnlineYN+"', '"+vCharSet+"'," +
                    " '"+vLocationCode+"', '"+vBranchCode+"', '"+Modules+"')";
            //Log.d("UPDATE LOCAL",insert);
            add=db.exeQuery(insert);
            String qPrinter="insert into tb_settingprinter(IPAddress,Port)values('"+vPrinterIP+"', '"+vPrinterPort+"')";
            db.exeQuery(qPrinter);

        }
        if(add>0){
            Toast.makeText(getContext(), "OK, update setting succesful ", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "ERROR, update setting failure ", Toast.LENGTH_SHORT).show();
        }
        db.closeDB();
    }
    private void checkConn(){
        String IPAddress=txtServerName.getText().toString();
        String UserName=txtUserName.getText().toString();
        String DBName=txtDBName.getText().toString();
        String Password=txtPassword.getText().toString();
        String Port=txtPort.getText().toString();
        CheckConnection checkConnection=new CheckConnection(getActivity(),IPAddress,UserName,
                DBName,Password,Port);
        try {
            String checkYN= checkConnection.execute().get();
            if(checkYN.equals("success")){
                btnSave.setEnabled(true);
            }else{
                btnSave.setEnabled(false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private void retMac(){
        deviceId= Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
        txtMACAddress.setText(deviceId);
        try{
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qPrinter="select IPAddress,Port from tb_settingprinter";
            Cursor rsPrinter=db.getQuery(qPrinter);
            while(rsPrinter.moveToNext()){
                txtPrinterIP.setText(rsPrinter.getString(0));
                txtPrinterPort.setText(rsPrinter.getString(1));
            }
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
        //txtMAC.setText(deviceId);
    }
    private void getConn(){
        ConnLicense connLicense=new ConnLicense(getActivity());
        connLicense.execute();
    }
    public class CheckLicenese extends AsyncTask<Void,Void,String> {
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port;
        String vIPAddress,vUserName,vPassword,
                vDBName,vPort,vCompanyName,
                vOnlineYN,vCharSet,vLocationCode,
                vBranchCode,vStoreCode,vModules;

        public CheckLicenese(Context c, String IPAddress, String userName, String password, String DBName, String port) {
            this.c = c;
            this.IPAddress = IPAddress;
            UserName = userName;
            Password = password;
            this.DBName = DBName;
            Port = port;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.checkLicense();
        }

        @Override
        protected void onPostExecute(String isConnect) {
            super.onPostExecute(isConnect);
            if(isConnect.equals("error")){
                Toast.makeText(c,"get conn license failure", Toast.LENGTH_SHORT).show();
            }else if(isConnect.equals("ada")){
                Toast.makeText(c,"Success get setting server", Toast.LENGTH_SHORT).show();
                txtServerName.setText(vIPAddress);
                txtUserName.setText(vUserName);
                txtDBName.setText(vDBName);
                txtPassword.setText(vPassword);
                txtPort.setText(vPort);
                txtBranchCode.setText(vBranchCode);
                txtLocationCode.setText(vLocationCode);
                txtStoreCode.setText(vStoreCode);
                Modules=vModules;
                if(vOnlineYN.equals("1")){
                    swDBStatus.setChecked(true);
                    swDBStatus.setText("Online");
                }else {
                    swDBStatus.setChecked(false);
                    swDBStatus.setText("Offline");
                }
                if(vCharSet.equals("GBK")){
                    swCharSetType.setChecked(true);
                    swCharSetType.setText("GBK");
                }else if(vCharSet.equals("UTF-8")) {
                    swCharSetType.setChecked(false);
                    swCharSetType.setText("UTF-8");
                }else{
                    swCharSetType.setChecked(false);
                    swCharSetType.setText("Unknown");
                }
            }else if (isConnect.equals("kosong")){
                Toast.makeText(c,"Information not available, please request vendor to register the device!", Toast.LENGTH_SHORT).show();
            }
        }
        private String  checkLicense(){
            try{
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
                //String deviceId = telephonyManager.getDeviceId();
                String URLc="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
                Connection conn= Connector.connect(URLc, UserName, Password);
                if (conn == null) {
                    Log.d("ERROR",URLc+UserName+Password);
                    z = "error";
                }else{
                    String numrow="0";
                    String vCheck="SELECT COUNT(MACAddress)as NumRows, DB_IP, DB_ID," +
                            " DB_Password, DB_Port, DatabaseName," +
                            "OnlineYN,CompanyName,StoreCode," +
                            "IFNULL(ChineseCharSet,'')as ChineseCharSet, LocationCode, BranchCode, " +
                            "IFNULL(Modules,'')as Modules " +
                            "from androidlicense where MACAddress='"+deviceId+"' ";
                    Statement statement     = conn.createStatement();
                    statement.execute(vCheck);
                    ResultSet rsDB      = statement.getResultSet();
                    while(rsDB.next()){
                        numrow=rsDB.getString("NumRows");
                        if(!numrow.equals("0")){
                            z="ada";
                            vIPAddress=rsDB.getString("DB_IP");
                            vUserName=rsDB.getString("DB_ID");
                            vPort=rsDB.getString("DB_Port");
                            vPassword=rsDB.getString("DB_Password");
                            vDBName=rsDB.getString("DatabaseName");
                            vOnlineYN=rsDB.getString("OnlineYN");
                            vCompanyName=rsDB.getString("CompanyName");
                            vCharSet=rsDB.getString("ChineseCharSet");
                            vStoreCode=rsDB.getString("StoreCode");
                            vLocationCode=rsDB.getString("LocationCode");
                            vBranchCode=rsDB.getString("BranchCode");
                            vModules=rsDB.getString("Modules");
                           /* String check="select count(*) as numrows from tb_setting";
                            Cursor rsCheck=db.getQuery(check);
                            int numrows=0;
                            while(rsCheck.moveToNext()){
                                numrows=rsCheck.getInt(0);
                            }
                            if(numrows==0){
                                String insert="insert into tb_setting(ServerName, UserName, Password," +
                                        "DBName, Port, UserCode," +
                                        "StoreCode,DBStatus,CharSetType," +
                                        "LocationCode, BranchCode)values" +
                                        "('"+vIPAddress+"', '"+vUserName+"', '"+vPassword+"'," +
                                        " '"+vDBName+"', '"+vPort+"', '"+deviceId+"', " +
                                        "'"+StoreCode+"', '"+vOnlineYN+"', '"+vCharSet+"'," +
                                        " '"+LocationCode+"', '"+BranchCode+"')";
                                Log.d("INSERT LOCAL",insert);
                                db.exeQuery(insert);
                            }else{
                                String del="delete from tb_setting";
                                db.exeQuery(del);
                                String insert="insert into tb_setting(ServerName, UserName, Password," +
                                        "DBName, Port, UserCode," +
                                        "StoreCode,DBStatus,CharSetType," +
                                        "LocationCode, BranchCode)values" +
                                        "('"+vIPAddress+"', '"+vUserName+"', '"+vPassword+"'," +
                                        " '"+vDBName+"', '"+vPort+"', '"+deviceId+"', " +
                                        "'"+StoreCode+"', '"+vOnlineYN+"', '"+vCharSet+"'," +
                                        " '"+LocationCode+"', '"+BranchCode+"')";
                                Log.d("UPDATE LOCAL",insert);
                                db.exeQuery(insert);

                            }*/
                        }else{
                            z="kosong";
                        }
                    }
                    db.closeDB();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }catch (SQLiteException e){
                e.printStackTrace();
            }
            return z;
        }
    }
    public class ConnLicense extends AsyncTask<Void,Void,String>{
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port;
        // TelephonyManager telephonyManager;
        public ConnLicense(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                z="success";
                URL url = new URL("http://skybiz.com.my/userlicensesetting/androidlicense.txt");
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                z="";
                int i=0;
                while ((str = in.readLine()) != null) {
                    Log.d("STRING",str);
                    fngetStr(i,str);
                    z +=str;
                    i++;
                    // str is one line of text; readLine() strips the newline character(s)
                }
                in.close();
                return z;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return z;
        }

        @Override
        protected void onPostExecute(String isConnect) {
            super.onPostExecute(isConnect);
            if(isConnect.equals("error")){
                Toast.makeText(c,"Get Conn License Failure", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Success Conn License", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CheckLicenese checkLicenese=new CheckLicenese(c,IPAddress,UserName,Password,DBName,Port);
                        checkLicenese.execute();
                    }
                }, 600);
            }
        }
        private void fngetStr(int no,String str){
            int position = str.indexOf("=")+1;
            switch (no) {
                case 0:
                    IPAddress=str.substring(position,str.length());
                    break;
                case 1:
                    Log.d("STR",str+String.valueOf(position)+String.valueOf(str.length()-position));
                    Port=str.substring(position,str.length());
                    break;
                case 2:
                    UserName=str.substring(position,str.length());
                    break;
                case 3:
                    Password=str.substring(position,str.length());
                    break;
                case 4:
                    DBName=str.substring(position,str.length());
                    break;
            }
        }

    }
}
