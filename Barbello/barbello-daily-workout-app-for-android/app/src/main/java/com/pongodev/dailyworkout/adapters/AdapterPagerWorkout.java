package com.pongodev.dailyworkout.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Design and developed by pongodev.com
 *
 * AdapterPagerWorkout is created to add view inside CircularBarPager.
 * Created using PagerAdapter.
 */
public class AdapterPagerWorkout extends PagerAdapter {

    private View[] mViews;

    public AdapterPagerWorkout(View... views) {
        this.mViews = views;
    }

    @Override
    public int getCount() {
        return mViews.length;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View currentView = mViews[position];
        collection.addView(currentView);
        return currentView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}