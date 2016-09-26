package com.pongodev.dailyworkout.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.OrangeGangsters.circularbarpager.library.CircularBarPager;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.AdapterPagerWorkout;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.Utils;
import com.pongodev.dailyworkout.views.ViewWorkout;
import com.viewpagerindicator.CirclePageIndicator;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Design and developed by pongodev.com
 *
 * ActivityStopWatch is created to display stopwatch of workout program.
 * Created using AppCompatActivity.
 */
public class ActivityStopWatch extends AppCompatActivity implements
    View.OnClickListener {

    // Interval time
    private static final int INTERVAL = 1000;
    public int _cnt = 10;

    // Create view objects
    private Toolbar mToolbar;
    private FloatingActionButton mFabPlay;
    private AdView mAdView;
    private CircleProgressBar mPrgLoading;
    private CircularBarPager mCircularBarPager;
    private LinearLayout mLytContainerLayout;
    private TextView mTxtTitle, mTxtSubTitle, mTxtBreakTime;
    private CirclePageIndicator mCirclePageIndicator;
    private FrameLayout mLytBreakLayout;

    // Create variable to check admob visibility status
    private boolean mIsAdmobVisible;

    // Create audio, TTS, and counter class objects
    private AudioManager mAudioManager;
    private TextToSpeech mTextToSpeech;
    private Counter mCounter;
    private MediaPlayer mMediaPlayer;

    // Create database helper class object
    private DBHelperWorkouts mDbHelperWorkouts;

    // Create arraylist variables to store data
    private ArrayList<String> mWorkoutIds                       = new ArrayList<>();
    private ArrayList<String> mWorkoutNames                     = new ArrayList<>();
    private ArrayList<String> mWorkoutImages                    = new ArrayList<>();
    private ArrayList<String> mWorkoutTimes                     = new ArrayList<>();

    private Map<String, ArrayList<String>> mWorkoutGalleries    = new HashMap<String,
            ArrayList<String>>();

    // Create variable to store program name
    private String mProgramName;

    // Create variable to handle status
    private boolean mIsPlay = false;
    private boolean mIsPause = false;
    private boolean mIsBreak  = true;
    private boolean mIsFirstAppRun = true;

    ZoomControls simpleZoomControls;
    String _str_break = Utils.ARG_DEFAULT_BREAK;

    // Create variables to store current data
    private String mCurrentTime ="00:00";
    private static int mCurrentWorkout = 0;
    private static    int mCurrentData = 0;

    private int mStart = 0;
    private int mEnd = 0;
    private int mStep = 0;
    private int _msel_pos = -1;

    private ArrayList<String> _mtime = new ArrayList<>();


    private int start_circuit_number;
    private int current_rounds = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        simpleZoomControls = (ZoomControls) findViewById(R.id.zoomControls);
        zoom_control(); Read_data();
        // Keep activity screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        _msel_pos = getIntent().getIntExtra("sel_pos", -1);
       // Bundle bundle = getIntent().getExtras();
        //_mtime = (ArrayList<String>)bundle.get("select_list");

        // Get data from previous activity
        Intent iGet     = getIntent();
        mWorkoutIds     = iGet.getStringArrayListExtra(Utils.ARG_WORKOUT_IDS);
        mWorkoutNames   = iGet.getStringArrayListExtra(Utils.ARG_WORKOUT_NAMES);
        mWorkoutImages   = iGet.getStringArrayListExtra(Utils.ARG_WORKOUT_IMAGES);
        mWorkoutTimes   = iGet.getStringArrayListExtra(Utils.ARG_WORKOUT_TIMES);
        mProgramName    = iGet.getStringExtra(Utils.ARG_WORKOUT_NAME);
//        mCircuitNumbers = iGet.getStringArrayListExtra(Utils.ARG_CIRCUITNUMBERS);
//        mRoundNumbers = iGet.getStringArrayListExtra(Utils.ARG_ROUNDNUMBERS);

        // Connect view objects with view ids in xml
        mToolbar            = (Toolbar) findViewById(R.id.toolbar);
        mFabPlay            = (FloatingActionButton) findViewById(R.id.fabPlay);
        mAdView             = (AdView) findViewById(R.id.adView);
        mPrgLoading         = (CircleProgressBar) findViewById(R.id.prgLoading);
        mCircularBarPager   = (CircularBarPager) findViewById(R.id.circularBarPager);
        mLytContainerLayout = (LinearLayout) findViewById(R.id.lytContainerLayout);
        mTxtTitle           = (TextView) findViewById(R.id.txtTitle);
        mTxtSubTitle        = (TextView) findViewById(R.id.txtSubTitle);
        mTxtBreakTime       = (TextView) findViewById(R.id.txtBreakTime);
        mLytBreakLayout     = (FrameLayout) findViewById(R.id.lytBreakLayout);


        // Initialize views
        mTxtTitle.setText(getString(R.string.get_started));
        mTxtSubTitle.setText(getString(R.string.initial_time));
        mTxtBreakTime.setText(Utils.ARG_DEFAULT_START);

        // Set click listener to fab button
        mFabPlay.setOnClickListener(this);

        // Hide circle page indicator as we do not need it
        mCirclePageIndicator = mCircularBarPager.getCirclePageIndicator();
        mCirclePageIndicator.setVisibility(View.GONE);
        mCirclePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // If this is the first time screen display initialize views
                if (mIsFirstAppRun) {
                    mTxtTitle.setText(getString(R.string.get_started));
                    mTxtSubTitle.setText(getString(R.string.initial_time));
                } else {
                    // If pager page change set data base on its position
                    mTxtTitle.setText("(" + (position + 1) + "/" +
                            mWorkoutIds.size() + ") " +
                            mWorkoutNames.get(position));
                    mTxtSubTitle.setText(mWorkoutTimes.get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set audio manager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Utils.ARG_SOUND_VOLUME, 0);

        mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                R.raw.alert_beep);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        switch (mAudioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                mMediaPlayer.setVolume(0, 0);
                break;
        }
        // Get admob visibility status
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);

        // Set progress circle loading color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);

        // Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set toolbar background to transparent at the beginning
        mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,
                ContextCompat.getColor(this, R.color.primary_color)));

        // Add mCurrentData with 1 as the first data will be display
        mCurrentData += 1;

        // Call asynctask class to load gallery images from database
        new AsyncGetWorkoutGalleryImages().execute();
        // Call asynctask class to load admob
        new SyncShowAd(mAdView).execute();

        // Set text to speech object
        mTextToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    // Set text to speech language
                    int languageAvailable = mTextToSpeech.isLanguageAvailable(Utils.ARG_LOCALE);
                    // Check whether locale language is supported, if not supported set US
                    // as default locale language for TTS
                    if(languageAvailable == TextToSpeech.LANG_NOT_SUPPORTED){
                        mTextToSpeech.setLanguage(Locale.US);
                    }else {
                        mTextToSpeech.setLanguage(Utils.ARG_LOCALE);
                    }
                }
            }

        });

        // Set database helper class object
        mDbHelperWorkouts = new DBHelperWorkouts(getApplicationContext());

        // Create database
        try {
            mDbHelperWorkouts.createDataBase();
        }catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        // Open database
        mDbHelperWorkouts.openDataBase();

        int ss = _cnt / 60; int ss1 = _cnt % 60;
        _str_break = get_string(ss, ss1);
        mTxtBreakTime.setText(_str_break);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            //Intent i = new Intent(this, ActivityWorkouts1.class);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                interstitialAd = new InterstitialAd(ActivityStopWatch.this);
                interstitialAd.setAdUnitId(ActivityStopWatch.this.getResources()
                        .getString(R.string.interstitial_ad_unit_id));
                interstitialTrigger = Utils.loadPreferences(Utils.ARG_TRIGGER,
                        ActivityStopWatch.this);
                if(interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    if(Utils.IS_ADMOB_IN_DEBUG) {
                        interstitialAdRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();
                    }else {
                        interstitialAdRequest = new AdRequest.Builder().build();
                    }
                    Utils.savePreferences(Utils.ARG_TRIGGER, 0, ActivityStopWatch.this);
                }else{
                    Utils.savePreferences(Utils.ARG_TRIGGER, (interstitialTrigger+1),
                            ActivityStopWatch.this);
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

    // Asynctask class to fetch data from database in background
    private class AsyncGetWorkoutGalleryImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // When data still retrieve from database display loading view
            // and hide other view
            mLytContainerLayout.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
            mPrgLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get workout image from database
            getWorkoutGalleryImagesFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // After fetching gallery images, add them to view pager in background process
            startViewPagerThread();

        }
    }

    // Method to add gallery images to view pager in UI thread
    private void startViewPagerThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                View[] viewFlippers = new View[mWorkoutIds.size()];
                while (i < mWorkoutIds.size()) {
                    viewFlippers[i] = new ViewWorkout(ActivityStopWatch.this,
                            mWorkoutGalleries.get(mWorkoutIds.get(i)));
                    i++;
                }
                mCircularBarPager.setViewPagerAdapter(new AdapterPagerWorkout(
                        viewFlippers));
                mLytContainerLayout.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.VISIBLE);
                mPrgLoading.setVisibility(View.GONE);
            }
        });

    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    // Method to get workout gallery images from database
    private void getWorkoutGalleryImagesFromDatabase(){
        ArrayList<ArrayList<Object>> data;

        for(int i = 0; i < mWorkoutIds.size(); i++) {
            data = mDbHelperWorkouts.getImages(mWorkoutIds.get(i));

            ArrayList<String> gallery = new ArrayList<String>();

            // If image gallery is not available for selected workout id,
            // add workout image to the gallery
            if(data.size() > 0) {
                // store data to arraylist variable
                for (int j = 0; j < data.size(); j++) {
                    ArrayList<Object> row = data.get(j);
                    gallery.add(row.get(0).toString());
                }
            }else{
                gallery.add(mWorkoutImages.get(i));
            }
            mWorkoutGalleries.put(mWorkoutIds.get(i), gallery);
        }
    }

    // Method to set count down timer
    private void startTimer(String time){

        String[] splitTime = time.split(":");

        int splitMinute = Integer.valueOf(splitTime[0]);
        int splitSecond = Integer.valueOf(splitTime[1]);

        Long mMilisSecond = (long) (((splitMinute * 60) + splitSecond) * 1000);

        int max = (((splitMinute * 60) + splitSecond) * 1000);
        mCircularBarPager.getCircularBar().setMax(max);
        mStep = (int) ((max * INTERVAL) / mMilisSecond);
        mCounter = new Counter(mMilisSecond, INTERVAL);

        mStart = mEnd;
        mEnd = mEnd + mStep;
        mCounter.start();
    }

    // Class Counter to create count down timer, created using CountDownTimer class
    public class Counter extends CountDownTimer {

        private long mAlert=4000;
        private int paramAlert=1;
        private String mTimer;
        boolean isRunning = false;

        public Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            // Convert time format from millisInFuture to "00:00" format
            mTimer = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisInFuture),
                    TimeUnit.MILLISECONDS.toSeconds(millisInFuture) -
                            TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisInFuture)));

            // If this is the first time screen display set break time view as start time view
            if(mIsFirstAppRun){
                mTxtBreakTime.setText(mTimer);
            }else {
                if (mIsBreak) {
                    // If this is break, set mTimer to break time view and set sub title view
                    // with initial time
                    mTxtBreakTime.setText(mTimer);
                    mTxtSubTitle.setText(getResources().getString(R.string.initial_time));
                } else {
                    mTxtSubTitle.setText(mTimer);
                }
            }
        }

        @Override
        public void onFinish() {
            mStart = 0;
            mEnd = 0;
            isRunning = false;

            // When count down finished, set both break time and sub title view with
            // initial time
            mTxtBreakTime.setText(getResources().getString(R.string.initial_time));
            mTxtSubTitle.setText(getResources().getString(R.string.initial_time));

            // If this is the first time play the first workout data
            if(mIsFirstAppRun){
                int is = Integer.parseInt(mWorkoutIds.get(mCurrentData-1));
//                startTimer((mWorkoutTimes.get(is)));
                startTimer(mWorkoutTimes.get(mCurrentData-1));
                start_circuit_number = mCurrentData;

                mTxtTitle.setText("("+(mCurrentWorkout + 1) + "/" +
                        mWorkoutNames.size() + ") " + mWorkoutNames.get(mCurrentData - 1));
                mLytBreakLayout.setVisibility(View.GONE);
                mIsFirstAppRun = false;
            }else {
                // Otherwise check mCurrentData
                if (mCurrentData != (mWorkoutIds.size())) {
                    if (mIsBreak) {
                        // If it is break, play break time
                        takeABreak();
                    } else {
                            // If it is not break, play next workout and hide break layout view
                                mLytBreakLayout.setVisibility(View.GONE);
                                getNextWorkout();
                    }
                } else {

                        // Say that workouts has been completed
                        saySomething(getString(R.string.all_workouts_completed));
                        // Display dialog when workout finished, whether user want to end
                        // or share the workouts
                        new MaterialDialog.Builder(ActivityStopWatch.this)
                                .title(R.string.workout_completed)
                                .content(R.string.all_workouts_completed)
                                .positiveText(R.string.done)
                                .negativeText(R.string.share)
                                .positiveColorRes(R.color.primary_color)
                                .negativeColorRes(R.color.primary_color)
                                .contentColorRes(R.color.text_and_icon_color)
                                .backgroundColorRes(R.color.material_background_color)
                                .cancelable(false)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        // Turn off activity screen on
                                        getWindow().clearFlags(
                                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                        finish();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        String subject = getString(R.string.share_program_message) +
                                                " " + mProgramName + " " + getString(R.string.workouts) +
                                                " " + getString(R.string.program);
                                        String message = subject + ".\n\n" +
                                                getString(R.string.sent_via_message) + " " +
                                                getString(R.string.app_name) + ". " +
                                                getString(R.string.download) + " " +
                                                getString(R.string.app_name) +
                                                " " + getString(R.string.at) + " " +
                                                getString(R.string.google_play_url);
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                                        startActivity(Intent.createChooser(shareIntent,
                                                getResources().getString(R.string.share_to)));

                                    }
                                })
                                .show();

                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // Set current view pager with current workout
            mCirclePageIndicator.setCurrentItem(mCurrentWorkout);

            isRunning = true;

            mStart = mEnd;
            mEnd = mEnd + mStep;

            mCircularBarPager.getCircularBar().animateProgress(mStart, mEnd, INTERVAL);

            // Convert time format from millisUntilFinished to "00:00" format
            mTimer = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

            if(mIsFirstAppRun){
                mTxtBreakTime.setText(mTimer);
            }else{
                if(mIsBreak){
                    mTxtSubTitle.setText(mTimer);
                } else {
                    mTxtBreakTime.setText(mTimer);
                    mTxtSubTitle.setText(getResources().getString(R.string.initial_time));
                }
            }


            if (millisUntilFinished < mAlert && paramAlert==1){

                mMediaPlayer.start();

                if(mIsFirstAppRun){
                    saySomething(getString(R.string.first_step) + " " + mWorkoutNames.get(0));
                }

                if(!mIsBreak) {
                    if(mCurrentData == (mWorkoutIds.size() - 1)) {
                        saySomething(getString(R.string.last_step) + " " +
                                mWorkoutNames.get(mCurrentData));
                    }else{
                        saySomething(getString(R.string.next_step) + " " +
                                mWorkoutNames.get(mCurrentData));
                    }
                }

                paramAlert+=1;
            }
        }

        // Method to pause count down timer
        public String timerPause(){
            return mTimer;
        }

        // Method to check count down timer status
        public Boolean timerCheck(){
            return isRunning;
        }

    }

    // Method to convert message to speech
    private void saySomething(String message){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(message);
        } else {
            ttsUnder20(message);
        }
    }

    // Method to get the next workout
    private void getNextWorkout(){
        mCurrentWorkout += 1;
        mCurrentData += 1;
        if (mCurrentData > 1) {
                    mTxtTitle.setText("(" + mCurrentWorkout + "/" + mWorkoutNames.size() + ") " +
                            mWorkoutNames.get(mCurrentData - 1));
                    mIsPlay = true;
                    mIsBreak = true;
//                    int is = Integer.parseInt(mWorkoutIds.get(mCurrentData - 1));
                    startTimer(mWorkoutTimes.get(mCurrentData - 1));
        }

        mCirclePageIndicator.setCurrentItem(mCurrentData - 1);
    }

    // Method to set a break
    private void takeABreak(){
        mIsBreak = false;
        mLytBreakLayout.setVisibility(View.VISIBLE);
        startTimer(_str_break);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // If screen destroy then stop count down timer and turn off screen on
        Write_data();

        if(mCounter != null) {
            if (mCounter.timerCheck()) mCounter.cancel();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentWorkout = 0;
        mCurrentData = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        // If screen stop then stop count down timer and turn off screen on
        if(mCounter != null) {
            if (mCounter.timerCheck()) mCounter.cancel();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // If screen pause, pause the count down timer
        pauseWorkout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabPlay:
                // Condition when button start push
                if(!mIsPlay){
                    playWorkouts();
                } else {
                    pauseWorkout();
                }
            default:
                break;
        }
    }

    // Method to play workout
    public void playWorkouts(){
        mFabPlay.setIcon(R.mipmap.ic_pause_white_24dp);
        mIsPlay = true;
        mCirclePageIndicator.setCurrentItem(mCurrentWorkout);

        if(mIsPause) {
            // re-play paused workout
            mCounter.cancel();
            startTimer(mCurrentTime);
        } else {
            // Start ready timer
            mLytBreakLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsGreater21(getString(R.string.beginning_workout));
            } else {
                ttsUnder20(getString(R.string.beginning_workout));
            }
            startTimer(_str_break);
        }
    }

    // Method to pause workout
    public void pauseWorkout(){
        mStart = 0;
        mEnd = 0;
        mIsPlay = false;
        mIsPause = true;
        mFabPlay.setIcon(R.mipmap.ic_play_arrow_white_24dp);
        if(mCounter != null) {
            if (mCounter.timerCheck()) mCounter.cancel();
            mCurrentTime = mCounter.timerPause();
        }
        if(mIsFirstAppRun){
            mTxtBreakTime.setText(mCurrentTime);
        }else {
            if (mIsBreak) {
                // If this is break, set mTimer to break time view and set sub title view
                // with initial time
                mTxtBreakTime.setText(mCurrentTime);
                mTxtSubTitle.setText(mCurrentTime);
            } else {
                mTxtSubTitle.setText(mCurrentTime);
            }
        }
    }

    // Method to handle physical back button with animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Write_data();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    // perform setOnZoomInClickListener event on ZoomControls

  public void zoom_control()
  {
      simpleZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              _cnt++;
              int ss = _cnt / 60; int ss1 = _cnt % 60;
              _str_break = get_string(ss, ss1);
              mTxtBreakTime.setText(_str_break);
          }
      });
      // perform setOnZoomOutClickListener event on ZoomControls
      simpleZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              _cnt--;
              if(_cnt <= 0) _cnt = 0;

              int ss = _cnt / 60; int ss1 = _cnt % 60;
              _str_break = get_string(ss, ss1);
              mTxtBreakTime.setText(_str_break);
          }
      });
  }

    private static int getInt_string(String time){

        String[] splitTime = time.split(":");

        int splitMinute = Integer.valueOf(splitTime[0]);
        int splitSecond = Integer.valueOf(splitTime[1]);

        int msec = (int) (((splitMinute * 60) + splitSecond));

        return msec;
    }
    private static String get_string(int time, int time1)
    {
        NumberFormat numberFormat  = new DecimalFormat("##00");
        String str = numberFormat.format(time);         // -1235
        String str1 = numberFormat.format(time1);
        String ss = str + ":" + str1;
        return  ss;
    }

///////////////////////////////////////////////////
public void Read_data() {
    try {
        FileInputStream fileIn=openFileInput("savedata11.txt");
        InputStreamReader InputRead= new InputStreamReader(fileIn);

        char[] inputBuffer= new char[1024];
        String s="";
        int charRead;

        while ((charRead=InputRead.read(inputBuffer))>0) {
            // char to string conversion
            String readstring=String.copyValueOf(inputBuffer,0,charRead);
            s +=readstring;
        }
        _cnt = Integer.parseInt(s);
        InputRead.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // write text to file
    public void Write_data() {
        // add-write text into file
        try {
            FileOutputStream fileout=openFileOutput("savedata11.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(Integer.toString(_cnt));
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
