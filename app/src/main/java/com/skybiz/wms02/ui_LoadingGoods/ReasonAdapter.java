package com.skybiz.wms02.ui_LoadingGoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Reason;


/**
 * Created by 7 on 07/12/2017.
 */

public class ReasonAdapter extends BaseAdapter {
    Context c;
    ArrayList<Spacecraft_Reason> spacecrafts;
    LayoutInflater inflater;

    public ReasonAdapter(Context c, ArrayList<Spacecraft_Reason> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;

        //INITIALIE
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return spacecrafts.size();
    }

    @Override
    public Object getItem(int position) {
        return spacecrafts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.model_bay,parent,false);
        }
        TextView txtReason= (TextView) convertView.findViewById(R.id.vBay);
        txtReason.setText(spacecrafts.get(position).getReason());
        //descTxt.setText(spacecrafts.get(position).getDescription());
        //ITEM CLICKS
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(c,spacecrafts.get(position).getDoc1No(),Toast.LENGTH_SHORT).show();
            }
        });*/
        return convertView;
    }
}
