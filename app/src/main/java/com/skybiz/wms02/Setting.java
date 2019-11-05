package com.skybiz.wms02;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.CheckConnection;
import com.skybiz.wms02.m_Database.Server.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Setting extends AppCompatActivity {

    EditText txtServerName,txtUserName,txtDBName,txtPassword,txtPort;
    Switch swDBStatus,swCharSetType;
    String deviceId,Modules;
    Button btnCheck,btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_setting);
        getSupportActionBar().setTitle("SETTING SERVER");
        txtServerName=(EditText)findViewById(R.id.txtServerName);
        txtDBName=(EditText)findViewById(R.id.txtDBName);
        txtUserName=(EditText)findViewById(R.id.txtUserName);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        txtPort=(EditText)findViewById(R.id.txtPort);
        swDBStatus=(Switch) findViewById(R.id.swDBStatus);
        swCharSetType=(Switch) findViewById(R.id.swCharSetType);
        btnCheck=(Button)findViewById(R.id.btnCheck);
        btnSave=(Button)findViewById(R.id.btnSave);

        swDBStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swDBStatus.setText("Online");
                }else{
                    swDBStatus.setText("Offline");
                }
            }
        });
        swCharSetType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swCharSetType.setText("GBK");
                }else{
                    swCharSetType.setText("UTF-8");
                }
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fncheck();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fnsave();
            }
        });
        retMac();
        getConn();
        //retData();
    }
    private void fnsave(){
        String IPAddress=txtServerName.getText().toString();
        String UserName=txtUserName.getText().toString();
        String DBName=txtDBName.getText().toString();
        String Password=txtPassword.getText().toString();
        String Port=txtPort.getText().toString();
        //String PrinterPort=txtPrinterPort.getText().toString();
        String DBStatus="0";
        if(swDBStatus.isChecked()){
            DBStatus="1";
        }else{
            DBStatus="0";
        }

        String CharSetType="UTF-8";
        if(swCharSetType.isChecked()){
            CharSetType="GBK";
        }else{
            CharSetType="UTF-8";
        }
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        String qCheck="select count(*)as numrows from tb_setting";
        int numrows=0;
        Cursor rsCheck=db.getQuery(qCheck);
        while(rsCheck.moveToNext()){
            numrows=rsCheck.getInt(0);
        }
        if(numrows==0){
            String qInsert="Insert into tb_setting(ServerName,UserName,Password," +
                    "Port,DBName,DBStatus," +
                    "CharSetType)values(" +
                    "'"+IPAddress+"', '"+UserName+"', '"+Password+"'," +
                    "'"+Port+"', '"+DBName+"', '"+DBStatus+"'," +
                    "'"+CharSetType+"')";
            long addNew=db.exeQuery(qInsert);
            if(addNew>0){
                Toast.makeText(Setting.this,"Succesful, save setting", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Setting.this,"Failure, save setting", Toast.LENGTH_SHORT).show();
            }
        }else{
            String qDel="delete from tb_setting";
            db.exeQuery(qDel);
            String qInsert="Insert into tb_setting(ServerName,UserName,Password," +
                    "Port,DBName,DBStatus," +
                    "CharSetType)values(" +
                    "'"+IPAddress+"', '"+UserName+"', '"+Password+"'," +
                    "'"+Port+"', '"+DBName+"', '"+DBStatus+"'," +
                    "'"+CharSetType+"')";
            long addNew=db.exeQuery(qInsert);
            if(addNew>0){
                Toast.makeText(Setting.this,"Succesful, save setting", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Setting.this,"Failure, save setting", Toast.LENGTH_SHORT).show();
            }
        }
        db.closeDB();
    }
    private void fncheck(){
        String IPAddress=txtServerName.getText().toString();
        String UserName=txtUserName.getText().toString();
        String DBName=txtDBName.getText().toString();
        String Password=txtPassword.getText().toString();
        String Port=txtPort.getText().toString();
        CheckConnection checkConnection=new CheckConnection(Setting.this,IPAddress,UserName,DBName,Password,Port);
        checkConnection.execute();
    }
    private void retData(){
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        String query="select ServerName,UserName,Password," +
               "Port,DBName,DBStatus from tb_setting";
        Cursor rsData=db.getQuery(query);
        while(rsData.moveToNext()){
            txtServerName.setText(rsData.getString(0));
            txtUserName.setText(rsData.getString(1));
            txtPassword.setText(rsData.getString(2));
            txtPort.setText(rsData.getString(3));
            txtDBName.setText(rsData.getString(4));
            String DBStatus=rsData.getString(5);
            String CharSetType=rsData.getString(6);
            if(DBStatus.equals("1")){
                swDBStatus.setChecked(true);
            }else{
                swDBStatus.setChecked(false);
            }
            if(CharSetType.equals("1")){
                swCharSetType.setChecked(true);
            }else{
                swCharSetType.setChecked(false);
            }
        }
        db.closeDB();
    }

    private void retMac(){
        deviceId= Settings.Secure.getString(Setting.this.getContentResolver(),Settings.Secure.ANDROID_ID);
        //txtMAC.setText(deviceId);
    }
    private void getConn(){
        ConnLicense connLicense=new ConnLicense(Setting.this);
        connLicense.execute();
    }
    public class CheckLicenese extends AsyncTask<Void,Void,String> {
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port;
        String vIPAddress,vUserName,vPassword,
                vDBName,vPort,vCompanyName,
                vOnlineYN,vCharSet,vModules;

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
                if(vOnlineYN.equals("1")){
                    swDBStatus.setChecked(true);
                }else {
                    swDBStatus.setChecked(false);
                }
                if(vCharSet.equals("GBK")){
                    swCharSetType.setChecked(true);
                }else {
                    swCharSetType.setChecked(false);
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
                            "IFNULL(ChineseCharSet,'')as ChineseCharSet," +
                            "IFNULL(Modules,'')as Modules  " +
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
                            vModules=rsDB.getString("Modules");
                            String StoreCode=rsDB.getString("StoreCode");
                            String check="select count(*) as numrows from tb_setting";
                            Cursor rsCheck=db.getQuery(check);
                            int numrows=0;
                            while(rsCheck.moveToNext()){
                                numrows=rsCheck.getInt(0);
                            }
                            if(numrows==0){
                                String insert="insert into tb_setting(ServerName, UserName, Password," +
                                        "DBName, Port, UserCode," +
                                        "StoreCode,DBStatus,CharSetType," +
                                        "Modules)values" +
                                        "('"+vIPAddress+"', '"+vUserName+"', '"+vPassword+"'," +
                                        " '"+vDBName+"', '"+vPort+"', '"+deviceId+"', " +
                                        "'"+StoreCode+"', '"+vOnlineYN+"', '"+vCharSet+"'," +
                                        "'"+vModules+"')";
                                Log.d("INSERT LOCAL",insert);
                                db.exeQuery(insert);
                            }else{
                                String del="delete from tb_setting";
                                db.exeQuery(del);
                                String insert="insert into tb_setting(ServerName, UserName, Password," +
                                        "DBName, Port, UserCode," +
                                        "StoreCode, DBStatus, CharSetType," +
                                        "Modules)values" +
                                        "('"+vIPAddress+"', '"+vUserName+"', '"+vPassword+"'," +
                                        " '"+vDBName+"', '"+vPort+"', '"+deviceId+"', " +
                                        "'"+StoreCode+"', '"+vOnlineYN+"', '"+vCharSet+"', " +
                                        " '"+vModules+"')";
                                Log.d("UPDATE LOCAL",insert);
                                db.exeQuery(insert);

                            }
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
