package com.skybiz.wms02.ui_IssueStock;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Summary.CheckPaidAmt;
import com.skybiz.wms02.m_Summary.SaveTrn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;


public class Fragment_Summary extends Fragment {
    View view;
    TextView txtAmountDue,txtDoc1No,txtTotalAmt,txtTotalQty;
    LinearLayout lnCash;
    Button btnPay;
    String isTotal,totalamt,Doc1No, PaidAmount;
    String TypePrinter,NamePrinter,IPPrinter,UUID;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary, container, false);
        txtDoc1No= (TextView) view.findViewById(R.id.txtDoc1No);
        txtAmountDue= (TextView) view.findViewById(R.id.txtAmountDue);
        txtTotalAmt = (TextView) view.findViewById(R.id.txtTotal);
        txtTotalQty = (TextView) view.findViewById(R.id.txtTotalQty);
        btnPay=(Button)view.findViewById(R.id.btnPay);
        lnCash= (LinearLayout) view.findViewById(R.id.ln_cash);
        //btn pay cash
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPay.setEnabled(false);
                PaidAmount= (String) txtTotalAmt.getText();
                PaidAmount=PaidAmount.replaceAll(",","");
                if(PaidAmount.equals("0.00") || PaidAmount.length()==0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Total Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                }else{
                    SaveTrn fnsave=new SaveTrn(getActivity(),"IS",Doc1No,"");
                    fnsave.execute();
                    btnPay.setEnabled(true);
                    txtDoc1No.setText("");
                }
            }
        });
        //readyPrinter();
        return view;
    }
    private void readyPrinter(){
        /*DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        Cursor c=db.getAllSeting();
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            Log.d("RESULT", ""+RunNo);
            IPAddress = c.getString(1);
            UserName = c.getString(2);
            Password = c.getString(3);
            DBName = c.getString(4);
        }
        String queryPrint="Select TypePrinter";
        Cursor cPrinter=db.getSettingPrint();
        while (cPrinter.moveToNext()) {
            TypePrinter = cPrinter.getString(1);
            NamePrinter = cPrinter.getString(2);
            IPPrinter = cPrinter.getString(3);
            UUID = cPrinter.getString(4);
        }
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else{
            //
        }
        db.closeDB();*/
    }
    private void fnchecktotal(){
        String TotalAmt="";
        String TotalQty="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select sum(HCLineAmt + HCDiscount) as TotalAmt, sum(QtyOUT) as TotalQty " +
                " from cloud_inventory_dt where DocType='IS' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Double dTotalAmt        = cRunNo.getDouble(0);
            Double dTotalQty   = cRunNo.getDouble(1);
            TotalAmt=String.format(Locale.US, "%,.2f", dTotalAmt);
            TotalQty=String.format(Locale.US, "%,.2f", dTotalQty);
        }
        txtTotalAmt.setText(TotalAmt);
        txtTotalQty.setText(TotalQty);
    }
    private void retLastNo(){
        String Prefix="";
        String LastNo="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select Prefix,LastNo from sys_runno_dt where RunNoCode='IS' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Prefix= cRunNo.getString(0);
            LastNo= cRunNo.getString(1);
        }
        Doc1No=Prefix+LastNo;
        txtDoc1No.setText(Doc1No);
        db.closeDB();
    }

    public void refreshPayment() {
        retLastNo();
        CheckPaidAmt chekpaid=new CheckPaidAmt(getActivity(),"IS");
        fnchecktotal();
        isTotal=chekpaid.fncheckpaid();
        if(!isTotal.equals("error")){
            totalamt=isTotal;
        }else{
            totalamt="0.00";
        }
        txtAmountDue.setText(totalamt);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Fragment_Summary.this.refreshPayment();
            InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }



    public class CalcTotal extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public CalcTotal(Context c, String DocType) {

            this.c = c;
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
            if(result==null){
                Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
            }else{

            }
        }

        private String downloadData() {
            try {
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
                if (DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {

                    }
                }else{
                    String query="select ";
                }
                return z;
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            return z;
        }
    }
}
