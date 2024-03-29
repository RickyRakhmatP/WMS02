package com.skybiz.wms02.ui_ReceiptStock;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Detail.AddItem_BySearch;
import com.skybiz.wms02.m_Detail.DownloaderOrder;


public class Fragment_Detail extends Fragment {
    View view;
    RecyclerView rvDetail;
    private GridLayoutManager lLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retDetail();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        search=(SearchView)view.findViewById(R.id.search);
        search.setIconified(false);
        search.requestFocusFromTouch();
        //fnactivsearch();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String ItemCode) {
                callSearch(ItemCode);
                search.setQuery("", true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String ItemCode) {
                fnAddSearch(getActivity(),ItemCode);
            }
        });
        retDetail();
        return view;
    }

    private void retDetail(){
        rvDetail=(RecyclerView)view.findViewById(R.id.rvDetail);
        rvDetail.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvDetail.setLayoutManager(lLayout);
        rvDetail.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder dOrder=new DownloaderOrder(getActivity(),"RS", rvDetail,getActivity().getSupportFragmentManager());
        dOrder.execute();
    }

    public void fnAddSearch(Context c, String ItemCode){
        String vQty="1";
        AddItem_BySearch fnaddsearch=new AddItem_BySearch(c, "RS", ItemCode,vQty);
        fnaddsearch.execute();
    }
}
