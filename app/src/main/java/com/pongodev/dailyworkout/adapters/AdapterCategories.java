package com.pongodev.dailyworkout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.activities.ActivityHome;
import com.pongodev.dailyworkout.listeners.OnTapListener;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.ImageLoader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * AdapterCategories is created to display workout item under workout category tab.
 * Created using RecyclerView.Adapter.
 */
public class AdapterCategories extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static ActivityHome home_activity;

    // Create variables to store data
    private final ArrayList<String> mCategoryIds;
    private final ArrayList<String> mCategoryNames;
    private final ArrayList<String> mCategoryImages;
    private final ArrayList<String> mTotalWorkouts;
    private static final ArrayList<String> mtotaltime=new ArrayList<>();

    public static final ArrayList<String> time_list = new ArrayList<>();
    public static int _ipos = -1;

    // Create listener object
    private OnTapListener onTapListener;

    // Create Context and ImageLoader objects
    private static Context mContext;
    private ImageLoader mImageLoader;

    // Create view and LayoutInflater objects
    private View mHeaderView;
    private LayoutInflater mInflater;

    public static int _str_time = 30;
    private static DBHelperWorkouts mMydatabase;

    // Constructor to set classes objects
    public AdapterCategories(Context context, View headerView)
    {
        this.mCategoryIds = new ArrayList<>();
        this.mCategoryNames = new ArrayList<>();
        this.mCategoryImages = new ArrayList<>();
        this.mTotalWorkouts = new ArrayList<>();

        mContext = context; home_activity = (ActivityHome)mContext;

        mHeaderView = headerView;

        mInflater = LayoutInflater.from(context);

        // Get image width and height sizes from dimens.xml
        int mImageWidth = mContext.getResources().getDimensionPixelSize(R.dimen.thumb_width);
        int mImageHeight = mContext.getResources().getDimensionPixelSize(R.dimen.thumb_height);

        mImageLoader = new ImageLoader(mContext, mImageWidth, mImageHeight);

    }

    @Override
    public int getItemCount() {
        // If header has been set, add +1 to arraylist variables size
        if (mHeaderView == null) {
            return mCategoryIds.size();
        } else {
            return mCategoryIds.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else {
            return new ItemViewHolder(mInflater.inflate(R.layout.adapter_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position)
    {
        if(viewHolder instanceof ItemViewHolder){
            ((ItemViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTapListener != null)
                        onTapListener.onTapView(mCategoryIds.get(position - 1),
                                mCategoryNames.get(position - 1));
                }
            });

            ((ItemViewHolder) viewHolder).mTxtWorkoutName.setText(mCategoryNames.get(position - 1));

            // Get total workout
            int count = Integer.parseInt(mTotalWorkouts.get(position - 1));

            // If total workout is more than one then add 's'
            if(count > 1){// ((ItemViewHolder) viewHolder).mTxtWorkoutNumber.setText(count+" "+mContext.getResources().getString(R.string.workouts));
            }else{  //((ItemViewHolder) viewHolder).mTxtWorkoutNumber.setText(count+" "+mContext.getResources().getString(R.string.workout));
            }

            ((ItemViewHolder) viewHolder).mTxtWorkoutNumber.setText(mtotaltime.get(position - 1));
         //   ((ItemViewHolder) viewHolder).mTxtWorkoutNumber.setVisibility(View.INVISIBLE);

            // Set image to ImageView
            int image = mContext.getResources().getIdentifier(mCategoryImages.get(position - 1),
                    "drawable", mContext.getPackageName());

            // Load image lazily
            mImageLoader.loadBitmap(image, ((ItemViewHolder) viewHolder).mImgCategoryImage);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        // Create view objects
        private RoundedImageView mImgCategoryImage;
        private TextView mTxtWorkoutName, mTxtWorkoutNumber;
        private Button m_btn_minus;
        private Button m_btn_plus;

        public ItemViewHolder(View view) {
            super(view);
            // Connect view objects with view ids in xml
            mImgCategoryImage   = (RoundedImageView) view.findViewById(R.id.imgThumbnail);
            mTxtWorkoutName     = (TextView) view.findViewById(R.id.txtPrimaryText);
            mTxtWorkoutNumber   = (TextView) view.findViewById(R.id.txtSecondaryText);
            m_btn_minus = (Button)view.findViewById(R.id.b_minus);
            m_btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int sk = getAdapterPosition(); _ipos = sk;

                    String ss = mTxtWorkoutNumber.getText().toString();
                    _str_time = getInt_string(ss);
                    _str_time = _str_time -1;
                    if(_str_time <= 0) _str_time = 0;

                    int sd = _str_time /60; int sr = _str_time % 60;
                    String ss1 = get_string(sd, sr);
                    mTxtWorkoutNumber.setText(ss1);
                    time_list.set(sk-1, ss1); mtotaltime.set(sk - 1, ss1); home_activity.str_data.set(sk-1, ss1);
                  //  read_database_record();
                }
            });
            m_btn_plus = (Button)view.findViewById(R.id.b_plus);
            m_btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int sk = getAdapterPosition(); _ipos = sk;
                    String ss = mTxtWorkoutNumber.getText().toString();

                    _str_time = getInt_string(ss);
                    _str_time++;
                    int sd = _str_time /60; int sr = _str_time % 60;
                    String ss1 = get_string(sd, sr);
                    mTxtWorkoutNumber.setText(ss1);
                    time_list.set(sk-1, ss1); mtotaltime.set(sk - 1, ss1); home_activity.str_data.set(sk-1, ss1);
                  //  read_database_record();
                }
            });
        }
    }

    // Method to set data to recyclerview item
    public void updateList(
            ArrayList<String> categoryIds,
            ArrayList<String> categoryNames,
            ArrayList<String> categoryImages,
            ArrayList<String> totalWorkouts,
            ArrayList<String> totaltime) {

        this.mCategoryIds.clear();
        this.mCategoryIds.addAll(categoryIds);

        this.mCategoryNames.clear();
        this.mCategoryNames.addAll(categoryNames);

        this.mCategoryImages.clear();
        this.mCategoryImages.addAll(categoryImages);

        this.mTotalWorkouts.clear();
        this.mTotalWorkouts.addAll(totalWorkouts);

        this.mtotaltime.clear(); time_list.clear();
        this.mtotaltime.addAll(totaltime); time_list.addAll(totaltime);

        this.notifyDataSetChanged();
    }

    // Method to set listener to handle item click
    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
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

   private static void read_database_record() {
        mMydatabase = new DBHelperWorkouts(mContext);

        // Create workout and program database
        try {
            mMydatabase.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        // Open workout and program database
        mMydatabase.openDataBase();

        // If ActivityWorkouts open via workouts tab then get workout list from workout database
        mMydatabase.update_time_data("7", "24:45");

       ArrayList<ArrayList<Object>> data = mMydatabase.getAll_Time();
        mMydatabase.close();
    }
}