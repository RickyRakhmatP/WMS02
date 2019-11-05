package com.skybiz.wms02.ui_PrintInvLabel.m_ListUser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Spacecraft_Customer;
import com.skybiz.wms02.m_DataObject.Spacecraft_User;
import com.skybiz.wms02.m_Supplier.DialogSupplier;
import com.skybiz.wms02.ui_PrintInvLabel.PrintInvLabel;

import java.util.ArrayList;

/**
 * Created by 7 on 27/10/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

    Context c;
    ArrayList<Spacecraft_User> spacecrafts;

    public UserAdapter(Context c, ArrayList<Spacecraft_User> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_user,parent,false);
        return new UserHolder(v);
    }
    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        final Spacecraft_User spacecraft=spacecrafts.get(position);
        holder.vUserName.setText(spacecraft.getUserName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String UserCode=spacecraft.getUserCode();
                ((PrintInvLabel)c).setInvoice(UserCode);
                //dialogSupplier.setCustomer(CusCode,CusName,TermCode,D_ay);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
