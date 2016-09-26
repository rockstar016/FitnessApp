package com.pongodev.dailyworkout.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mrengineer13.snackbar.SnackBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.activities.ActivityWorkouts;
import com.pongodev.dailyworkout.activities.ActivityWorkouts1;
import com.pongodev.dailyworkout.listeners.OnTapListener;
import com.pongodev.dailyworkout.models.ProgramsHeaderModel;
import com.pongodev.dailyworkout.models.ProgramsModel;
import com.pongodev.dailyworkout.utils.DBHelperPrograms;
import com.pongodev.dailyworkout.utils.ImageLoader;
import com.pongodev.dailyworkout.utils.Utils;
import com.pongodev.dailyworkout.views.DaySelectDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Design and developed by pongodev.com
 *
 * AdapterWorkouts is created to display workout item in ActivityWorkouts.
 * Created using RecyclerView.Adapter.
 */
public class AdapterWorkouts1 extends BaseExpandableListAdapter
{
    private List<ProgramsHeaderModel> programHeaderList;
    private HashMap<ProgramsHeaderModel, List<ProgramsModel>> programItemList;
    // Create listener object
    private OnTapListener onTapListener;

    private Activity mActivity;

    // Create object of database helper class
    private DBHelperPrograms mDbHelperPrograms;
    // Create ImageLoader class object
    private ImageLoader mImageLoader;

    // Create variables to store day data and dimension
    private String[] mDays;
    private String mSelectedDay;
//    private static String sParentPage;
    public int ss_pos = -1;
    public boolean ff = false;

    public void setProgramHeaderList(List<ProgramsHeaderModel> programHeaderList) {
        this.programHeaderList = programHeaderList;
    }

    public void setProgramItemList(HashMap<ProgramsHeaderModel, List<ProgramsModel>> programItemList) {
        this.programItemList = programItemList;
        this.notifyDataSetChanged();
    }

    // Constructor to set data and class objects
    public AdapterWorkouts1(Activity activity, String selectedDay, String parentPage,
                            DBHelperPrograms dbHelperPrograms) {
        mActivity = activity;
        ActivityWorkouts1 wa = (ActivityWorkouts1) activity;
        ss_pos = wa.mSel_pos;
        // Set database object
        mDbHelperPrograms = dbHelperPrograms;
        mSelectedDay = selectedDay;

        // Get day list from strings.xml
        mDays = mActivity.getResources().getStringArray(R.array.day_names);
        // Get image width and height sizes from dimens.xml
        int mImageWidth = mActivity.getResources().getDimensionPixelSize(R.dimen.thumb_width);
        int mImageHeight = mActivity.getResources().getDimensionPixelSize(R.dimen.thumb_height);

        // Set image loader object
        mImageLoader = new ImageLoader(mActivity, mImageWidth, mImageHeight);

    }
    public ArrayList<String> getData(int idx){
        ArrayList<String> array_return = new ArrayList<String>();

                    if(idx == 0){
                        //workout id
                        for(int header_idx = 0 ; header_idx < programHeaderList.size(); header_idx++){
                            for(int round_idx = 0 ; round_idx < Integer.parseInt(programHeaderList.get(header_idx).getRoundNumber()) ; round_idx++){
                                for (int item_idx = 0; item_idx < getChildrenCount(header_idx); item_idx++) {
                                    array_return.add(getChild(header_idx, item_idx).getWorkId() + "");
                                }
                            }
                        }
                    }
                    else if(idx == 1){
                        //workout name
                        for(int header_idx = 0 ; header_idx < programHeaderList.size(); header_idx++) {
                            for (int round_idx = 0; round_idx < Integer.parseInt(programHeaderList.get(header_idx).getRoundNumber()); round_idx++) {
                                for (int item_idx = 0; item_idx < getChildrenCount(header_idx); item_idx++) {
                                    array_return.add(getChild(header_idx, item_idx).getWorkOutName());
                                }
                            }
                        }
                    }
                    else if(idx == 2){
                        //workout image
                        for(int header_idx = 0 ; header_idx < programHeaderList.size(); header_idx++) {
                            for (int round_idx = 0; round_idx < Integer.parseInt(programHeaderList.get(header_idx).getRoundNumber()); round_idx++) {
                                for (int item_idx = 0; item_idx < getChildrenCount(header_idx); item_idx++) {
                                    array_return.add(getChild(header_idx, item_idx).getWorkOutImage());
                                }
                            }
                        }
                    }
                    else if(idx == 3){
                        //workout times
                        for(int header_idx = 0 ; header_idx < programHeaderList.size(); header_idx++){
                            for(int round_idx = 0 ; round_idx < Integer.parseInt(programHeaderList.get(header_idx).getRoundNumber()) ; round_idx++){
                                for(int item_idx = 0 ; item_idx < getChildrenCount(header_idx); item_idx++) {
                                    array_return.add(getChild(header_idx,item_idx).getWorkOutTimes());
                                }
                            }
                        }
                    }

        return array_return;
    }
    @Override
    public int getGroupCount() {
        try {
            return programHeaderList.size();
        }
        catch(Exception e){
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int i) {
        try {
            return this.programItemList.get(programHeaderList.get(i)).size();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public ProgramsHeaderModel getGroup(int i) {
        return programHeaderList.get(i);
    }

    @Override
    public ProgramsModel getChild(int groupId, int childId) {
        return this.programItemList.get(this.programHeaderList.get(groupId)).get(childId);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final ProgramsHeaderModel headerModel = getGroup(i);
        if(view == null){
            LayoutInflater infalInflater = (LayoutInflater) this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expand_header_programs, null);
        }
        TextView txt_header = (TextView)view.findViewById(R.id.program_circuit_id);
        final TextView txt_item = (TextView)view.findViewById(R.id.program_circuit_round);
        txt_header.setText("Circuit " + (Integer.parseInt(headerModel.getCircuitNumber()) + 1));
        txt_item.setText(headerModel.getRoundNumber() + " Rounds");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_rounds = Integer.parseInt(headerModel.getRoundNumber());
                current_rounds += 1;
                if(current_rounds == 4) {
                    current_rounds = 1;
                }

                headerModel.setRoundNumber(current_rounds + "");
                txt_item.setText(current_rounds + " Rounds");
                notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {
        final ProgramsModel itemModel = getChild(groupPosition,childPosition);;
        if(view == null){
            LayoutInflater infalInflater = (LayoutInflater) this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expand_item_programs, null);
        }
        TextView txtPrimaryText = (TextView)view.findViewById(R.id.txtPrimaryText);
        TextView txtSecondaryText = (TextView)view.findViewById(R.id.txtSecondaryText);
        ImageView mImgThumbnail = (ImageView)view.findViewById(R.id.imgThumbnail);
        RelativeLayout btnAction = (RelativeLayout)view.findViewById(R.id.btnAction);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showAlertDialog(groupPosition,childPosition, mSelectedDay);
            }
        });
        int image = mActivity.getResources().getIdentifier(itemModel.getWorkOutImage(),
                "drawable", mActivity.getPackageName());
        // Load image lazily
        mImageLoader.loadBitmap(image, mImgThumbnail);
        txtPrimaryText.setText(itemModel.getWorkOutName());
        txtSecondaryText.setText(itemModel.getWorkOutTimes());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTapListener != null) {
                    String work_id = String.format("%d",itemModel.getWorkId());
                    onTapListener.onTapView(work_id,itemModel.getWorkOutName());
                }
            }
        });
        return view;
    }
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    // Method to create alert dialog
    public void showAlertDialog(final int groupPosition, final int childPosition, final String selectedDay){
        final String day = mDays[(Integer.valueOf(selectedDay) - 1)];
        String confirmMessage = mActivity.getString(R.string.confirm_message)+" "+
                day+" "+mActivity.getString(R.string.program) + "?";
        new MaterialDialog.Builder(mActivity)
                .title(R.string.confirm)
                .content(confirmMessage)
                .positiveText(R.string.remove)
                .negativeText(R.string.cancel)
                .cancelable(false)
                .positiveColorRes(R.color.primary_color)
                .negativeColorRes(R.color.primary_color)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ProgramsModel item = getChild(groupPosition,childPosition);
                        ProgramsHeaderModel header = getGroup(groupPosition);
                        boolean result = mDbHelperPrograms.deleteWorkoutFromDay(item.getProgId());

                        if (result) {
                            // Remove data from recyclerview item
                            removeAt(groupPosition,childPosition);
                            if(getChildrenCount(groupPosition) == 0) {
                                programItemList.remove(programHeaderList.get(groupPosition));
                                programHeaderList.remove(groupPosition);
                            }
                            else {
                                programHeaderList.get(groupPosition).setRoundNumber(getChildrenCount(groupPosition) + "");
                            }
                            notifyDataSetChanged();

                            new SnackBar.Builder(mActivity)
                                    .withMessage(mActivity.getString(R.string.success_remove) + " " +
                                            day + " " + mActivity.getString(R.string.program))
                                    .show();
                            dialog.dismiss();
                        }
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // Method to show snackbar message
    public void showSnackbar(String message){
        new SnackBar.Builder(mActivity)
                .withMessage(message)
                .show();
    }
    // Method to remove data from arraylist variables
    public void removeAt(int groupPosition, int ChildPosition) {
        programItemList.get(getGroup(groupPosition)).remove(ChildPosition);
    }
    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
    }
}