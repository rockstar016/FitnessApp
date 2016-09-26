package com.pongodev.dailyworkout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.listeners.OnTapListener;

import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * AdapterPrograms is created to display day item under program tab.
 * Created using RecyclerView.Adapter.
 */
public class AdapterPrograms extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    // Create variables to store data
    private final ArrayList<String> mDayIds;
    private final ArrayList<String> mDayNames;
    private final ArrayList<String> mTotalPrograms;

    // Create listener object
    private OnTapListener onTapListener;

    // Create Context object
    private Context mContext;

    // Create view and LayoutInflater objects
    private View mHeaderView;
    private LayoutInflater mInflater;

    // Constructor to set classes objects
    public AdapterPrograms(Context context, View headerView) {
        this.mDayIds = new ArrayList<>();
        this.mDayNames = new ArrayList<>();
        this.mTotalPrograms = new ArrayList<>();

        mContext = context;

        mHeaderView = headerView;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        // If header has been set, add +1 to arraylist variables size
        if (mHeaderView == null) {
            return mDayIds.size();
        } else {
            return mDayIds.size() + 1;
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
            return new ItemViewHolder(mInflater.inflate(R.layout.adapter_programs, parent, false));
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
                        onTapListener.onTapView(mDayIds.get(position - 1),
                                mDayNames.get(position - 1));
                }
            });

            // Set data to text view
            ((ItemViewHolder) viewHolder).mTxtDayName.setText(mDayNames.get(position - 1));

            // Get total workout
            int count = Integer.parseInt(mTotalPrograms.get(position - 1));

            // If workout is more than one then add 's'
            if(count > 1){
                ((ItemViewHolder) viewHolder).mTxtWorkoutNumber.setText(count+" exercises");
            }else{
                ((ItemViewHolder) viewHolder).mTxtWorkoutNumber.setText(count+" exercise");
            }
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        // Create view objects
        private TextView mTxtDayName, mTxtWorkoutNumber;

        public ItemViewHolder(View view) {
            super(view);
            // Set view objects with view ids in xml
            mTxtDayName          = (TextView) view.findViewById(R.id.txtDayName);
            mTxtWorkoutNumber    = (TextView) view.findViewById(R.id.txtWorkoutNumber);
        }
    }

    // Method to set data to recyclerview item
    public void updateList(
            ArrayList<String> dayIds,
            ArrayList<String> dayNames,
            ArrayList<String> totalPrograms)
    {
        this.mDayIds.clear();
        this.mDayIds.addAll(dayIds);

        this.mDayNames.clear();
        this.mDayNames.addAll(dayNames);

        this.mTotalPrograms.clear();
        this.mTotalPrograms.addAll(totalPrograms);

        this.notifyDataSetChanged();
    }

    // Method to set listener to handle item click
    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
    }
}