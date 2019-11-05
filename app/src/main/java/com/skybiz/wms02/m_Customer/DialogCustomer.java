package com.skybiz.wms02.m_Customer;

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
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_GRNReceiving.GRN_Receiving;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_SupInv.SupplierInvoice;
import com.skybiz.wms02.ui_TransferStock.TransferStock;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogCustomer extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svSupplier;
    String DocType;
    String FinCatCode;
    Spinner spSearchBy;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_suplier, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvSupplier);
        svSupplier=(SearchView)view.findViewById(R.id.svSupplier);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby2_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchBy.setAdapter(adapter);
        spSearchBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Item=adapterView.getItemAtPosition(i).toString();
                if(Item.equals("Show All")){
                    refresh("");
                }
                //Log.d("select spinner",Item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        svSupplier.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        setHeader();
        refresh("");
        return view;
    }
    private void setHeader(){
        String N_ame="";
        if(DocType.equals("IS")){
            N_ame       ="Customer";
            FinCatCode  ="B55";
        }else if(DocType.equals("GRNDO")){
            N_ame       ="Supplier";
            FinCatCode  ="B70";
        }else if(DocType.equals("RS")){
            N_ame="Supplier";
            FinCatCode  ="B70";
        }else if(DocType.equals("TS")){
            N_ame       ="Customer";
            FinCatCode  ="All";
        }else if(DocType.equals("GRN2")){
            N_ame       ="Supplier";
            FinCatCode   ="B70";
        }else if(DocType.equals("SupInv")){
            N_ame       ="Supplier";
            FinCatCode  ="B70";
        }

        getDialog().setTitle("List of "+N_ame);
    }

    public void refresh(String Keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadCustomer dCustomer=new DownloadCustomer(getActivity(),FinCatCode, SearchBy,Keyword,
                rv, DialogCustomer.this);
        dCustomer.execute();
    }
    public void setCustomer(String CusCode, String CusName, String TermCode, String D_ay){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vDel="delete from tb_member";
        db.exeQuery(vDel);
        String vInsert="insert into tb_member(CusCode, CusName, TermCode, D_ay, SalesPersonCode)" +
                "values('"+CusCode+"', '"+CusName+"', '"+TermCode+"', '"+D_ay+"', '')";
        db.exeQuery(vInsert);
        db.closeDB();
        setBar(CusCode,CusName);
    }
    private void setBar(String CusCode, String CusName){
        if(DocType.equals("IS")){
            ((IssueStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("GRNDO")){
            ((GRNDO)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("RS")){
           ((ReceiptStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("TS")){
            ((TransferStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("GRN2")){
            ((GRN_Receiving)getActivity()).setCustomerBar(CusCode);
        }else if(DocType.equals("SupInv")){
            ((SupplierInvoice)getActivity()).setHeader("",CusCode,CusName,"");
        }
        dismiss();
    }


}
