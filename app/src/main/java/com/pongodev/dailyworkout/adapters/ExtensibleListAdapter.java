package com.pongodev.dailyworkout.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;

import com.pongodev.dailyworkout.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by RockStar0116 on 2016.09.13.
 */
public class ExtensibleListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> header_list;
    private java.util.HashMap<String, List<String>> item_list;
    int current_group_position, current_child_position;
    public ExtensibleListAdapter(Context context, List<String> header, HashMap<String,List<String>> items){
        this._context = context;
        this.header_list = header;
        this.item_list = items;
        current_child_position = current_group_position = -1;
    }
    @Override
    public int getGroupCount() {
        return header_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.item_list.get(header_list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.header_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.item_list.get(this.header_list.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header_item = (String)getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_list_header, null);
        }
        RadioButton header_list_option = (RadioButton) convertView.findViewById(R.id.header_list_option);
        header_list_option.setChecked(groupPosition == current_group_position);
        header_list_option.setText(header_item);
        return convertView;
    }
    public void setOnCurrentGroupPosition(int position){
        current_group_position = position;
        current_child_position = -1;
    }
    public int getCurrentGroupPosition(){
        return current_group_position;

    }
    public int getCurrentChildPosition(){
        return current_child_position;
    }
    /////////////////
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String item_model = (String)getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_list_item, null);
        }
        RadioButton item_list_option = (RadioButton)convertView.findViewById(R.id.item_list_option);
        item_list_option.setText(item_model);
        item_list_option.setChecked((childPosition == current_child_position) && (groupPosition == groupPosition));
        item_list_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_child_position = childPosition;
                current_group_position = groupPosition;
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
