package com.skybiz.wms02;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skybiz.wms02.m_Sync.SyncIN;
import com.skybiz.wms02.m_Sync.SyncOUT;

public class Synchronize extends AppCompatActivity {

    Button btnSyncIN,btnSyncOUT;
    ProgressBar pbSyncIN,pbSyncOUT;
    TextView txtProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_synchronize);
        getSupportActionBar().setTitle("Synchronize");
        btnSyncIN=(Button)findViewById(R.id.btnSyncIN);
        pbSyncIN=(ProgressBar)findViewById(R.id.pbSyncIN);

        btnSyncOUT=(Button)findViewById(R.id.btnSyncOUT);
        pbSyncOUT=(ProgressBar)findViewById(R.id.pbSyncOUT);
        txtProgress=(TextView)findViewById(R.id.txtProgress);

        btnSyncIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //eventSave();
                btnSyncIN.setBackgroundColor(Color.BLACK);
                btnSyncIN.setEnabled(false);
                SyncIN syncIN=new SyncIN(Synchronize.this,pbSyncIN);
                syncIN.execute();
            }
        });

        btnSyncOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSyncOUT.setBackgroundColor(Color.BLACK);
                btnSyncOUT.setEnabled(false);
                eventOUT();
                SyncOUT syncOUT=new SyncOUT(Synchronize.this);
                syncOUT.execute();
            }
        });
    }
    public void afterSave(){
       // pbSyncIN.setVisibility(View.GONE);
    }
    private void eventSave(){
       // pbSyncIN.setVisibility(View.VISIBLE);
    }

    public void setProgress(String vValue){
        txtProgress.setText(vValue);
    }
    public void afterOUT(){
        pbSyncOUT.setVisibility(View.GONE);
    }
    private void eventOUT(){
        pbSyncOUT.setVisibility(View.VISIBLE);
    }
}
