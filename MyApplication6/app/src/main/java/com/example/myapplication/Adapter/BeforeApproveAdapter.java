package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.Ba_Intro;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class BeforeApproveAdapter extends BaseAdapter {
    ArrayList<String> balist = new ArrayList<>();

    @Override
    public int getCount() {
        return balist.size();
    }

    @Override
    public String getItem(int position) {
        return balist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_ba, null);
        }

        TextView tv_groupName = convertView.findViewById(R.id.tv_groupName);
        String teamName = getItem(position);
        tv_groupName.setText(teamName);

        return convertView;
    }

    public void addItem(String name) {
        String teamItem = name;
        balist.add(teamItem);
    }
    public void removeItem(int pos){
        balist.remove(pos);
    }

}

