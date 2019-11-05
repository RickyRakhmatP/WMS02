package com.skybiz.wms02.ui_PrintInvLabel.m_ManualPrint;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Supplier.DownloadSupplier;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_PrintInvLabel.PrintInvLabel;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogManual extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    Spinner spUser,spDoc1NoFrom,spDoc1NoTo;
    DatePickerDialog datePickerDialog;
    EditText txtDateFrom,txtDateTo;
    Button btnRefresh,btnPrint;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_manual, container, false);

        spUser              = (Spinner) view.findViewById(R.id.spUser);
        spDoc1NoFrom        = (Spinner) view.findViewById(R.id.spDoc1NoFrom);
        spDoc1NoTo          = (Spinner) view.findViewById(R.id.spDoc1NoTo);
        txtDateFrom         = (EditText)view.findViewById(R.id.txtDateFrom);
        txtDateTo           = (EditText)view.findViewById(R.id.txtDateTo);
        btnRefresh          = (Button)view.findViewById(R.id.btnRefresh);
        btnPrint          = (Button)view.findViewById(R.id.btnPrint);
        txtDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDateFrom();
            }
        });

        txtDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDateTo();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printLabel();
            }
        });
        getDialog().setTitle("Manual Print");
        initData();
       // refresh("");
        return view;
    }

    private void initData(){
        btnRefresh.setEnabled(false);
        retUser();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String D_ate = sdf.format(date);
        String Y_esterday=fnchangedate(-1);
        txtDateTo.setText(D_ate);
        txtDateFrom.setText(Y_esterday);
    }

    private void printLabel(){
        btnRefresh.setBackgroundColor(Color.parseColor("#000000"));
        String UserCode     =spUser.getSelectedItem().toString();
        String DateFrom     =txtDateFrom.getText().toString();
        String DateTo       =txtDateTo.getText().toString();
        String Doc1NoTo     =spDoc1NoTo.getSelectedItem().toString();
        String Doc1NoFrom   =spDoc1NoFrom.getSelectedItem().toString();
        if(UserCode.isEmpty()){
            Toast.makeText(getActivity(),"User Cannot Empty", Toast.LENGTH_SHORT).show();
            btnRefresh.setBackgroundColor(Color.parseColor("#007E33"));
        }else {
            ((PrintInvLabel)getActivity()).setInvoice2(UserCode,DateFrom,DateTo,Doc1NoFrom,Doc1NoTo);
            btnRefresh.setBackgroundColor(Color.parseColor("#007E33"));
            dismiss();
        }
    }
    private void loadDateTo(){
        final Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        final int mMonth=c.get(Calendar.MONTH);
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        final DecimalFormat mFormat= new DecimalFormat("00");
        datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                final Double dMonth=(monthOfYear+1)*1.00;
                final Double dDay=dayOfMonth*1.00;
                txtDateTo.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }
    private void loadDateFrom(){
        final Calendar c=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        final int mMonth=c.get(Calendar.MONTH);
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        final DecimalFormat mFormat= new DecimalFormat("00");
        datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                final Double dMonth=(monthOfYear+1)*1.00;
                final Double dDay=dayOfMonth*1.00;
                txtDateFrom.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }

    private String fnchangedate(int calDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String curentDate = sdf.format(date);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(curentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, calDay);
        Date resultdate = new Date(c.getTimeInMillis());
        String olDate = sdf.format(resultdate);
        return olDate;
    }
    private void retUser(){
        RetUser dUser=new RetUser(getActivity());
        dUser.execute();
    }

    public void refresh() {
        btnRefresh.setBackgroundColor(Color.parseColor("#000000"));
        String UserCode =spUser.getSelectedItem().toString();
        String DateFrom =txtDateFrom.getText().toString();
        String DateTo   =txtDateTo.getText().toString();
        if(UserCode.length()==0){
            Toast.makeText(getActivity(),"User Cannot Empty", Toast.LENGTH_SHORT).show();
            btnRefresh.setBackgroundColor(Color.parseColor("#42a5f5"));
        }else{
            RetDoc1No retDoc1No=new RetDoc1No(getActivity(),UserCode,DateFrom,DateTo);
            retDoc1No.execute();
            btnRefresh.setBackgroundColor(Color.parseColor("#42a5f5"));
        }

    }

    private class RetDoc1No extends AsyncTask<Void,Void,String>{

        Context c;
        String UserCode,DateFrom,DateTo;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsDoc1No=new ArrayList<String>();

        public RetDoc1No(Context c, String userCode, String dateFrom, String dateTo) {
            this.c = c;
            UserCode = userCode;
            DateFrom = dateFrom;
            DateTo = dateTo;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnretdoc1no();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                spDoc1NoFrom.setAdapter(new ArrayAdapter<String>(c,
                        android.R.layout.simple_spinner_dropdown_item,lsDoc1No));

                spDoc1NoTo.setAdapter(new ArrayAdapter<String>(c,
                        android.R.layout.simple_spinner_dropdown_item,lsDoc1No));
            }else{
                Toast.makeText(c,"ret doc1no is empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String fnretdoc1no(){
            try {
                z = "error";
                lsDoc1No.clear();
                lsDoc1No.add("");
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet = "select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                }
               /* String sql ="select H.Doc1No from stk_cus_inv_hd H" +
                        " inner join stk_receipt2 R ON H.Doc1No=R.Doc1No" +
                        " where H.D_ate>='"+DateFrom+"' and H.D_ate<='"+DateTo+"'" +
                        " and R.UserCode='"+UserCode+"' Order By H.D_ate Desc ";*/
                String sql=" SELECT H.Doc1No, S.UserCode " +
                        " FROM stk_cus_inv_hd H " +
                        " INNER JOIN sys_audit_log S ON H.Doc1No = S.Doc1No" +
                        " WHERE S.UserCode = '"+UserCode+"'" +
                        " and (H.D_ate between '"+DateFrom+"' and '"+DateTo+"') " +
                        " GROUP BY H.Doc1No Order By H.Doc1No ASC";
                Log.d("Query", sql);
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsData = statement.getResultSet();
                        while (rsData.next()) {
                            lsDoc1No.add(rsData.getString(1));
                        }
                        z="success";
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsData=db.getQuery(sql);
                    while(rsData.moveToNext()){
                        lsDoc1No.add(rsData.getString(0));
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
    private class RetUser extends AsyncTask<Void,Void,String>{

        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsUser=new ArrayList<String>();

        public RetUser(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                btnRefresh.setEnabled(true);
                spUser.setAdapter(new ArrayAdapter<String>(c,
                        android.R.layout.simple_spinner_dropdown_item,lsUser));
            }else{
                //
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fndownload();
        }

        private String fndownload(){
            try{
                z="error";
                lsUser.clear();
                lsUser.add("");
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
                String sql ="Select UserCode, UserName from sys_userprofile_hd where Status='Active' and DepartmentCode='BILLING' ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsData = statement.getResultSet();
                        while (rsData.next()) {
                            lsUser.add(rsData.getString(1));
                        }
                        z="success";
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsData=db.getQuery(sql);
                    while(rsData.moveToNext()){
                        lsUser.add(rsData.getString(0));
                    }
                    z="success";
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
