package com.skybiz.wms02.ui_Setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_Summary.CheckPaidAmt;
import com.skybiz.wms02.m_Summary.SaveTrn;

import java.sql.Connection;
import java.util.Locale;


public class Fragment_Prefix extends Fragment {
    View view;
    EditText PrefixGRNDO,LastNoGRNDO,PrefixIS,LastNoIS,PrefixRS,LastNoRS,
    PrefixTS,LastNoTS;
    Button btnBack,btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_prefix, container, false);

        PrefixGRNDO=(EditText)view.findViewById(R.id.PrefixGRNDO);
        LastNoGRNDO=(EditText)view.findViewById(R.id.LastNoGRNDO);
        PrefixIS=(EditText)view.findViewById(R.id.PrefixIS);
        LastNoIS=(EditText)view.findViewById(R.id.LastNoIS);
        PrefixRS=(EditText)view.findViewById(R.id.PrefixRS);
        LastNoRS=(EditText)view.findViewById(R.id.LastNoRS);
        PrefixTS=(EditText)view.findViewById(R.id.PrefixTS);
        LastNoTS=(EditText)view.findViewById(R.id.LastNoTS);

        btnBack=(Button)view.findViewById(R.id.btnBack);
        btnSave=(Button)view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(mainIntent);
            }
        });
        retData();
        return view;
    }
    private void retData(){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String all="select Prefix,LastNo,RunNoCode from sys_runno_dt";
        Cursor rsAll=db.getQuery(all);
        while(rsAll.moveToNext()){
            String RunNoCode=rsAll.getString(2);
            if(RunNoCode.equals("GRNDO")){
                PrefixGRNDO.setText(rsAll.getString(0));
                LastNoGRNDO.setText(rsAll.getString(1));
            }else if(RunNoCode.equals("IS")){
                PrefixIS.setText(rsAll.getString(0));
                LastNoIS.setText(rsAll.getString(1));
            }else if(RunNoCode.equals("RS")){
                PrefixRS.setText(rsAll.getString(0));
                LastNoRS.setText(rsAll.getString(1));
            }else if(RunNoCode.equals("TS")){
                PrefixTS.setText(rsAll.getString(0));
                LastNoTS.setText(rsAll.getString(1));
            }
        }
        db.closeDB();
    }
    private void save(){
        String vPrefixGRNDO=PrefixGRNDO.getText().toString();
        String vLastNoGRNDO=LastNoGRNDO.getText().toString();
        String vPrefixIS=PrefixIS.getText().toString();
        String vLastNoIS=LastNoIS.getText().toString();
        String vPrefixRS=PrefixRS.getText().toString();
        String vLastNoRS=LastNoRS.getText().toString();
        String vPrefixTS=PrefixTS.getText().toString();
        String vLastNoTS=LastNoTS.getText().toString();
        savePrefix("GRNDO",vPrefixGRNDO,vLastNoGRNDO);
        savePrefix("IS",vPrefixIS,vLastNoIS);
        savePrefix("RS",vPrefixRS,vLastNoRS);
        savePrefix("TS",vPrefixTS,vLastNoTS);
        Toast.makeText(getContext(),"Prefix has been saved", Toast.LENGTH_SHORT).show();
    }

    private void savePrefix(String DocType,String Prefix,String LastNo){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String query="Select Count(*)as numrows from sys_runno_dt where RunNoCode='"+DocType+"' ";
        Cursor rsData=db.getQuery(query);
        int numrows=0;
        while(rsData.moveToNext()){
            numrows=rsData.getInt(0);
        }
        if(numrows==0){
            String insert="insert into sys_runno_dt (Prefix,LastNo,RunNoCode)values(" +
                    "'"+Prefix+"', '"+LastNo+"', '"+DocType+"')";
            db.exeQuery(insert);
        }else{
            String update="update  sys_runno_dt set Prefix='"+Prefix+"',LastNo='"+LastNo+"' where RunNoCode='"+DocType+"' ";
            db.exeQuery(update);
        }
        db.closeDB();
    }
}
