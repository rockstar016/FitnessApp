package com.pongodev.dailyworkout.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.ExtensibleListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by RockStar-0116 on 2016.07.19.
 */
public class DaySelectDialog extends Dialog {
    Context _this;
    Button m_bt_yes, m_bt_no;
    ExpandableListView m_list_expandable;
    View.OnClickListener m_listener_ok, m_listener_cancel;
    ExtensibleListAdapter list_adapter;

    private List<String> headerList;
    private HashMap<String,List<String>> itemList;

    public DaySelectDialog(Context context){
        super(context);
        _this = context;
    }
    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.days_dialog);
        m_list_expandable = (ExpandableListView)findViewById(R.id.expandableListView);
        initAdapter();
        list_adapter = new ExtensibleListAdapter(_this,headerList,itemList);
        m_list_expandable.setAdapter(list_adapter);
        m_list_expandable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                ListCollapseAll(i);
                list_adapter.setOnCurrentGroupPosition(i);

                list_adapter.notifyDataSetChanged();
            }
        });
        m_bt_yes = (Button)findViewById(R.id.dlg_add_button);
        m_bt_no = (Button)findViewById(R.id.dlg_cancel_button);
        m_bt_yes.setOnClickListener(m_listener_ok);
        m_bt_no.setOnClickListener(m_listener_cancel);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
    private void ListCollapseAll(int idx){
        for(int i = 0 ; i < list_adapter.getGroupCount(); i++) {
            if(i != idx)
                m_list_expandable.collapseGroup(i);
        }
    }
    private void initAdapter(){
        headerList = Arrays.asList(_this.getResources().getStringArray(R.array.day_names));
        itemList = new HashMap<>();
        List<String> item_array = new ArrayList<String>();
        item_array.add("Circuit1");
        item_array.add("Circuit2");
        item_array.add("Circuit3");

        for(int i = 0 ; i < headerList.size();i++){
            itemList.put(headerList.get(i).toString(),item_array);
        }

    }
    public void setButtonClickListener(View.OnClickListener m_listener_ok, View.OnClickListener m_listener_cancel){
        this.m_listener_ok = m_listener_ok;
        this.m_listener_cancel = m_listener_cancel;
    }
    public int getCurrentGroupIndex(){
        return list_adapter.getCurrentGroupPosition();
    }
    public int getCurrentItemIndex(){
        return  list_adapter.getCurrentChildPosition();
    }
}
