package com.pongodev.dailyworkout.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.AdapterWorkouts;
import com.pongodev.dailyworkout.listeners.OnTapListener;
import com.pongodev.dailyworkout.utils.DBHelperPrograms;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.Utils;


import java.io.IOException;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * ActivityWorkouts is created to display workouts list.
 * Created using AppCompatActivity.
 */
public class ActivityWorkouts extends AppCompatActivity implements View.OnClickListener {

    private CircleProgressBar mPrgLoading;
    public static RecyclerView sList;
    public static TextView sTxtAlert;
//    public static RelativeLayout sLytSubHeader;

    // Create variables to store data that passed from previous activity
    private String mProgramName;
    private String mSelectedId;
    private String mParentPage;

    // Create adapter object
    private AdapterWorkouts mAdapterWorkouts;

    // Create object of database helper class
    private DBHelperWorkouts mDbHelperWorkouts;
    private DBHelperPrograms mDbHelperPrograms;

    // Create arraylist variables to store data
    private ArrayList<String> mProgramIds     = new ArrayList<>();
    private ArrayList<String> mWorkoutIds     = new ArrayList<>();
    private ArrayList<String> mWorkoutNames   = new ArrayList<>();
    private ArrayList<String> mWorkoutImages  = new ArrayList<>();
    private ArrayList<String> mWorkoutTimes   = new ArrayList<>();
    private ArrayList<String> mWorkoutSteps   = new ArrayList<>();

    private ArrayList<String> _time_list   = new ArrayList<>();
    public ArrayList<String> work_time_list  = new ArrayList<String>();
    public int mSel_pos = -1;

    // Create variable to check admob visibility status
    private boolean mIsAdmobVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        // Get data that passed from previous activity
        Intent i        = getIntent();
        mSelectedId     = i.getStringExtra(Utils.ARG_WORKOUT_ID);
        mProgramName    = i.getStringExtra(Utils.ARG_WORKOUT_NAME);
        mParentPage     = i.getStringExtra(Utils.ARG_PARENT_PAGE);

        // connect view objects with view ids in xml
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPrgLoading     = (CircleProgressBar) findViewById(R.id.prgLoading);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        sList           = (RecyclerView) findViewById(R.id.list);
        sTxtAlert       = (TextView) findViewById(R.id.txtAlert);
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);
        Bundle bundle = getIntent().getExtras();
        work_time_list = (ArrayList<String>)bundle.get("array_list");
        {
            _time_list.clear();_time_list.addAll(work_time_list);
        }
        mSel_pos = getIntent().getIntExtra("Select_Pos", -1);

        // Set toolbar name with workout category name and set toolbar as actionbar
        mToolbar.setTitle(mProgramName);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configure recyclerview object
        sList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        sList.setLayoutManager(mLayoutManager);

        // Set progress circle loading color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);

        // Create object of database helpers.
        mDbHelperWorkouts = new DBHelperWorkouts(this);
        mDbHelperPrograms = new DBHelperPrograms(this);

        // Create workout and program database
        try {
            mDbHelperWorkouts.createDataBase();
            mDbHelperPrograms.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open workout and program database
        mDbHelperWorkouts.openDataBase();
        mDbHelperPrograms.openDataBase();

        // Set adapter object
        mAdapterWorkouts = new AdapterWorkouts(this, mSelectedId, mParentPage, mDbHelperPrograms);

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
//        switch (v.getId()){
//            case R.id.raisedStart:
//                // When raisedStart button click, open ActivityStopWatch and pass
//                // all workout data in selected day program to that activity
//                Intent detailIntent = new Intent(this, ActivityStopWatch.class);
//                mWorkoutIds = mAdapterWorkouts.getData(0);
//                mWorkoutNames = mAdapterWorkouts.getData(1);
//                mWorkoutImages = mAdapterWorkouts.getData(2);
//                mWorkoutTimes = mAdapterWorkouts.getData(3);
//
//                init_other();
//                detailIntent.putExtra(Utils.ARG_WORKOUT_IDS, mWorkoutIds);
//                detailIntent.putExtra(Utils.ARG_WORKOUT_NAMES, mWorkoutNames);
//                detailIntent.putExtra(Utils.ARG_WORKOUT_IMAGES, mWorkoutImages);
//                detailIntent.putExtra(Utils.ARG_WORKOUT_TIMES, mWorkoutTimes);//mWorkoutTimes);
//                detailIntent.putExtra(Utils.ARG_WORKOUT_NAME, mProgramName);
//
//                detailIntent.putExtra("sel_pos", mSel_pos);
//                detailIntent.putStringArrayListExtra("select_list", work_time_list);
//
//                startActivity(detailIntent);
//                overridePendingTransition(R.anim.open_next, R.anim.close_main);
//                break;
//        }
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
            if(mWorkoutIds.isEmpty()){
                sTxtAlert.setVisibility(View.VISIBLE);
            } else {
                sTxtAlert.setVisibility(View.GONE);
                sList.setVisibility(View.VISIBLE);
                mAdapterWorkouts.updateList(mProgramIds, mWorkoutIds, mWorkoutNames,
                        mWorkoutImages, _time_list, mWorkoutSteps);
            }
            sList.setAdapter(mAdapterWorkouts);
        }
    }

    // Method to fetch workout list from database
    private void getWorkoutListDataFromDatabase() {
        ArrayList<ArrayList<Object>> data;
            data = mDbHelperWorkouts.getAllWorkoutsByCategory(mSelectedId);
            for (int i = 0; i < data.size(); i++) {
                ArrayList<Object> row = data.get(i);
                mWorkoutIds.add(row.get(0).toString());
                mWorkoutNames.add(row.get(1).toString());
                mWorkoutImages.add(row.get(2).toString());
                mWorkoutTimes.add(row.get(3).toString());
                mWorkoutSteps.add(row.get(4).toString());
            }
    }

    // Method to handle physical back button with transition
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

//    public void init_other()
//    {
//        for(int i = 0; i < mWorkoutTimes.size(); i++) {
//            if(i < mWorkoutIds.size()) {
//                int id = Integer.parseInt(mWorkoutIds.get(i));
//                mWorkoutTimes.set(id, _time_list.get(id));
//            }
//        }
//    }
}


