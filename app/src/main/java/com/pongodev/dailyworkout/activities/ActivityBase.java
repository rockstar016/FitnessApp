package com.pongodev.dailyworkout.activities;

import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.pongodev.dailyworkout.R;

/**
 * Design and developed by pongodev.com
 *
 * ActivityBase is created to handle scrolling page event.
 * Modified from ObservableScrollView library.
 */
public abstract class ActivityBase extends AppCompatActivity {

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

}