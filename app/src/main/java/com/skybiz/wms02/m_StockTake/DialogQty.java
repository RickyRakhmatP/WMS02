package com.skybiz.wms02.m_StockTake;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skybiz.wms02.MainActivity;
import com.skybiz.wms02.MyKeyboard;
import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.m_Detail.Dialog_Edit;
import com.skybiz.wms02.m_ListUOM.DownloaderUOM;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogQty extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtQty;
    MyKeyboard keyboard;
    String RunNo,ItemCode,D_ateTime,uFrom,T_ype;
    LinearLayout lnChangeQty,lnChangeUOM;
    Button btnChangeUOM,btnChangeQty,btnClose;
    RecyclerView rvUOM;
    private GridLayoutManager lLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_qty, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtQty=(EditText)view.findViewById(R.id.txtQty);
        T_ype=this.getArguments().getString("TYPE_KEY");
        uFrom=this.getArguments().getString("UFROM_KEY");
        RunNo=this.getArguments().getString("RUNNO_KEY");
        ItemCode=this.getArguments().getString("CODE_KEY");
        D_ateTime=this.getArguments().getString("DATETIME_KEY");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQty();
            }
        });
        keyboard=(MyKeyboard)view.findViewById(R.id.keyboard);
        lnChangeQty=(LinearLayout)view.findViewById(R.id.lnChangeQty);
        lnChangeUOM=(LinearLayout)view.findViewById(R.id.lnChangeUOM);
        btnChangeUOM=(Button)view.findViewById(R.id.btnChangeUOM);
        btnChangeQty=(Button)view.findViewById(R.id.btnChangeQty);
        btnClose=(Button)view.findViewById(R.id.btnClose);
        rvUOM=(RecyclerView) view.findViewById(R.id.rvUOM);

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

        btnChangeQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnshowQty();
            }
        });
        btnChangeUOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnshowUOM();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        InputConnection ic=txtQty.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtQty.setTextIsSelectable(true);
        txtQty.setInputType(0);
        initView();
        return view;
    }

    private void initView(){
        if(T_ype.equals("UOM")){
            fnshowUOM();
        }else if(T_ype.equals("Qty")){
            fnshowQty();
        }
    }
    //  <color name="colorPrimary">#42a5f5</color>
    //    <color name="colorPrimaryDark">#64b5f6</color>

    private void fnshowQty(){
        lnChangeUOM.setVisibility(View.GONE);
        lnChangeQty.setVisibility(View.VISIBLE);
        btnChangeUOM.setBackgroundColor(Color.parseColor("#42a5f5"));
        btnChangeQty.setBackgroundColor(Color.parseColor("#64b5f6"));
    }
    private void fnshowUOM(){
        lnChangeUOM.setVisibility(View.VISIBLE);
        lnChangeQty.setVisibility(View.GONE);
        btnChangeQty.setBackgroundColor(Color.parseColor("#42a5f5"));
        btnChangeUOM.setBackgroundColor(Color.parseColor("#64b5f6"));
        if(uFrom.equals("GRNDO")){
            Toast.makeText(getActivity(), "Module Change UOM cannot run on GRN", Toast.LENGTH_SHORT).show();
        }else{
            RetListUOM();
        }

    }
    private void changeQty(){
        String Qty=txtQty.getText().toString();
        if(uFrom.equals("ST")) {
            ((StockTake) getActivity()).editQty(RunNo, ItemCode, D_ateTime, Qty);
        }else if(uFrom.equals("GRNDO")){
            ((GRN_Receiving) getActivity()).editQty(RunNo, Qty);
        }
        dismiss();
    }

    private void RetListUOM(){
        rvUOM.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvUOM.setLayoutManager(lLayout);
        rvUOM.setItemAnimator(new DefaultItemAnimator());
        DownloaderUOM downloaderUOM=new DownloaderUOM(getActivity(),ItemCode, rvUOM, DialogQty.this);
        downloaderUOM.execute();
    }
    public void setUOM(String UOM,String UOMPrice,String UOMFactor){
        if(uFrom.equals("ST")) {
            ((StockTake) getActivity()).editUOM(RunNo, UOM, UOMPrice,UOMFactor);
        }else if(uFrom.equals("GRNDO")){

            //((GRN_Receiving) getActivity()).editQty(RunNo, Qty);
        }
        dismiss();
    }
}
