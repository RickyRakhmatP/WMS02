package com.skybiz.wms02.ui_StockEnquiry;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;

import com.skybiz.wms02.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class StockEnquiry extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    EditText txtItemCode,txtDescription;
    TextView txtAlternateItem,txtUOM,txtUOM1,
            txtUOM2,txtUOM3,txtUOM4,
            txtUOMPrice1,txtUOMPrice2,txtUOMPrice3,
            txtUOMPrice4,txtUnitPrice,txtUOMFactor1,
            txtUOMFactor2,txtUOMFactor3,txtUOMFactor4,
            txtFactorQty,txtRemark,txtTaxCode,
            txtSalesTaxCode,txtRetailTaxCode,chkSuspendedYN,txtItemGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_enquiry);


        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

       /* txtItemCode=(EditText)findViewById(R.id.txtItemCode);
        txtDescription=(EditText)findViewById(R.id.txtDescription);
        txtAlternateItem=(TextView) findViewById(R.id.txtAlternateItem);
        txtItemGroup=(EditText) findViewById(R.id.txtItemGroup);
        txtUOM=(TextView) findViewById(R.id.txtUOM);
        txtUOM1=(TextView) findViewById(R.id.txtUOM1);
        txtUOM2=(TextView) findViewById(R.id.txtUOM2);
        txtUOM3=(TextView) findViewById(R.id.txtUOM3);
        txtUOM4=(TextView) findViewById(R.id.txtUOM4);
        txtUOMPrice1=(TextView) findViewById(R.id.txtUOMPrice1);
        txtUOMPrice2=(TextView) findViewById(R.id.txtUOMPrice2);
        txtUOMPrice3=(TextView) findViewById(R.id.txtUOMPrice3);
        txtUOMPrice4=(TextView) findViewById(R.id.txtUOMPrice4);
        txtUOMFactor1=(TextView) findViewById(R.id.txtUOMFactor1);
        txtUOMFactor2=(TextView) findViewById(R.id.txtUOMFactor2);
        txtUOMFactor3=(TextView) findViewById(R.id.txtUOMFactor3);
        txtUOMFactor4=(TextView) findViewById(R.id.txtUOMFactor4);
        txtFactorQty=(TextView)findViewById(R.id.txtFactorQty);
        txtUnitPrice=(TextView) findViewById(R.id.txtUnitPrice);
        txtRemark=(TextView) findViewById(R.id.txtRemark);
        txtTaxCode=(TextView) findViewById(R.id.txtTaxCode);
        txtSalesTaxCode=(TextView) findViewById(R.id.txtSalesTaxCode);
        txtRetailTaxCode=(TextView) findViewById(R.id.txtRetailTaxCode);
        chkSuspendedYN=(TextView) findViewById(R.id.chkSuspendedYN);*/

        /*mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_enquiry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_old, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    Fragment_Stock_Enquiry tab1 = new Fragment_Stock_Enquiry();
                    return tab1;
                case 1:
                    Fragment_Stock_Enquiry2 tab2 = new Fragment_Stock_Enquiry2();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Stock Enquiry";
                case 1:
                    return "OTHERS";
            }
            return null;
        }
    }

    public void setItem(String result){
       /* Fragment_Stock_Enquiry fragment=new Fragment_Stock_Enquiry();
        Bundle bundle = new Bundle();
        bundle.putString("JSON_KEY", result);
        fragment.setArguments(bundle);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);*/
        try {
            txtItemCode=(EditText)findViewById(R.id.txtItemCode);
            txtDescription=(EditText)findViewById(R.id.txtDescription);
            txtAlternateItem=(TextView) findViewById(R.id.txtAlternateItem);
            txtItemGroup=(TextView) findViewById(R.id.txtItemGroup);
            txtUOM=(TextView) findViewById(R.id.txtUOM);
            txtUOM1=(TextView) findViewById(R.id.txtUOM1);
            txtUOM2=(TextView) findViewById(R.id.txtUOM2);
            txtUOM3=(TextView) findViewById(R.id.txtUOM3);
            txtUOM4=(TextView) findViewById(R.id.txtUOM4);
            txtUOMPrice1=(TextView) findViewById(R.id.txtUOMPrice1);
            txtUOMPrice2=(TextView) findViewById(R.id.txtUOMPrice2);
            txtUOMPrice3=(TextView) findViewById(R.id.txtUOMPrice3);
            txtUOMPrice4=(TextView) findViewById(R.id.txtUOMPrice4);
            txtUOMFactor1=(TextView) findViewById(R.id.txtUOMFactor1);
            txtUOMFactor2=(TextView) findViewById(R.id.txtUOMFactor2);
            txtUOMFactor3=(TextView) findViewById(R.id.txtUOMFactor3);
            txtUOMFactor4=(TextView) findViewById(R.id.txtUOMFactor4);
            txtFactorQty=(TextView)findViewById(R.id.txtFactorQty);
            txtUnitPrice=(TextView) findViewById(R.id.txtUnitPrice);
            txtRemark=(TextView) findViewById(R.id.txtRemark);
            txtTaxCode=(TextView) findViewById(R.id.txtTaxCode);
            txtSalesTaxCode=(TextView) findViewById(R.id.txtSalesTaxCode);
            txtRetailTaxCode=(TextView) findViewById(R.id.txtRetailTaxCode);
            chkSuspendedYN=(TextView) findViewById(R.id.chkSuspendedYN);
            /*txtItemCode.setText("");
            txtDescription.setText("");
            txtItemGroup.setText("");
            txtRemark.setText("");*/
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                //Log.d("UNITPRICE",jo.getString("UnitPrice"));
                txtItemCode.setText(jo.getString("ItemCode"));
                txtItemGroup.setText(jo.getString("GroupDesc"));
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
