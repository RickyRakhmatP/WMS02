package com.skybiz.wms02.ui_StockEnquiry;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_DataObject.Encode;
import com.skybiz.wms02.m_DataObject.SetUOM;
import com.skybiz.wms02.m_Database.Local.DBAdapter;
import com.skybiz.wms02.m_Database.Server.Connect_db;
import com.skybiz.wms02.m_Database.Server.Connector;
import com.skybiz.wms02.m_ItemList.DialogItem;
import com.skybiz.wms02.m_OnHand.DialogOnHandL;
import com.skybiz.wms02.m_Summary.CheckPaidAmt;
import com.skybiz.wms02.m_Summary.SaveTrn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Fragment_Stock_Enquiry extends Fragment {
    View view;
    EditText txtItemCode,txtDescription;
    Button btnSearchCode,btnSearchDesc,btnOnHand;
    TextView txtAlternateItem,txtUOM,txtUOM1,
            txtUOM2,txtUOM3,txtUOM4,
            txtUOMPrice1,txtUOMPrice2,txtUOMPrice3,
            txtUOMPrice4,txtUnitPrice,txtUOMFactor1,
            txtUOMFactor2,txtUOMFactor3,txtUOMFactor4,
            txtFactorQty,txtRemark,txtTaxCode,
            txtSalesTaxCode,txtRetailTaxCode,chkSuspendedYN,txtItemGroup;

    private Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stock_enquiry, container, false);
        txtItemCode=(EditText)view.findViewById(R.id.txtItemCode);
        txtDescription=(EditText)view.findViewById(R.id.txtDescription);
        txtAlternateItem=(TextView) view.findViewById(R.id.txtAlternateItem);
        txtItemGroup=(TextView) view.findViewById(R.id.txtItemGroup);
        txtUOM=(TextView) view.findViewById(R.id.txtUOM);
        txtUOM1=(TextView) view.findViewById(R.id.txtUOM1);
        txtUOM2=(TextView) view.findViewById(R.id.txtUOM2);
        txtUOM3=(TextView) view.findViewById(R.id.txtUOM3);
        txtUOM4=(TextView) view.findViewById(R.id.txtUOM4);
        txtUOMPrice1=(TextView) view.findViewById(R.id.txtUOMPrice1);
        txtUOMPrice2=(TextView) view.findViewById(R.id.txtUOMPrice2);
        txtUOMPrice3=(TextView) view.findViewById(R.id.txtUOMPrice3);
        txtUOMPrice4=(TextView) view.findViewById(R.id.txtUOMPrice4);
        txtUOMFactor1=(TextView) view.findViewById(R.id.txtUOMFactor1);
        txtUOMFactor2=(TextView) view.findViewById(R.id.txtUOMFactor2);
        txtUOMFactor3=(TextView) view.findViewById(R.id.txtUOMFactor3);
        txtUOMFactor4=(TextView) view.findViewById(R.id.txtUOMFactor4);
        txtFactorQty=(TextView) view.findViewById(R.id.txtFactorQty);
        txtUnitPrice=(TextView) view.findViewById(R.id.txtUnitPrice);
        txtRemark=(TextView) view.findViewById(R.id.txtRemark);
        txtTaxCode=(TextView) view.findViewById(R.id.txtTaxCode);
        txtSalesTaxCode=(TextView) view.findViewById(R.id.txtSalesTaxCode);
        txtRetailTaxCode=(TextView) view.findViewById(R.id.txtRetailTaxCode);
        chkSuspendedYN=(TextView) view.findViewById(R.id.chkSuspendedYN);
        btnSearchCode=(Button)view.findViewById(R.id.btnSearchCode);
        btnSearchDesc=(Button)view.findViewById(R.id.btnSearchDesc);
        btnOnHand=(Button)view.findViewById(R.id.btnOnHand);
        btnSearchCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnsearch("ByCode");
            }
        });
        btnSearchDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemList();
                //fnsearch("ByDesc");
            }
        });
        btnOnHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retOnHand();
            }
        });
        txtItemCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String ItemCode=txtItemCode.getText().toString();
                    if(!ItemCode.isEmpty()){
                        fnsearch("ByCode");
                    }else{
                        txtItemCode.requestFocus();
                    }
                }
                return false;
            }
        });
        txtDescription.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String Description=txtDescription.getText().toString();
                    if(!Description.isEmpty()){
                        fnsearch("ByDesc");
                    }else{
                        txtDescription.requestFocus();
                    }
                }
                return false;
            }
        });

       // bundle = this.getArguments();
        //if (bundle!=null) {
            //String result = bundle.getString("JSON_KEY");
            //setItems(result);
       // }
        return view;
    }
    private void setItems(String result){
        try {
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                txtItemCode.setText(jo.getString("ItemCode"));
                txtItemGroup.setText(jo.getString("ItemGroup"));
                txtDescription.setText(jo.getString("Description"));
                txtTaxCode.setText(jo.getString("PurchaseTaxCode"));
                txtRetailTaxCode.setText(jo.getString("RetailTaxCode"));
                txtSalesTaxCode.setText(jo.getString("SalesTaxCode"));
                txtUnitPrice.setText(jo.getString("UnitPrice"));
                Double dPrice1=parseToDb(jo.getString("UOMPrice1"));
                Double dPrice2=parseToDb(jo.getString("UOMPrice2"));
                Double dPrice3=parseToDb(jo.getString("UOMPrice3"));
                Double dPrice4=parseToDb(jo.getString("UOMPrice4"));
                if(dPrice1>1) {
                    txtUOMPrice1.setText(jo.getString("UOMPrice1"));
                }
                if(dPrice2>1) {
                    txtUOMPrice2.setText(jo.getString("UOMPrice2"));
                }
                if(dPrice3>1) {
                    txtUOMPrice3.setText(jo.getString("UOMPrice3"));
                }
                if(dPrice4>1) {
                    txtUOMPrice4.setText(jo.getString("UOMPrice4"));
                }
                txtUOM.setText(jo.getString("UOM"));
                txtUOM1.setText(jo.getString("UOM1"));
                txtUOM2.setText(jo.getString("UOM2"));
                txtUOM3.setText(jo.getString("UOM3"));
                txtUOM4.setText(jo.getString("UOM4"));
                txtFactorQty.setText("1.00");
                Double dFactor1=parseToDb(jo.getString("UOMFactor1"));
                Double dFactor2=parseToDb(jo.getString("UOMFactor2"));
                Double dFactor3=parseToDb(jo.getString("UOMFactor3"));
                Double dFactor4=parseToDb(jo.getString("UOMFactor4"));
                if(dFactor1>1) {
                    txtUOMFactor1.setText(jo.getString("UOMFactor1"));
                }
                if(dFactor2>1) {
                    txtUOMFactor2.setText(jo.getString("UOMFactor2"));
                }
                if(dFactor3>1) {
                    txtUOMFactor3.setText(jo.getString("UOMFactor3"));
                }
                if(dFactor4>1) {
                    txtUOMFactor4.setText(jo.getString("UOMFactor4"));
                }
                txtAlternateItem.setText(jo.getString("AlternateItem"));
                txtRemark.setText(jo.getString("Remark"));
                if(jo.getString("SuspendedYN").equals("1")){
                    chkSuspendedYN.setText("[Suspended]");
                }else{
                    chkSuspendedYN.setText("[Active]");
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void showItemList(){
        Bundle b=new Bundle();
        b.putString("DOCTYPE_KEY","SE");
        DialogItem dialogItem = new DialogItem();
        dialogItem.setArguments(b);
        dialogItem.show(getActivity().getSupportFragmentManager(), "mListItem");
    }
    private void retOnHand(){
        String ItemCode=txtItemCode.getText().toString();
        Bundle b=new Bundle();
        b.putString("ITEMCODE_KEY",ItemCode);
        DialogOnHandL dialogOnHand = new DialogOnHandL();
        dialogOnHand.setArguments(b);
        dialogOnHand.show(getActivity().getSupportFragmentManager(), "mOnHand");
    }
    private void fnsearch(String SearchBy){
        String ItemCode=txtItemCode.getText().toString();
        String ItemDesc=txtDescription.getText().toString();

        if(SearchBy.equals("ByCode")){
            if(ItemCode.isEmpty()){
                Toast.makeText(getActivity(),"Item Code Cannot Emtpy", Toast.LENGTH_SHORT).show();
            }else {
                RetItem retItem = new RetItem(getActivity(), SearchBy, ItemCode);
                retItem.execute();
            }
        }else{
            if(ItemDesc.isEmpty()){
                Toast.makeText(getActivity(),"Description Code Cannot Emtpy", Toast.LENGTH_SHORT).show();
            }else {
                RetItem retItem = new RetItem(getActivity(), SearchBy, ItemDesc);
                retItem.execute();
            }
        }

    }

    public class RetItem extends AsyncTask<Void,Void,String>{
        Context c;
        String SearchBy,Keyword;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,CharType;
        String Description,UnitPrice,UOM,FactorQty,
                TaxCode,SalesTaxCode,RetailTaxCode,PurchaseTaxCode,
                AlternateItem,SuspendedYN,UOM1,
                UOM2,UOM3,UOM4,
                UOMFactor1="0",UOMFactor2="0",UOMFactor3="0",
                UOMFactor4="0",ItemCode,UOMPrice1="0",
                UOMPrice2="0",UOMPrice3="0",UOMPrice4="0",
                Remark,ItemGroup;

        public RetItem(Context c, String SearchBy, String Keyword) {
            this.c = c;
            this.SearchBy = SearchBy;
            this.Keyword = Keyword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.downloadData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Failure, No data retrieve", Toast.LENGTH_SHORT).show();
            }else{
                txtItemCode.setText(ItemCode);
                txtItemGroup.setText(ItemGroup);
                txtDescription.setText(Description);
                txtTaxCode.setText(TaxCode);
                txtRetailTaxCode.setText(RetailTaxCode);
                txtSalesTaxCode.setText(SalesTaxCode);
                txtUnitPrice.setText(UnitPrice);
                Double dPrice1=parseToDb(UOMPrice1);
                Double dPrice2=parseToDb(UOMPrice2);
                Double dPrice3=parseToDb(UOMPrice3);
                Double dPrice4=parseToDb(UOMPrice4);
                if(dPrice1>1) {
                    txtUOMPrice1.setText(UOMPrice1);
                }
                if(dPrice2>1) {
                    txtUOMPrice2.setText(UOMPrice2);
                }
                if(dPrice3>1) {
                    txtUOMPrice3.setText(UOMPrice3);
                }
                if(dPrice4>1) {
                    txtUOMPrice4.setText(UOMPrice4);
                }
                txtUOM.setText(UOM);
                txtUOM1.setText(UOM1);
                txtUOM2.setText(UOM2);
                txtUOM3.setText(UOM3);
                txtUOM4.setText(UOM4);
                txtFactorQty.setText("1.00");
                Double dFactor1=parseToDb(UOMFactor1);
                Double dFactor2=parseToDb(UOMFactor2);
                Double dFactor3=parseToDb(UOMFactor3);
                Double dFactor4=parseToDb(UOMFactor3);
                if(dFactor1>1) {
                    txtUOMFactor1.setText(UOMFactor1);
                }
                if(dFactor2>1) {
                    txtUOMFactor2.setText(UOMFactor2);
                }
                if(dFactor3>1) {
                    txtUOMFactor3.setText(UOMFactor3);
                }
                if(dFactor4>1) {
                    txtUOMFactor4.setText(UOMFactor4);
                }
                txtAlternateItem.setText(AlternateItem);
                txtRemark.setText(Remark);
                if(SuspendedYN.equals("1")){
                    chkSuspendedYN.setText("[Suspended]");
                }else{
                    chkSuspendedYN.setText("[Active]");
                }
            }
        }

        private String downloadData() {
            try {
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "Port,DBName,DBStatus," +
                        "CharSetType from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    Port = curSet.getString(3);
                    DBName = curSet.getString(4);
                    DBStatus=curSet.getString(5);
                    CharType=curSet.getString(6);
                }
                String vClause="";
                if(SearchBy.equals("ByCode")){
                   vClause=" and M.ItemCode='"+Keyword+"' ";
                }else{
                    vClause=" and M.Description like '%"+Keyword+"%' ";
                }
                String sql = "select M.ItemCode, M.Description, M.ItemGroup, M.UnitPrice," +
                        " M.UnitCost, M.DefaultUOM, M.UOM, IFNULL(M.UOM1,'') as UOM1, " +
                        " IFNULL(M.UOM2,'') as UOM2, IFNULL(M.UOM3,'') as UOM3, IFNULL(M.UOM4,'') as UOM4, IFNULL(M.UOMPrice1,0) as UOMPrice1," +
                        " IFNULL(M.UOMPrice2,0) as UOMPrice2,  IFNULL(M.UOMPrice3,0)as UOMPrice3,  IFNULL(M.UOMPrice4,0)as UOMPrice4,  IFNULL(M.UOMFactor1,0)as UOMFactor1," +
                        " IFNULL(M.UOMFactor2,0) as UOMFactor2, IFNULL(M.UOMFactor3,0) as UOMFactor3, IFNULL(M.UOMFactor4,0) as UOMFactor4, M.AnalysisCode1," +
                        " M.AnalysisCode2, M.AnalysisCode3, M.AnalysisCode4, M.AnalysisCode5," +
                        " M.MAXSP, M.MAXSP1, M.MAXSP2, M.MAXSP3," +
                        " M.MAXSP4, M.SalesTaxCode, M.RetailTaxCode, M.PurchaseTaxCode," +
                        " M.FixedPriceYN, M.SuspendedYN, M.AlternateItem," +
                        " U.AttachmentDescription " +
                        " from stk_master M left join stk_userdefine_trn U ON M.ItemCode=U.Doc1No " +
                        " where M.SuspendedYN='0' "+vClause+" ";
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if(conn!=null){
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (statement.execute(sql)) {
                            ResultSet rsItem = statement.getResultSet();
                            while (rsItem.next()) {
                                ItemCode            =rsItem.getString(1);
                                ItemGroup           =Encode.setChar(CharType,rsItem.getString(3));
                                Description         =Encode.setChar(CharType,rsItem.getString(2));
                                UnitPrice           =twoDecimal(rsItem.getDouble(4));
                                UOM                 =Encode.setChar(CharType,rsItem.getString(7));
                                UOM1                =Encode.setChar(CharType,rsItem.getString(8));
                                UOM2                =Encode.setChar(CharType,rsItem.getString(9));
                                UOM3                =Encode.setChar(CharType,rsItem.getString(10));
                                UOM4                =Encode.setChar(CharType,rsItem.getString(11));
                                UOMPrice1           =twoDecimal(rsItem.getDouble(12));
                                UOMPrice2           =twoDecimal(rsItem.getDouble(13));
                                UOMPrice3           =twoDecimal(rsItem.getDouble(14));
                                UOMPrice4           =twoDecimal(rsItem.getDouble(15));
                                UOMFactor1          =twoDecimal(rsItem.getDouble(16));
                                UOMFactor2          =twoDecimal(rsItem.getDouble(17));
                                UOMFactor3          =twoDecimal(rsItem.getDouble(18));
                                UOMFactor4          =twoDecimal(rsItem.getDouble(19));
                                SalesTaxCode        =rsItem.getString(30);
                                RetailTaxCode       =rsItem.getString(31);
                                TaxCode             =rsItem.getString(32);
                                SuspendedYN         =rsItem.getString(34);
                                AlternateItem       =rsItem.getString(35);
                                Remark              =rsItem.getString(36);
                            }
                            z="success";
                        }
                    }else{
                        z="error";
                    }
                }else{
                    Cursor rsItem=db.getQuery(sql);
                    while (rsItem.moveToNext()) {
                        ItemCode            =rsItem.getString(0);
                        ItemGroup           =rsItem.getString(2);
                        Description         =rsItem.getString(1);
                        UnitPrice           =twoDecimal(rsItem.getDouble(3));
                        UOM                 =rsItem.getString(6);
                        UOM1                =rsItem.getString(7);
                        UOM2                =rsItem.getString(8);
                        UOM3                =rsItem.getString(9);
                        UOM4                =rsItem.getString(10);
                        UOMPrice1           =twoDecimal(rsItem.getDouble(11));
                        UOMPrice2           =twoDecimal(rsItem.getDouble(12));
                        UOMPrice3           =twoDecimal(rsItem.getDouble(13));
                        UOMPrice4           =twoDecimal(rsItem.getDouble(14));
                        UOMFactor1          =twoDecimal(rsItem.getDouble(15));
                        UOMFactor2          =twoDecimal(rsItem.getDouble(16));
                        UOMFactor3          =twoDecimal(rsItem.getDouble(17));
                        UOMFactor4          =twoDecimal(rsItem.getDouble(18));
                        SalesTaxCode        =rsItem.getString(29);
                        RetailTaxCode       =rsItem.getString(30);
                        TaxCode             =rsItem.getString(31);
                        SuspendedYN         =rsItem.getString(33);
                        AlternateItem       =rsItem.getString(34);
                        Remark              =rsItem.getString(35);
                    }
                    z="success";
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
    }

    private Double parseToDb(String values){
        Double newDouble=0.00;
        newDouble=Double.parseDouble(values);
        return newDouble;
    }
}
