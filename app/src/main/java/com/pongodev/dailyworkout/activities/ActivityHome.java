package com.pongodev.dailyworkout.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.AdapterNavigation;
import com.pongodev.dailyworkout.fragments.FragmentPrograms;
import com.pongodev.dailyworkout.fragments.FragmentCategories;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 * <p/>
 * ActivityHome is created to display workout category and program in tab view.
 * Created using AppCompatActivity.
 */
public class ActivityHome extends AppCompatActivity implements
        FragmentCategories.OnSelectedCategoryListener,
        FragmentPrograms.OnSelectedDayListener {

    private DBHelperWorkouts mDbHelperPrograms;
    public ArrayList<String> str_data;
    public int sel_pos = -1;
    public ArrayList<String> save_data;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        save_data = new ArrayList<String>();
        for(int i = 0; i < 79; i++) save_data.add("00:30");

        ReadBtn();
        read_database_record();
        // Connect view objects with view ids in xml
        View mHeaderView = findViewById(R.id.header);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        // Set adapter object
        AdapterNavigation mAdapterNavigation =
                new AdapterNavigation(this, getSupportFragmentManager());

        // Set elevation to header view
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        // Set tab bar adapter to the pager
        mPager.setAdapter(mAdapterNavigation);
        // Set toolbar as actionbar
        setSupportActionBar(mToolbar);

        // Configure tab layout
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.
                getColor(this, R.color.accent_color));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mPager);

        // Handle item menu in toolbar
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuAbout:
                        // Open ActivityAbout
                        Intent aboutIntent = new Intent(getApplicationContext(),
                                ActivityAbout.class);
                        startActivity(aboutIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    default:
                        return true;
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_home, menu);
        return true;
    }

    @Override
    public void onSelectedCategory(String selectedID, String selectedName) {
        // On workout category selected open ActivityWorkouts and pass
        // selected values to that activity
        Intent detailIntent = new Intent(this, ActivityWorkouts.class);
        detailIntent.putExtra(Utils.ARG_WORKOUT_ID, selectedID);
        detailIntent.putExtra(Utils.ARG_WORKOUT_NAME, selectedName);
        detailIntent.putExtra(Utils.ARG_PARENT_PAGE, Utils.ARG_WORKOUTS);

        detailIntent.putExtra("Select_Pos", sel_pos);
        detailIntent.putStringArrayListExtra("array_list", str_data);

        startActivity(detailIntent);
        overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    @Override
    public void onSelectedDay(String selectedID, String selectedName) {
        // On day selected open ActivityWorkouts and pass
        // selected values to that activity
        Intent detailIntent = new Intent(this, ActivityWorkouts1.class);
        detailIntent.putExtra(Utils.ARG_WORKOUT_ID, selectedID);
        detailIntent.putExtra(Utils.ARG_WORKOUT_NAME, selectedName);
        detailIntent.putExtra(Utils.ARG_PARENT_PAGE, Utils.ARG_PROGRAMS);

        detailIntent.putExtra("Select_Pos", sel_pos);
        detailIntent.putStringArrayListExtra("array_list", str_data);

        startActivity(detailIntent);
        overridePendingTransition(R.anim.open_next, R.anim.close_main);

    }

    void read_database_record() {
        mDbHelperPrograms = new DBHelperWorkouts(this);

        // Create workout and program database
        try {
            mDbHelperPrograms.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        // Open workout and program database
        mDbHelperPrograms.openDataBase();

        str_data = new ArrayList<String>();
        ArrayList<ArrayList<Object>> data;

        // If ActivityWorkouts open via workouts tab then get workout list from workout database
       // mDbHelperPrograms.update_time_data("xx", "23:45");
        data = mDbHelperPrograms.getAll_Time();
        for(int i=0;i< data.size();i++){
            ArrayList<Object> row = data.get(i);
            String ss = row.get(0).toString();
            String kk = ss;
            str_data.add(ss);
        }
        str_data.clear(); str_data.addAll(save_data);
        //    Intent it = new Intent(this, ActivityHome.class);
    //  /  it.putStringArrayListExtra("array_list", str_data);
     //   startActivity(it);
       // mDbHelperPrograms.close();
    }

    // Read text from file
    public void ReadBtn() {
        try {
            FileInputStream fileIn=openFileInput("savedata.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[1024];
            String s="";
            int charRead; save_data.clear();

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            int i = s.indexOf(" "); int j = 0;
            while(i > 0){
                i = s.indexOf(" ");
                save_data.add(s.substring(0, i));

                j = i;
                s = s.substring(i+1);
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // write text to file
    public void WriteBtn() {
        // add-write text into file
        try {
            FileOutputStream fileout=openFileOutput("savedata.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);

            for(int i = 0; i < 79; i++) {
                outputWriter.write(str_data.get(i).toString());
                outputWriter.write(" ");
            }
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WriteBtn();
    }
}

