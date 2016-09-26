package com.pongodev.dailyworkout.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.AdapterWorkouts;
import com.pongodev.dailyworkout.adapters.AdapterWorkouts1;
import com.pongodev.dailyworkout.listeners.OnTapListener;
import com.pongodev.dailyworkout.models.ProgramsHeaderModel;
import com.pongodev.dailyworkout.models.ProgramsModel;
import com.pongodev.dailyworkout.utils.DBHelperPrograms;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Design and developed by pongodev.com
 *
 * ActivityWorkouts is created to display workouts list.
 * Created using AppCompatActivity.
 */
public class ActivityWorkouts1 extends AppCompatActivity implements View.OnClickListener {

    private CircleProgressBar mPrgLoading;
//    public static RecyclerView sList;
    public static ExpandableListView sList;
    public static TextView sTxtAlert;
    public static RelativeLayout sLytSubHeader;

    // Create variables to store data that passed from previous activity
    private String mProgramName;
    private String mSelectedId;
    private String mParentPage;

    // Create adapter object
    private AdapterWorkouts1 mAdapterWorkouts;

    // Create object of database helper class

    private DBHelperPrograms mDbHelperPrograms;
    private List<ProgramsHeaderModel> headerList;
    private HashMap<ProgramsHeaderModel, List<ProgramsModel>> itemList;

    // Create arraylist variables to store data
    private ArrayList<String> mProgramIds     = new ArrayList<>();
    private ArrayList<String> mWorkoutIds     = new ArrayList<>();
    private ArrayList<String> mWorkoutNames   = new ArrayList<>();
    private ArrayList<String> mWorkoutImages  = new ArrayList<>();
    private ArrayList<String> mWorkoutTimes   = new ArrayList<>();
    public ArrayList<String> work_time_list  = new ArrayList<String>();
    public int mSel_pos = -1;
    // Create variable to check admob visibility status
    private boolean mIsAdmobVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts1);
        // Get data that passed from previous activity
        Intent i        = getIntent();
        mSelectedId     = i.getStringExtra(Utils.ARG_WORKOUT_ID);
        mProgramName    = i.getStringExtra(Utils.ARG_WORKOUT_NAME);
        mParentPage     = i.getStringExtra(Utils.ARG_PARENT_PAGE);
        // connect view objects with view ids in xml
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPrgLoading     = (CircleProgressBar) findViewById(R.id.prgLoading);
        AppCompatButton mRaisedStart = (AppCompatButton) findViewById(R.id.raisedStart);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        sList           = (ExpandableListView) findViewById(R.id.expandable_list_workout);
        sTxtAlert       = (TextView) findViewById(R.id.txtAlert);
        sLytSubHeader       = (RelativeLayout) findViewById(R.id.lytSubHeaderLayout);

        // Set listener to the views
        mRaisedStart.setOnClickListener(this);

        // Get admob visibility status
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);
        mSel_pos = getIntent().getIntExtra("Select_Pos", -1);
        Bundle bundle = getIntent().getExtras();
        work_time_list = (ArrayList<String>)bundle.get("array_list");
        // Set toolbar name with workout category name and set toolbar as actionbar
        mToolbar.setTitle(mProgramName);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set progress circle loading color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);
        mDbHelperPrograms = new DBHelperPrograms(this);
        // Create workout and program database
        try {
            mDbHelperPrograms.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open workout and program database
        mDbHelperPrograms.openDataBase();
        // Set adapter object
        mAdapterWorkouts = new AdapterWorkouts1(this, mSelectedId, mParentPage, mDbHelperPrograms);
        // Call asynctask class to load ad in background
        new SyncShowAd(mAdView).execute();
        // Call asynctask class to load workouts data in background
        new AsyncGetWorkoutList().execute();
        mAdapterWorkouts.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String id, String Name) {
                // On workout data selected open ActivityDetail and pass selected
                // data to that activity
                Intent i = new Intent(getApplicationContext(), ActivityDetail.class);
                i.putExtra(Utils.ARG_WORKOUT_ID, id);
                i.putExtra(Utils.ARG_PARENT_PAGE, mParentPage);
                i.putExtra("sel_pos", mSel_pos);
                i.putStringArrayListExtra("select_list", work_time_list);
                startActivity(i);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
            }
        });

    }
    private void refreshGroupExpand(){
        for(int i = 0 ; i < mAdapterWorkouts.getGroupCount(); i++){
            sList.expandGroup(i);
        }
    }
    // Asynctask class to load admob in background
    public class SyncShowAd extends AsyncTask<Void, Void, Void>{

        AdView ad;
        AdRequest adRequest;

        public SyncShowAd(AdView ad){
            this.ad = ad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check ad visibility. If visible, create adRequest
            if(mIsAdmobVisible) {
                // Create an ad request
                if (Utils.IS_ADMOB_IN_DEBUG) {
                    adRequest = new AdRequest.Builder().
                            addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Check ad visibility. If visible, display ad banner
            if(mIsAdmobVisible) {
                // Start loading the ad
                ad.loadAd(adRequest);

                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (ad != null) {
                            ad.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.raisedStart:
                // When raisedStart button click, open ActivityStopWatch and pass
                // all workout data in selected day program to that activity
                if(mAdapterWorkouts.getGroupCount() != 0) {
                    Intent detailIntent = new Intent(this, ActivityStopWatch.class);
                    mWorkoutIds = mAdapterWorkouts.getData(0);
                    mWorkoutNames = mAdapterWorkouts.getData(1);
                    mWorkoutImages = mAdapterWorkouts.getData(2);
                    mWorkoutTimes = mAdapterWorkouts.getData(3);
                    detailIntent.putExtra(Utils.ARG_WORKOUT_IDS, mWorkoutIds);
                    detailIntent.putExtra(Utils.ARG_WORKOUT_NAMES, mWorkoutNames);
                    detailIntent.putExtra(Utils.ARG_WORKOUT_IMAGES, mWorkoutImages);
                    detailIntent.putExtra(Utils.ARG_WORKOUT_TIMES, mWorkoutTimes);//mWorkoutTimes);
                    detailIntent.putExtra(Utils.ARG_WORKOUT_NAME, mProgramName);
                    detailIntent.putExtra("sel_pos", mSel_pos);
                    detailIntent.putStringArrayListExtra("select_list", work_time_list);
                    startActivity(detailIntent);
                    overridePendingTransition(R.anim.open_next, R.anim.close_main);
                }
                break;
        }
    }

    // Asynctask class that is used to fetch data from database in background
    private class AsyncGetWorkoutList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // When data still retrieve from database display loading view
            // and hide other view
            super.onPreExecute();
            mPrgLoading.setVisibility(View.VISIBLE);
            sList.setVisibility(View.GONE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get workout data from database
            getWorkoutListDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // TODO Auto-generated method stub
            super.onPostExecute(aVoid);

            // When finishing fetching data, close progress dialog and display data
            // to the recyclerview. If data is not available display no result text
            mPrgLoading.setVisibility(View.GONE);
            if(itemList.isEmpty()){
                sTxtAlert.setVisibility(View.VISIBLE);
            } else {
                sTxtAlert.setVisibility(View.GONE);
                sList.setVisibility(View.VISIBLE);
                mAdapterWorkouts.setProgramHeaderList(headerList);
                mAdapterWorkouts.setProgramItemList(itemList);
            }
            sList.setAdapter(mAdapterWorkouts);
            refreshGroupExpand();
        }
    }

    // Method to fetch workout list from database
    private void getWorkoutListDataFromDatabase() {
        ArrayList<ArrayList<Object>> data;
        headerList = new ArrayList();
        headerList = mDbHelperPrograms.getHeaderModel(mSelectedId);
        itemList = new HashMap<>();
        for (int i = 0; i < headerList.size(); i++) {
            ArrayList<ProgramsModel> row = mDbHelperPrograms.getItemModel(mSelectedId, headerList.get(i).getCircuitNumber());
            itemList.put(headerList.get(i),row);
        }
        return;
    }

    // Method to handle physical back button with transition
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

}


