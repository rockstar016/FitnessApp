package com.pongodev.dailyworkout.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.OrangeGangsters.circularbarpager.library.CircularBarPager;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.AdapterPagerWorkout;
import com.pongodev.dailyworkout.utils.DBHelperPrograms;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.Utils;
import com.pongodev.dailyworkout.views.DaySelectDialog;
import com.pongodev.dailyworkout.views.ViewSteps;
import com.pongodev.dailyworkout.views.ViewWorkout;
import com.viewpagerindicator.CirclePageIndicator;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * ActivityDetail is created to display workout detail.
 * Created using AppCompatActivity.
 */
public class ActivityDetail extends AppCompatActivity implements
        View.OnClickListener {

    // Create view objects
    private Toolbar mToolbar;
    private FloatingActionButton mFabAdd;
    private CircleProgressBar mPrgLoading;
    private CircularBarPager mCircularBarPager;
    private TextView mTxtTitle, mTxtSubTitle;
    private LinearLayout lytTitleLayout;

    // Create variable to check admob visibility status
    private boolean mIsAdmobVisible;

    // Create database helper class object
    private DBHelperPrograms mDbHelperPrograms;
    private DBHelperWorkouts mDbHelperWorkouts;

    // Create arraylist variables to store data
    private String mWorkoutId;
    private String mWorkoutName;
    private String mWorkoutImage;
    private String mWorkoutTime;
    private String mWorkoutSteps;
    private ArrayList<String> mWorkoutGalleries    = new ArrayList<>();

    private ArrayList<String> _mtime = new ArrayList<>();

    // Create array variable to store days
    private String[] mDays;
    int _msel_pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        _msel_pos = getIntent().getIntExtra("sel_pos", -1);
        // Get days from strings.xml
        mDays  = getResources().getStringArray(R.array.day_names);

        Bundle bundle = getIntent().getExtras();
        _mtime = (ArrayList<String>)bundle.get("select_list");

        // Get data from previous activity
        // Get data that passed from previous activity
        Intent iGet         = getIntent();
        mWorkoutId          = iGet.getStringExtra(Utils.ARG_WORKOUT_ID);

        // Connect view objects with view ids in xml
        mToolbar            = (Toolbar) findViewById(R.id.toolbar);
        mFabAdd             = (FloatingActionButton) findViewById(R.id.fabAdd);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mPrgLoading         = (CircleProgressBar) findViewById(R.id.prgLoading);
        mCircularBarPager   = (CircularBarPager) findViewById(R.id.circularBarPager);
        mTxtTitle           = (TextView) findViewById(R.id.txtTitle);
        mTxtSubTitle        = (TextView) findViewById(R.id.txtSubTitle);
        lytTitleLayout      = (LinearLayout) findViewById(R.id.lytTitleLayout);

        // Set click listener to fab button
        mFabAdd.setOnClickListener(this);

        // Hide circle page indicator as we do not need it
        CirclePageIndicator mCirclePageIndicator = mCircularBarPager.getCirclePageIndicator();
        mCirclePageIndicator.setFillColor(ContextCompat.getColor(this, R.color.accent_color));
        mCirclePageIndicator.setStrokeColor(ContextCompat.getColor(this, R.color.divider_color));

        // Get admob visibility status
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);

        // Set progress circle loading color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);

        // Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set toolbar background to transparent at the beginning
        mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,
                getResources().getColor(R.color.primary_color)));

        // Call asynctask class to get data from database
        new AsyncGetWorkoutDetail().execute();
        // Call asynctask class to load admob
        new SyncShowAd(mAdView).execute();

        // Create object of database helpers class
        mDbHelperPrograms = new DBHelperPrograms(getApplicationContext());
        mDbHelperWorkouts = new DBHelperWorkouts(getApplicationContext());

        // Create program and workout databases
        try {
            mDbHelperPrograms.createDataBase();
            mDbHelperWorkouts.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open program and workout databases
        mDbHelperPrograms.openDataBase();
        mDbHelperWorkouts.openDataBase();
    }

    // Asynctask class to load admob in background
    public class SyncShowAd extends AsyncTask<Void, Void, Void>{

        AdView ad;
        AdRequest adRequest, interstitialAdRequest;
        InterstitialAd interstitialAd;
        int interstitialTrigger;

        public SyncShowAd(AdView ad){
            this.ad = ad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Check ad visibility. If visible, create adRequest
            if(mIsAdmobVisible) {
                // Create an ad request
                if (Utils.IS_ADMOB_IN_DEBUG) {
                    adRequest = new AdRequest.Builder().
                            addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }

                // When interstitialTrigger equals ARG_TRIGGER_VALUE, display interstitial ad
                interstitialAd = new InterstitialAd(ActivityDetail.this);
                interstitialAd.setAdUnitId(ActivityDetail.this.getResources()
                        .getString(R.string.interstitial_ad_unit_id));
                interstitialTrigger = Utils.loadPreferences(Utils.ARG_TRIGGER,
                        ActivityDetail.this);
                if(interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    if(Utils.IS_ADMOB_IN_DEBUG) {
                        interstitialAdRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();
                    }else {
                        interstitialAdRequest = new AdRequest.Builder().build();
                    }
                    Utils.savePreferences(Utils.ARG_TRIGGER, 0, ActivityDetail.this);
                }else{
                    Utils.savePreferences(Utils.ARG_TRIGGER, (interstitialTrigger+1),
                            ActivityDetail.this);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Check ad visibility. If visible, display ad banner and interstitial

            if(mIsAdmobVisible) {
                // Start loading the ad
                ad.loadAd(adRequest);

                if (interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    // Start loading the ad
                    interstitialAd.loadAd(interstitialAdRequest);
                    // Set the AdListener
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                        }

                        @Override
                        public void onAdClosed() {
                        }

                    });
                }
            }
        }
    }

    // Asynctask class to load data from database in background
    private class AsyncGetWorkoutDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // When data still retrieve from database display loading view
            // and hide other view
            super.onPreExecute();
            mPrgLoading.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.GONE);
            mCircularBarPager.setVisibility(View.GONE);
            mFabAdd.setVisibility(View.GONE);
            lytTitleLayout.setVisibility(View.GONE);

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
            getWorkoutDetailFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Call asynctask class to get workout galleries in background
            new AsyncGetWorkoutGalleryImages().execute();


        }
    }

    // Asynctask class to load data from database in background
    private class AsyncGetWorkoutGalleryImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get workout galleries from database
            getWorkoutGalleryImagesFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // When finishing fetching data, close progress dialog and display data to the views
            super.onPostExecute(aVoid);
            mTxtTitle.setText(mWorkoutName);
            mTxtSubTitle.setText(mWorkoutTime);
            // Add gallery images and steps to view pager
            startViewPagerThread();
        }
    }

    // Method to add gallery images and steps to view pager in UI thread
    private void startViewPagerThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View[] viewFlippers = new View[2];
                // View for gallery images
                viewFlippers[0] = new ViewWorkout(ActivityDetail.this,
                        mWorkoutGalleries);
                // View for steps
                viewFlippers[1] = new ViewSteps(ActivityDetail.this,
                        mWorkoutSteps);
                mCircularBarPager.setViewPagerAdapter(new AdapterPagerWorkout(
                        viewFlippers));

                mPrgLoading.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                mCircularBarPager.setVisibility(View.VISIBLE);
                mFabAdd.setVisibility(View.VISIBLE);
                lytTitleLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    // Method to fetch workout detail from database
    public void getWorkoutDetailFromDatabase() {

        ArrayList<Object> data;
        data = mDbHelperWorkouts.getWorkoutDetail(mWorkoutId);

        mWorkoutId     = data.get(0).toString();
        mWorkoutName   = data.get(1).toString();
        mWorkoutImage  = data.get(2).toString();
        mWorkoutTime = _mtime.get(Integer.parseInt(mWorkoutId)); ///mWorkoutTime   = data.get(3).toString();
        mWorkoutSteps  = data.get(4).toString();
    }

    // Method to get workout gallery images from database
    private void getWorkoutGalleryImagesFromDatabase(){
        ArrayList<ArrayList<Object>> data;
        data = mDbHelperWorkouts.getImages(mWorkoutId);

        if(data.size() > 0) {
            // If gallery is available then store data to variables
            for (int i = 0; i < data.size(); i++) {
                ArrayList<Object> row = data.get(i);
                mWorkoutGalleries.add(row.get(0).toString());
            }
        }else {
            mWorkoutGalleries.add(mWorkoutImage);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAdd:
                // When add fab click show days dialog
                showDayListDialog();
            default:
                break;
        }
    }

    // Method to display day list dialog
    public void showDayListDialog() {
        final DaySelectDialog m_dialog = new DaySelectDialog(this);
        View.OnClickListener ok_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_dialog.getCurrentGroupIndex() != -1) {
                    if (mDbHelperPrograms.isDataAvailable((m_dialog.getCurrentGroupIndex() + 1), mWorkoutId,m_dialog.getCurrentItemIndex())) {
                        // If workout has already added to selected day program
                        // inform user with snackbar
                        showSnackbar(getString(R.string.workout_already_added) +
                                " " + mDays[m_dialog.getCurrentGroupIndex()] + " " + getString(R.string.program) + ".");
                    } else {
                        // If it has not added yet add it to programs database
                        mDbHelperPrograms.addData(
                                Integer.valueOf(mWorkoutId),
                                mWorkoutName,
                                (m_dialog.getCurrentGroupIndex() + 1),
                                (m_dialog.getCurrentItemIndex()),
                                mWorkoutImage,
                                mWorkoutTime,
                                mWorkoutSteps);
                        showSnackbar(
                                getString(R.string.workout_successfully_added)
                                        + " " + mDays[m_dialog.getCurrentGroupIndex()] + " " +
                                        getString(R.string.program) + ".");
                    }
                }
                m_dialog.dismiss();
            }
        };

        View.OnClickListener cancel_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                m_dialog.dismiss();
            }
        };
        m_dialog.setButtonClickListener(ok_listener,cancel_listener);

        m_dialog.show();
    }


    // Method to show snackbar message
    public void showSnackbar(String message){
        new SnackBar.Builder(this)
                .withMessage(message)
                .show();
    }

    // Method to handle physical back button with animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
