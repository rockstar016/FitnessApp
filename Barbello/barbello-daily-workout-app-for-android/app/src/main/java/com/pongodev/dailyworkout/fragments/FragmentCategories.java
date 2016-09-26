package com.pongodev.dailyworkout.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.activities.ActivityHome;
import com.pongodev.dailyworkout.adapters.AdapterCategories;
import com.pongodev.dailyworkout.listeners.OnTapListener;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * FragmentCategories is created to display workout category and add it to tab.
 * Created using Fragment.
 */
public class FragmentCategories extends Fragment {

    // Create listener object
    private OnSelectedCategoryListener mCallback;

    // Create view objects
    private RecyclerView mList;
    private CircleProgressBar mPrgLoading;
    private AdView mAdView;

    // Create variable to check admob visibility status
    private boolean mIsAdmobVisible;

    // Create adapter object
    private AdapterCategories mAdapterCategories;

    // Create object of database helper class
    private DBHelperWorkouts mDbHelperWorkouts;

    // Create arraylist variable to store data from database helper object
    private ArrayList<ArrayList<Object>> data;

    // Create arraylist variables to store data
    private ArrayList<String> mCategoryIds = new ArrayList<>();
    private ArrayList<String> mCategoryNames = new ArrayList<>();
    private ArrayList<String> mCategoryImages = new ArrayList<>();
    private ArrayList<String> mTotalWorkouts = new ArrayList<>();
    private ArrayList<String> mtotaltime = new ArrayList<>();
    private ActivityHome home_activity = null;

    // Create interface for listener
    public interface OnSelectedCategoryListener {
        public void onSelectedCategory(String selectedID, String selectedName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list, container, false);

        // Connect view objects with view ids in xml
        mPrgLoading     = (CircleProgressBar) v.findViewById(R.id.prgLoading);
        mAdView         = (AdView) v.findViewById(R.id.adView);
        mList           = (RecyclerView) v.findViewById(R.id.list);

        // Configure recyclerview
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.setHasFixedSize(false);

        // Set view for header
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.padding, null);

        // Get admob visibility status
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);
        // Set progress circle loading color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);

        // Call asynctask class to load ad in background
        new SyncShowAd(mAdView).execute();

        // Create object of database helpers class
        mDbHelperWorkouts = new DBHelperWorkouts(getActivity());

        // Create workout database
        try {
            mDbHelperWorkouts.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open workout database
        mDbHelperWorkouts.openDataBase();
        // Call asynctask class to get data from database
        new AsyncGetWorkoutCategories().execute();

        // Set adapter object
        mAdapterCategories = new AdapterCategories(getActivity(), headerView);

        mAdapterCategories.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String id, String name) {
                // When item on recyclerview clicked, send selected data to the activity
                // that implement this fragment
                int pos =  Integer.parseInt(id) +1;
                if(pos != -1)
                {
                    home_activity = (ActivityHome)getActivity(); home_activity.sel_pos = pos;


                    mAdapterCategories._ipos = -1;
                    String ss = mAdapterCategories.time_list.get(pos-1);
                    mDbHelperWorkouts.update_time_data(id, ss);

                    read_database_record();
                }
                mCallback.onSelectedCategory(id, name);
            }
        });
        return v;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnSelectedCategoryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectedCategoryListener");
        }
    }

    // Asynctask class that is used to fetch data from database in background
    private class AsyncGetWorkoutCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            // When data still retrieve from database display loading view
            // and hide other view
            mPrgLoading.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get workout category data from database
            getWorkoutCategoryFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // TODO Auto-generated method stub
            super.onPostExecute(aVoid);
            // When finishing fetching data, close progress dialog and display data
            // to the recyclerview. If data is not available display no result text
            mPrgLoading.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);

            // Send workout categories to adapter
            home_activity = (ActivityHome)getActivity();
            ArrayList<String> data11 = home_activity.str_data;
            mtotaltime.clear(); mtotaltime.addAll(data11);

            mAdapterCategories.updateList(mCategoryIds, mCategoryNames,
                    mCategoryImages, mTotalWorkouts, data11);
            // And set it to recyclerview object if data not empty
            if(mCategoryIds.size() != 0) {
                mList.setAdapter(mAdapterCategories);
            }
        }
    }


    // Method to fetch workout category data from database
    public void getWorkoutCategoryFromDatabase() {
        data = mDbHelperWorkouts.getAllCategories();

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            mCategoryIds.add(row.get(0).toString());
            mCategoryNames.add(row.get(1).toString());
            mCategoryImages.add(row.get(2).toString());
            mTotalWorkouts.add(row.get(3).toString());
        }
       // mDbHelperWorkouts.close();
    }

    // Close database before activity destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbHelperWorkouts.close();
    }
    void read_database_record() {
          // If ActivityWorkouts open via workouts tab then get workout list from workout database
       // mDbHelperWorkouts.update_time_data("7", "24:45");
        ArrayList<ArrayList<Object>> data = mDbHelperWorkouts.getAll_Time();
        String dat = data.get(0).get(0).toString();
    }

}