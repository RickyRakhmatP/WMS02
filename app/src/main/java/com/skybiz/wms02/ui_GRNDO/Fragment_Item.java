package com.skybiz.wms02.ui_GRNDO;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Item.DownloaderItem;
import com.skybiz.wms02.m_ItemGroup.DownloaderGroup;


public class Fragment_Item extends Fragment {
    View view;
    RecyclerView rvItem,rvGroup;
    private GridLayoutManager lLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item, container, false);
        rvItem=(RecyclerView)view.findViewById(R.id.rvItem);
        rvGroup=(RecyclerView)view.findViewById(R.id.rvGroup);
        retItem("");
        retGroup();
        return view;
    }
    private void retItem(String ItemGroup){
        rvItem.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 2);
        rvItem.setLayoutManager(lLayout);
        rvItem.setItemAnimator(new DefaultItemAnimator());
        if(ItemGroup.equals("Miscellaneous")) {
           // DownloaderMisc dItem = new DownloaderMisc(getActivity(), "IS", rvItem);
           // dItem.execute();
        }else{
            DownloaderItem dItem = new DownloaderItem(getActivity(), "GRNDO", ItemGroup, rvItem);
            dItem.execute();
        }
    }
    private void retGroup(){
        rvGroup.setHasFixedSize(true);
        rvGroup.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvGroup.setItemAnimator(new DefaultItemAnimator());
        DownloaderGroup dGroup = new DownloaderGroup(getActivity(),"GRNDO", rvGroup);
        dGroup.execute();
    }
}
