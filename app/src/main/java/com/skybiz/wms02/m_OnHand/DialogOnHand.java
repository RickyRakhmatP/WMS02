package com.skybiz.wms02.m_OnHand;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

import java.util.concurrent.ExecutionException;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogOnHand extends DialogFragment {
    View view;
    TextView txtItemCode,txtLocationCode,txtOnHand;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_onhand, container, false);
        txtItemCode=(TextView)view.findViewById(R.id.txtItemCode);
        txtLocationCode=(TextView)view.findViewById(R.id.txtLocationCode);
        txtOnHand=(TextView)view.findViewById(R.id.txtOnHand);
        final String ItemCode=this.getArguments().getString("ITEMCODE_KEY");
        final String LocationCode=this.getArguments().getString("LOCATIONCODE_KEY");
        final String QtyCur=this.getArguments().getString("QTYCUR_KEY");
        txtItemCode.setText(ItemCode);
        txtLocationCode.setText(LocationCode);
        calcOnHand(ItemCode,LocationCode,QtyCur);
        return view;
    }

    private void  calcOnHand(String ItemCode,String LocationCode,String QtyCur){
        CalculateOnHand calculateOnHand=new CalculateOnHand(getActivity(),ItemCode,LocationCode,QtyCur);
        try {
            String TotalQty= calculateOnHand.execute().get();
            txtOnHand.setText(TotalQty);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
