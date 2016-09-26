package com.pongodev.dailyworkout.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * ViewWorkout is created to create view for each view pager.
 * Created using LinearLayout.
 */
public class ViewWorkout extends LinearLayout {

    // Create view object
    private ViewFlipper mFlipper;

    // Create context and image loader class objects
    private Context mContext;
    private ImageLoader mImageLoader;

    // Create variables to store dimension values
    private int mImageWidth, mImageHeight;

    // Create arraylist variable to store image data
    private ArrayList<String> mImages;

    public ViewWorkout(Context context, ArrayList<String> images) {
        super(context);

        mContext        = context;
        mImages         = images;

        // Get dimension from dimens.xml
        mImageWidth     = mContext.getResources()
                .getDimensionPixelSize(R.dimen.image_flipper_width_and_height);
        mImageHeight    = mContext.getResources()
                .getDimensionPixelSize(R.dimen.image_flipper_width_and_height);

        // Set image loader class object
        mImageLoader    = new ImageLoader(mContext, mImageWidth, mImageHeight);

        initView();
    }

    // Method to initialize views
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Connect view objects with view ids in xml
        final View v            =  inflater.inflate(R.layout.pager_adapter_workout, this);
        mFlipper                = (ViewFlipper) v.findViewById(R.id.flipper);

        // Set flipper animation
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in_out));
        mFlipper.setFlipInterval(1000);
        if(!mFlipper.isFlipping()) mFlipper.startFlipping();

        for (int i = 0; i < mImages.size(); i++) {
            addImageToFlipper(mImages.get(i));
        }

    }

    // method to add image to flipper
    public void addImageToFlipper(String images){
        FrameLayout fl = new FrameLayout(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.image_flipper_width_and_height),
                getResources().getDimensionPixelSize(R.dimen.image_flipper_width_and_height));

        fl.setLayoutParams(lp);

        RoundedImageView imgWorkout = new RoundedImageView(mContext);
        imgWorkout.mutateBackground(true);
        imgWorkout.setOval(true);
        imgWorkout.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int image = getResources().getIdentifier(images, "drawable", mContext.getPackageName());

        // Load image lazily
        mImageLoader.loadBitmap(image, imgWorkout);
        fl.addView(imgWorkout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mFlipper.addView(fl);
    }

}