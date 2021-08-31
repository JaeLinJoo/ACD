package com.example.myapplication.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.ObjSingoList;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ObjSingoHistoryAdapter extends BaseAdapter {
    ArrayList<ObjSingoList> lists = new ArrayList<>();

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public ObjSingoList getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_obj_singo_history, null);
        }
        TextView tv_name = convertView.findViewById(R.id.tv_name_osh);
        TextView tv_id = convertView.findViewById(R.id.tv_id_osh);
        TextView tv_obj = convertView.findViewById(R.id.tv_obj_osh);

        tv_name.setText(lists.get(position).getName());
        tv_id.setText(lists.get(position).getId());
        tv_obj.setText(lists.get(position).getObjective());

        return convertView;
    }

    public void addItem(String name, String id, String obj) {
        ObjSingoList objSingoList = new ObjSingoList(name, id, obj);
        lists.add(objSingoList);
    }

    public void removeItem(int pos) {
        lists.remove(pos);
    }
}