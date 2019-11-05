package com.skybiz.wms02.ui_PickingList;

import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.ui_PickingList.m_List.ListDownload;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PickingList extends AppCompatActivity {

    EditText txtDoc1No;
    RecyclerView rvList;
    Button btnSave;
    private GridLayoutManager lLayout;
    ProgressBar pbSave;
    CheckBox chkVerify;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_list);
        txtDoc1No=(EditText)findViewById(R.id.txtDoc1No);
        rvList=(RecyclerView)findViewById(R.id.rvList);
        btnSave=(Button)findViewById(R.id.btnSave);
        pbSave=(ProgressBar)findViewById(R.id.pbSave);
        chkVerify=(CheckBox)findViewById(R.id.chkVerify);
        txtDoc1No.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String Doc1No=     txtDoc1No.getText().toString();
                    addList(Doc1No);
                    return true;
                }
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               savepicking();
               pbSave.setVisibility(View.VISIBLE);
            }
        });
        chkVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    CheckDoc1No checkDoc1No=new CheckDoc1No(PickingList.this);
                    checkDoc1No.execute();
                    txtDoc1No.setEnabled(false);
                }else{
                    disableSave();
                    txtDoc1No.setEnabled(true);
                }
            }
        });
        resetDum();
        disableSave();
    }
    private void afterSave(){
        txtDoc1No.setEnabled(true);
        chkVerify.setChecked(false);
        disableSave();
        loadList();
    }

    private void savepicking(){
        disableSave();
        SavePicking savePicking=new SavePicking(PickingList.this);
        savePicking.execute();
    }
    private void resetDum(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String delDum="delete from tb_dumpicking";
            db.exeQuery(delDum);
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void addList(String Doc1No){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String insert="insert into tb_dumpicking(Doc1No,Qty,Remark)values('"+Doc1No+"', '0' , '')";
            long add=db.exeQuery(insert);
            if(add>0){
                loadList();
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void enableSave(){
        btnSave.setEnabled(true);
    }
    public void disableSave(){
        btnSave.setEnabled(false);
    }
    public void loadList(){
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        ListDownload listDownload=new ListDownload(this,rvList);
        listDownload.execute();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtDoc1No.getText().clear();
                txtDoc1No.requestFocus();
            }
        }, 600);

    }

    public class CheckDoc1No extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType,resDoc1No="";
        Connection conn;

        public CheckDoc1No(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fncheck();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")){
                Toast.makeText(c,"Verify Suucess", Toast.LENGTH_SHORT).show();
                enableSave();
            }else if(s.equals("failure")){
                Toast.makeText(c,"Verify Failure", Toast.LENGTH_SHORT).show();
            }
            loadList();
            pbSave.setVisibility(View.GONE);
        }
        private String fncheck() {
            try {

                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "Port,DBName,DBStatus," +
                        "CharSetType from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress   = curSet.getString(0);
                    UserName    = curSet.getString(1);
                    Password    = curSet.getString(2);
                    Port        = curSet.getString(3);
                    DBName      = curSet.getString(4);
                    DBStatus    = curSet.getString(5);
                    CharType    = curSet.getString(6);
                }
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        String sqlDum = "select Doc1No,RunNo from tb_dumpicking Group By Doc1No ";
                        Cursor rsDum = db.getQuery(sqlDum);
                        int k=0;
                        while (rsDum.moveToNext()) {
                            String Doc1No = rsDum.getString(0);
                            String RunNoDum = rsDum.getString(1);
                            String count2 = "select COUNT(*)AS numrows FROM stk_cus_inv_hd " +
                                  " where DocType='CusInv' and Doc1No='" + Doc1No + "' and PickingYN='0' ";
                            Log.d("QUERY",count2);
                            Statement stmtCheck = conn.createStatement();
                            stmtCheck.execute(count2);
                            ResultSet rsCheck = stmtCheck.getResultSet();
                            int i=0;
                            while (rsCheck.next()) {
                                i=rsCheck.getInt(1);
                                if(i==0){
                                   k += k+1;
                                }else{
                                    String upDum="update tb_dumpicking set Qty='1' where RunNo='"+RunNoDum+"' ";
                                    Log.d("UPDATE",upDum);
                                    db.exeQuery(upDum);
                                }
                            }
                            Log.d("NUMROW", "NO "+i);
                        }
                        if(k>0){
                            z="failure";
                        }else{
                            z="success";
                        }
                    }
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
    public class SavePicking extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType,resDoc1No="";
        Connection conn;

        public SavePicking(Context c) {
            this.c = c;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                Toast.makeText(c,"Save Failure", Toast.LENGTH_SHORT).show();

            }else if(s.equals("success")){
                if(resDoc1No.isEmpty()){
                    Toast.makeText(c,"Save Succesful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(c,"Doc1No  Cannot Save "+resDoc1No, Toast.LENGTH_SHORT).show();
                }
               afterSave();
            }else if(s.equals("error")){
                Toast.makeText(c,"Error Connection", Toast.LENGTH_SHORT).show();
            }
            pbSave.setVisibility(View.GONE);
        }
        private String fnsave(){
            try{
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "Port,DBName,DBStatus," +
                        "CharSetType from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress   = curSet.getString(0);
                    UserName    = curSet.getString(1);
                    Password    = curSet.getString(2);
                    Port        = curSet.getString(3);
                    DBName      = curSet.getString(4);
                    DBStatus    = curSet.getString(5);
                    CharType    = curSet.getString(6);
                }

                String MacAddress   = Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);
                MacAddress          = MacAddress.substring(0,4);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
                String Doc1NoPick=D_ateTime+MacAddress;
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        String count   = "select count(*)as numrows from tb_dumpicking Group By Doc1No ";
                        String sqlDum   = "select Doc1No,RunNo from tb_dumpicking Group By Doc1No ";
                        Cursor rsDum    = db.getQuery(sqlDum);
                        Cursor rsCount =db.getQuery(count);
                        int numrow=0;
                        while(rsCount.moveToNext()){
                            numrow=rsCount.getInt(0);
                        }
                        int k=0;
                        while(rsDum.moveToNext()) {
                            String Doc1No     = rsDum.getString(0);
                            String RunNoDum   = rsDum.getString(1);
                            String CusInvRunNo="";
                            String count2   = "select count(*)as numrows from tb_dumpicking Group By Doc1No ";

                            String qCusInv="select D.RunNo from stk_cus_inv_dt D inner join stk_cus_inv_hd H " +
                                    "where H.DocType='CusInv' and D.Doc1No='"+Doc1No+"' and H.PickingYN='0' " +
                                    " Group By D.RunNo ";
                            Log.d("PICKINGYN", qCusInv);
                            Statement stmtCusInv = conn.createStatement();
                            stmtCusInv.execute(qCusInv);
                            ResultSet rsCusInv = stmtCusInv.getResultSet();
                            int i=0;
                            while (rsCusInv.next()) {
                                Log.d("ADA","ada"+i);
                                CusInvRunNo=rsCusInv.getString(1);
                                String qInPicking = "Insert Into stk_picking_hd (Doc1No, D_ate, DONo, DORunNo, InvNo, CusInvRunNo, L_ink)" +
                                        " Values ('" + Doc1NoPick + "', '" + D_ate + "', '', 0, '" + Doc1No + "', '" + CusInvRunNo + "','1')";
                                Statement stmtInPicking = conn.createStatement();
                                stmtInPicking.execute(qInPicking);
                                i++;
                            }

                            if(i>0) {
                                String qUpCusInv = "UPDATE stk_cus_inv_hd SET PickingYN = '1' WHERE Doc1No = '" + Doc1No + "' ";
                                Statement stmtUpCusInv = conn.createStatement();
                                stmtUpCusInv.execute(qUpCusInv);

                               /*String qCheckSO = " SELECT SO.Doc1No FROM stk_picking_hd P " +
                                        " INNER JOIN stk_cus_inv_dt INV ON P.CusInvRunNo = INV.RunNo " +
                                        " INNER JOIN stk_sales_order_dt SO ON INV.SORunNo = SO.RunNo " +
                                        " WHERE P.Doc1No = '" + Doc1NoPick + "' GROUP BY SO.Doc1No " ;
                                Log.d("Query SO", qCheckSO);
                                Statement stmtCheckSO = conn.createStatement();
                                stmtCheckSO.execute(qCheckSO);
                                ResultSet rsCheckSO = stmtCheckSO.getResultSet();
                                while (rsCheckSO.next()) {
                                    ///udpate status
                                    String vStatus = fnuGetSOStatus(rsCheckSO.getString(1));
                                    String qUpSO = " UPDATE stk_sales_order_hd SET status = '"+vStatus+"' " +
                                                " WHERE Doc1No = '" +rsCheckSO.getString(1) +"' ";
                                    Statement stmtUpSO = conn.createStatement();
                                    stmtUpSO.execute(qUpSO);
                                }*/
                                String upDum="delete from tb_dumpicking where RunNo='"+RunNoDum+"' ";
                                db.exeQuery(upDum);
                                resDoc1No="";
                                k++;
                            }else{
                                resDoc1No+=Doc1No+";";
                                k--;
                            }
                        }
                        z="success";
                        Log.d("COUNT ", "NO "+k);
                        /*if(k==numrow){
                            z="success";
                            Log.d("count", k +"-"+ numrow);
                        }else{
                           // Log.d("count 2", k +"-"+ numrow);
                           // z="success";
                        }*/
                    }else{
                        z="error";
                    }
                }else if(DBStatus.equals("0")){
                    z="error";
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        private String fnuGetSOStatus(String Doc1No){
            String vStatus      ="Waiting";
            String vApprovedYN  = "0";
            String vINVDoc1No   = "";
            Double vIdleQty=0.00, vSOQty=0.00, vINVQty=0.00;
            try{
                String sql = "SELECT H.ApprovedYN, D.Doc1No, D.RunNO , " +
                            " SUM(D.Qty - D.VoidQty) AS SOQty " +
                            " FROM stk_sales_order_hd H " +
                            " INNER JOIN stk_sales_order_dt D ON H.Doc1No = D.Doc1No " +
                            " WHERE D.Doc1No = '" + Doc1No + "' GROUP BY D.Doc1No";
                Statement stmtApprove = conn.createStatement();
                stmtApprove.execute(sql);
                ResultSet rsApp = stmtApprove.getResultSet();
                while (rsApp.next()) {
                    vApprovedYN = rsApp.getString(1);
                    vSOQty      = rsApp.getDouble(4);
                    if(!vApprovedYN.equals("1")){
                        vStatus = "W.Approve";
                    }else {
                        String qSODt = " Select INV.Doc1No, INV.RunNo, INV.Qty from stk_sales_order_dt D " +
                                " INNER JOIN stk_cus_inv_dt INV ON D.RunNO = INV.SORunNO " +
                                " WHERE D.Doc1No = '" + Doc1No + "' ";

                        String vCusInvRunNoList = "";
                        Statement stmtSODt = conn.createStatement();
                        stmtSODt.execute(qSODt);
                        ResultSet rsSODt = stmtSODt.getResultSet();
                        vINVQty = 0.00;
                        while (rsSODt.next()) {
                            vINVQty += vINVQty +rsSODt.getDouble(3);
                            if(!vCusInvRunNoList.isEmpty()){
                                vCusInvRunNoList += vCusInvRunNoList + ",";
                            }
                            vCusInvRunNoList += vCusInvRunNoList + rsSODt.getString(2);
                        }
                        Log.d("Cus Inv RunNo", vCusInvRunNoList);
                        if(vSOQty - vINVQty > 0 ) {
                            vStatus = "W.Invoice";
                        }else {
                            vStatus = "W.Loading";
                            if(!vCusInvRunNoList.isEmpty()) {
                                String qCusInvQty = " SELECT sum(D.Qty) as vCusInvQty FROM stk_picking_hd P " +
                                                    " INNER JOIN stk_cus_inv_dt D ON P.CusInvRunNo  = D.RunNo " +
                                                    " WHERE D.RunNo IN ("+ vCusInvRunNoList+")" +
                                                    " GROUP BY ''" ;
                                Log.d("CusInvRunNo", qCusInvQty);
                                Statement stmtCusInvQty  = conn.createStatement();
                                stmtCusInvQty.execute(qCusInvQty);
                                ResultSet rsCusInvQty    = stmtCusInvQty.getResultSet();
                                while (rsCusInvQty.next()) {
                                   if(vSOQty==rsCusInvQty.getDouble(1)){
                                       vStatus = "W.Deliver";
                                       String qCheckSODt = "SELECT SODT.Doc1No, SOHD.Status, P.Doc1No, P.RunNo, P.Remark " +
                                                " FROM stk_picking_hd P " +
                                                " INNER JOIN stk_cus_inv_dt INV ON P.CusInvRunNo=INV.RunNo " +
                                                " INNER JOIN stk_sales_order_dt SODT ON SODT.RunNo=INV.SORunNo  " +
                                                " INNER JOIN stk_sales_order_hd SOHD ON SOHD.Doc1No =SODT.Doc1No  " +
                                                " WHERE SODT.Doc1No = '" +Doc1No+"' AND (P.Remark IS NULL OR P.Remark = '') ";
                                       Statement stmtCheckSODt  = conn.createStatement();
                                       stmtCheckSODt.execute(qCheckSODt);
                                       ResultSet rsCheckSODt    = stmtCheckSODt.getResultSet();
                                       while (rsCheckSODt.next()) {
                                           vStatus = "Delivered";
                                       }
                                   }
                                }
                            }
                        }
                    }
                }
               return vStatus;
            }catch (SQLException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            return vStatus;
        }
    }
}
