package com.skybiz.wms02.ui_LoadingGoods.m_ListLoading;

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
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsIn;
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsOut;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogLoading extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svLoading;
    String D_ate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_loading, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvChecker);
        svLoading=(SearchView)view.findViewById(R.id.svLoading);
        D_ate=this.getArguments().getString("DATE_KEY");
        svLoading.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        getDialog().setTitle("List of Loading Header");
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DonwloadLoading donwloadChecker=new DonwloadLoading(getActivity(),D_ate, Keyword,
                rv, DialogLoading.this);
        donwloadChecker.execute();
    }
    public void setHeader(String RunNo,String CheckerCode, String D_ateTime ){
        ((LoadingGoodsIn)getActivity()).setLoading(RunNo,CheckerCode,D_ateTime);
        dismiss();
    }


}
