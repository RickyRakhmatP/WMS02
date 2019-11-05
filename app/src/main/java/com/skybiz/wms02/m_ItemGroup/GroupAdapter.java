package com.skybiz.wms02.m_ItemGroup;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.skybiz.wms02.m_DataObject.Spacecraft;
import java.util.ArrayList;
import  com.skybiz.wms02.R;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

/**
 * Created by 7 on 14/11/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {
    Context c;
    String DocType;
    ArrayList<Spacecraft> spacecrafts;
    int row_index;

    public GroupAdapter(Context c, String DocType, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemgroup,parent,false);
        return new GroupHolder(v);

    }

    @Override
    public void onBindViewHolder(GroupHolder holder,final int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.btnGroup.setText(spacecraft.getDescription());
        holder.btnGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                row_index=position;
                notifyDataSetChanged();
            }
        });
        if(row_index==position){
            holder.btnGroup.setBackgroundColor(Color.parseColor("#00C851"));
            if(DocType.equals("IS")) {
                ((IssueStock) c).retItem(spacecraft.getItemGroup());
            }else if(DocType.equals("GRNDO")){
                ((GRNDO) c).retItem(spacecraft.getItemGroup());
            }else if(DocType.equals("RS")){
                ((ReceiptStock) c).retItem(spacecraft.getItemGroup());
            }else if(DocType.equals("TS")){
                ((TransferStock) c).retItem(spacecraft.getItemGroup());
            }
        } else{
            holder.btnGroup.setBackgroundColor(Color.parseColor("#007E33"));
        }
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
