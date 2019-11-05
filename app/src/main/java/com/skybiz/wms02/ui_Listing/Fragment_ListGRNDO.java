package com.skybiz.wms02.ui_Listing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Listing.DownloaderList;


public class Fragment_ListGRNDO extends Fragment {
    View view;
    RecyclerView rvList;
    SearchView svList;
    private GridLayoutManager lLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listing, container, false);
        rvList=(RecyclerView)view.findViewById(R.id.rvList);
        svList=(SearchView)view.findViewById(R.id.svList);
        retList();
        return view;
    }
    private void retList(){
        rvList.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvList.setLayoutManager(lLayout);
        rvList.setItemAnimator(new DefaultItemAnimator());
        DownloaderList dList=new DownloaderList(getActivity(),"GRNDO", rvList);
        dList.execute();
    }
}
