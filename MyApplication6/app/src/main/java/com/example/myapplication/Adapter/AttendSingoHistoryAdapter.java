package com.example.myapplication.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.AttendSingoList;
import com.example.myapplication.ObjSingoList;
import com.example.myapplication.R;

import java.util.ArrayList;

public class AttendSingoHistoryAdapter extends BaseAdapter {
    ArrayList<AttendSingoList> lists = new ArrayList<>();

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public AttendSingoList getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_attend_singo_history, null);
        }
        TextView tv_name = convertView.findViewById(R.id.tv_name_ash);
        TextView tv_user = convertView.findViewById(R.id.tv_user_ash);
        TextView tv_date = convertView.findViewById(R.id.tv_date_ash);

        tv_name.setText(lists.get(position).getName());
        tv_user.setText(lists.get(position).getUser());
        tv_date.setText(lists.get(position).getDate());

        return convertView;
    }

    public void addItem(String name, String date, String user) {
        AttendSingoList attendSingoList = new AttendSingoList(name, date, user);
        lists.add(attendSingoList);
    }

    public void removeItem(int pos) {
        lists.remove(pos);
    }
}