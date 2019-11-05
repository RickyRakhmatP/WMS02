package com.skybiz.wms02.m_ItemList;

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
import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.StockTake;
import com.skybiz.wms02.ui_GRNDO.GRNDO;
import com.skybiz.wms02.ui_IssueStock.IssueStock;
import com.skybiz.wms02.ui_ReceiptStock.ReceiptStock;
import com.skybiz.wms02.ui_StockEnquiry.StockEnquiry;
import com.skybiz.wms02.ui_TransferStock.TransferStock;


/**
 * Created by 7 on 17/01/2018.
 */

public class DialogItem extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svItem;
    String DocType;
    Spinner spSearchBy;
    TextView txtTotalItem;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_item, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvItem);
        svItem=(SearchView)view.findViewById(R.id.svItem);
        txtTotalItem=(TextView) view.findViewById(R.id.txtTotalItem);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby_array, android.R.layout.simple_spinner_item);
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
        svItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                callSearch(keyword);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String keyword) {
                refresh(keyword);
            }
        });
        svItem.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //refresh("");
                return false;
            }
        });
        getDialog().setTitle("List of Item");
       // refresh("");
        return view;
    }

    public void refresh(String keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderItem downloaderItem=new DownloaderItem(getActivity(),DocType,SearchBy,
                keyword,rv, DialogItem.this);
        downloaderItem.execute();
    }
    public void totalItem(String vTotal){
        txtTotalItem.setText("Total : "+vTotal);

    }
    public void setItem(){
        if(DocType.equals("IS")){
            ((IssueStock)getActivity()).retDetail();
        }else if(DocType.equals("RS")){
            ((ReceiptStock)getActivity()).retDetail();
        }else if(DocType.equals("TS")){
            ((TransferStock)getActivity()).retDetail();
        }else if(DocType.equals("GRNDO")){
            ((GRNDO)getActivity()).retDetail();
        }else if(DocType.equals("SE")){
           // ((StockEnquiry)getActivity()).retItem();
        }else if(DocType.equals("ST")){
            //((StockTake)getActivity()).setItem();
        }

        dismiss();
    }

}
