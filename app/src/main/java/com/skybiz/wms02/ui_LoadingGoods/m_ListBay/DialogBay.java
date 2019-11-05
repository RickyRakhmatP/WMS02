package com.skybiz.wms02.ui_LoadingGoods.m_ListBay;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsOut;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogBay extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svBay;
    String DocType;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_bay, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvBay);
        svBay=(SearchView)view.findViewById(R.id.svBay);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        svBay.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String Keyword) {
                callSearch(Keyword);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String Keyword) {
                refresh(Keyword);
            }
        });
        getDialog().setTitle("List of Bay");
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DonwloadBay donwloadBay=new DonwloadBay(getActivity(),Keyword,rv, DialogBay.this);
        donwloadBay.execute();
    }
    public void setBay(String Bay){
        if(DocType.equals("OUT")){
            ((LoadingGoodsOut)getActivity()).setBay(Bay);
            dismiss();
        }else if(DocType.equals("GRNDO")){
            //((GRNDO)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("RS")){
          //  ((ReceiptStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("TS")){
           // ((TransferStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("GRN2")){
           // ((GRN_Receiving)getActivity()).setCustomerBar(CusCode);
        }
    }


}
