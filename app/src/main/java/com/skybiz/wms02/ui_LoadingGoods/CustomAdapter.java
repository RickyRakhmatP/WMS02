package com.skybiz.wms02.ui_LoadingGoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skybiz.wms02.R;

public class CustomAdapter extends BaseAdapter {
    Context c;
    private String[] data;
    LayoutInflater inflater;

    public CustomAdapter(Context c, String[] data) {
        this.c=c;
        this.data = data;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.model_bay,parent,false);
        }
        TextView txtReason= (TextView) convertView.findViewById(R.id.vBay);
        txtReason.setText(data[position]);
        return convertView;
    }
}
