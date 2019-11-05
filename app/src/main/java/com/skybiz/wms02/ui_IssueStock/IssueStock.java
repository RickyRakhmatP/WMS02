package com.skybiz.wms02.ui_IssueStock;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.skybiz.wms02.R;
import com.skybiz.wms02.m_Detail.DownloaderOrder;
import com.skybiz.wms02.m_Item.DownloaderItem;
import com.skybiz.wms02.m_ItemList.DialogItem;
import com.skybiz.wms02.m_Supplier.DialogSupplier;

public class IssueStock extends AppCompatActivity {

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

    RecyclerView rvItem,rvDetail;
    private GridLayoutManager lLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_issue_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Issue Stock");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

       /* mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        getMenuInflater().inflate(R.menu.menu_issue_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_old, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnSupplier) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","IS");
            b.putString("DBSTATUS_KEY","0");
            DialogSupplier dialogCustomer = new DialogSupplier();
            dialogCustomer.setArguments(b);
            dialogCustomer.show(getSupportFragmentManager(), "List Supplier");
            return true;
        }else if (id == R.id.mnItem) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","IS");
            DialogItem dialogItem = new DialogItem();
            dialogItem.setArguments(b);
            dialogItem.show(getSupportFragmentManager(), "mListItem");
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
                    Fragment_Detail tab1 = new Fragment_Detail();
                    return tab1;
                case 1:
                    Fragment_Summary tab2 = new Fragment_Summary();
                    return tab2;
                case 2:
                    Fragment_Item tab3 = new Fragment_Item();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DETAILS";
                case 1:
                    return "SUMMARY";
                case 2:
                    return "ITEMS";
            }
            return null;
        }
    }

    public void setCustomerBar(String CusName){
        getSupportActionBar().setTitle(CusName);
    }
    public void retDetail(){
        rvDetail=(RecyclerView)findViewById(R.id.rvDetail);
        rvDetail.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvDetail.setLayoutManager(lLayout);
        rvDetail.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder dOrder=new DownloaderOrder(this,"IS", rvDetail,this.getSupportFragmentManager());
        dOrder.execute();
    }
    public void retItem(String ItemGroup){
        rvItem=(RecyclerView)findViewById(R.id.rvItem);
        rvItem.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 2);
        rvItem.setLayoutManager(lLayout);
        rvItem.setItemAnimator(new DefaultItemAnimator());
        if(ItemGroup.equals("Miscellaneous")) {
            // DownloaderMisc dItem = new DownloaderMisc(getActivity(), "IS", rvItem);
            // dItem.execute();
        }else{
            DownloaderItem dItem = new DownloaderItem(this, "IS", ItemGroup, rvItem);
            dItem.execute();
        }
    }

    public void newRefresh(){
        finish();
        Intent mainIntent = new Intent(this, IssueStock.class);
        startActivity(mainIntent);
    }

}
