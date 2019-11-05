package com.skybiz.wms02.m_ListUOM;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;

import com.skybiz.wms02.MyKeyboard;
import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogUOM extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtQty;
    MyKeyboard keyboard;
    String RunNo,ItemCode,D_ateTime,uFrom;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_uom, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtQty=(EditText)view.findViewById(R.id.txtQty);
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

        InputConnection ic=txtQty.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtQty.setTextIsSelectable(true);
        txtQty.setInputType(0);

        return view;
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
}
