package com.skybiz.wms02.ui_PickingList.m_List;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.SetDefaultTax;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import com.skybiz.wms02.m_DataObject.Spacecraft_Dum;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Detail.AddItem;
import com.skybiz.wms02.m_Detail.EditItem;
import com.skybiz.wms02.ui_PickingList.PickingList;

import java.util.ArrayList;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    Context c;
    ArrayList<Spacecraft_Dum> spacecrafts;

    public ListAdapter(Context c, ArrayList<Spacecraft_Dum> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_dum,parent,false);
        return new ListHolder(v);
    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecraft_Dum spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vN_o.setText(spacecraft.getN_o());
        final String Qty=spacecraft.getQty();
        if(Qty.equals("1")){
            holder.vQty.setText("");
            holder.vQty.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
        }else{
            holder.vQty.setText("X");
        }
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delRow(spacecraft.getRunNo());
            }
        });

    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
    private void delRow(String RunNo){
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String vDel="delete from tb_dumpicking  where RunNo='"+RunNo+"' ";
            db.exeQuery(vDel);
            ((PickingList)c).loadList();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
}
