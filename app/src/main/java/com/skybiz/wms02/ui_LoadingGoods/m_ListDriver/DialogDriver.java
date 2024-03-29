package com.skybiz.wms02.ui_LoadingGoods.m_ListDriver;

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
import com.skybiz.wms02.ui_LoadingGoods.LoadingGoodsOut;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogDriver extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svChecker;
    String DocType;
    Spinner spSearchBy;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_checker, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvChecker);
        svChecker=(SearchView)view.findViewById(R.id.svChecker);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby3_array, android.R.layout.simple_spinner_item);
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
        svChecker.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        getDialog().setTitle("List of Driver");
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DonwloadDriver donwloadChecker=new DonwloadDriver(getActivity(),SearchBy, Keyword,
                rv, DialogDriver.this);
        donwloadChecker.execute();
    }
    public void setDriver(String DriverCode,String DriverName){
        if(DocType.equals("OUT")){
            ((LoadingGoodsOut)getActivity()).setDriver(DriverCode,DriverName);
            dismiss();
        }else if(DocType.equals("GRNDO")){
            //((GRNDO)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("RS")){
          //  ((ReceiptStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("TS")){
           // ((TransferStock)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("GRN2")){
           // ((GRN_Receiving)getActivity()).setCustomerBar(CusCode);
        }
    }


}
