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
import com.skybiz.wms02.m_Supplier.DownloadSupplier;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogOnHandL extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    TextView txtTotalQty;
    RecyclerView rv;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_onhandl, container, false);
        String ItemCode=this.getArguments().getString("ITEMCODE_KEY");
        txtTotalQty=(TextView)view.findViewById(R.id.txtTotalQty);
        getDialog().setTitle("List OnHand");
        refresh(ItemCode);
        return view;
    }

    public void refresh(String ItemCode) {
        rv=(RecyclerView) view.findViewById(R.id.rvOnHand);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadOnHand onHand=new DownloadOnHand(getActivity(),ItemCode,rv, DialogOnHandL.this);
        onHand.execute();
    }
    public void setTotal(String TotalQty) {
        txtTotalQty.setText(TotalQty);
    }
}
