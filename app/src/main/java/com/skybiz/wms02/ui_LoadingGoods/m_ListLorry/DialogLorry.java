package com.skybiz.wms02.ui_LoadingGoods.m_ListLorry;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsOut;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogLorry extends DialogFragment {
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
        getDialog().setTitle("List of Lorry");
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DonwloadLorry donwloadLorry=new DonwloadLorry(getActivity(),Keyword,rv, DialogLorry.this);
        donwloadLorry.execute();
    }
    public void setLorry(String LorryNo){
        if(DocType.equals("OUT")){
            ((LoadingGoodsOut)getActivity()).setLorry(LorryNo);
            dismiss();
        }else if(DocType.equals("GRNDO")){
            //((GRNDO)getActivity()).setCustomerBar(CusName);
        }
    }


}
