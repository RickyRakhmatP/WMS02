package com.skybiz.wms02.m_Location;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;
import com.skybiz.wms02.ui_SupInv.SupplierInvoice;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogLoc extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svLocation;
    String DocType,vDBStatus="0";
    FragmentManager fragmentManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_location, container, false);
        svLocation=(SearchView)view.findViewById(R.id.svLocation);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        vDBStatus=this.getArguments().getString("DBSTATUS_KEY");
        fragmentManager = getFragmentManager();
        svLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String CusCode) {
                callSearch(CusCode);
                // svCustomer.setQuery("", true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String LocationCode) {
                refresh(LocationCode);
            }
        });
        getDialog().setTitle("List of Location");
        refresh("");
        return view;
    }

    public void refresh(String LocationCode) {
        rv=(RecyclerView) view.findViewById(R.id.rvLocation);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderLoc dLoc=new DownloaderLoc(getActivity(),vDBStatus,rv,DialogLoc.this);
        dLoc.execute();
    }
    public void setLoc(String LocationCode){
        if(DocType.equals("ST")){
            ((StockTake)getActivity()).setLocation(LocationCode);
        }else if(DocType.equals("TS")){
            ((TransferStock)getActivity()).setSubTitle(LocationCode);
        }else if(DocType.equals("GRN2")){
            ((GRN_Receiving)getActivity()).setLocation(LocationCode);
        }else if(DocType.equals("SupInv")){
            ((SupplierInvoice)getActivity()).setHeader(LocationCode,"","","");
        }
        dismiss();
    }
}
